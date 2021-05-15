package com.github.wangji92.rpc.annotation;

import org.springframework.core.ExceptionDepthComparator;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Classes for handling exception messages in query classes
 * copy from {@link org.springframework.web.method.annotation.ExceptionHandlerMethodResolver}
 *
 * @author 汪小哥
 * @date 14-05-2021
 */
public class RpcServiceExceptionHandlerMethodResolver {
    /**
     * A filter for selecting {@code @DubboExceptionHandler} methods.
     */
    public static final ReflectionUtils.MethodFilter EXCEPTION_HANDLER_METHODS = method ->
            AnnotatedElementUtils.hasAnnotation(method, RpcServiceExceptionHandler.class);

    private static final Method NO_MATCHING_EXCEPTION_HANDLER_METHOD;

    static {
        try {
            NO_MATCHING_EXCEPTION_HANDLER_METHOD =
                    RpcServiceExceptionHandlerMethodResolver.class.getDeclaredMethod("noMatchingExceptionHandler");
        } catch (NoSuchMethodException ex) {
            throw new IllegalStateException("Expected method not found: " + ex);
        }
    }


    private final Map<Class<? extends Throwable>, Method> mappedMethods = new HashMap<>(16);

    private final Map<Class<? extends Throwable>, Method> exceptionLookupCache = new ConcurrentReferenceHashMap<>(16);


    /**
     * A constructor that finds {@link RpcServiceExceptionHandler} methods in the given type.
     *
     * @param handlerType the type to introspect
     */
    public RpcServiceExceptionHandlerMethodResolver(Class<?> handlerType) {
        for (Method method : MethodIntrospector.selectMethods(handlerType, EXCEPTION_HANDLER_METHODS)) {
            for (Class<? extends Throwable> exceptionType : detectExceptionMappings(method)) {
                addExceptionMapping(exceptionType, method);
            }
        }
    }


    /**
     * Extract exception mappings from the {@code @ExceptionHandler} annotation first,
     * and then as a fallback from the method signature itself.
     */
    @SuppressWarnings("unchecked")
    private List<Class<? extends Throwable>> detectExceptionMappings(Method method) {
        List<Class<? extends Throwable>> result = new ArrayList<>();
        detectAnnotationExceptionMappings(method, result);
        if (result.isEmpty()) {
            for (Class<?> paramType : method.getParameterTypes()) {
                if (Throwable.class.isAssignableFrom(paramType)) {
                    result.add((Class<? extends Throwable>) paramType);
                }
            }
        }
        if (result.isEmpty()) {
            throw new IllegalStateException("No exception types mapped to " + method);
        }
        return result;
    }

    private void detectAnnotationExceptionMappings(Method method, List<Class<? extends Throwable>> result) {
        RpcServiceExceptionHandler ann = AnnotatedElementUtils.findMergedAnnotation(method, RpcServiceExceptionHandler.class);
        Assert.state(ann != null, "No RpcServiceExceptionHandler annotation");
        result.addAll(Arrays.asList(ann.value()));
    }

    private void addExceptionMapping(Class<? extends Throwable> exceptionType, Method method) {
        Method oldMethod = this.mappedMethods.put(exceptionType, method);
        if (oldMethod != null && !oldMethod.equals(method)) {
            throw new IllegalStateException("Ambiguous @RpcServiceExceptionHandler method mapped for [" +
                    exceptionType + "]: {" + oldMethod + ", " + method + "}");
        }
    }

    /**
     * Whether the contained type has any exception mappings.
     */
    public boolean hasExceptionMappings() {
        return !this.mappedMethods.isEmpty();
    }

    /**
     * Find a {@link Method} to handle the given exception.
     * <p>Uses {@link ExceptionDepthComparator} if more than one match is found.
     *
     * @param exception the exception
     * @return a Method to handle the exception, or {@code null} if none found
     */
    @Nullable
    public Method resolveMethod(Exception exception) {
        return resolveMethodByThrowable(exception);
    }

    /**
     * Find a {@link Method} to handle the given Throwable.
     * <p>Uses {@link ExceptionDepthComparator} if more than one match is found.
     *
     * @param exception the exception
     * @return a Method to handle the exception, or {@code null} if none found
     * @since 5.0
     */
    @Nullable
    public Method resolveMethodByThrowable(Throwable exception) {
        Method method = resolveMethodByExceptionType(exception.getClass());
        if (method == null) {
            Throwable cause = exception.getCause();
            if (cause != null) {
                method = resolveMethodByThrowable(cause);
            }
        }
        return method;
    }

    /**
     * Find a {@link Method} to handle the given exception type. This can be
     * useful if an {@link Exception} instance is not available (e.g. for tools).
     * <p>Uses {@link ExceptionDepthComparator} if more than one match is found.
     *
     * @param exceptionType the exception type
     * @return a Method to handle the exception, or {@code null} if none found
     */
    @Nullable
    public Method resolveMethodByExceptionType(Class<? extends Throwable> exceptionType) {
        Method method = this.exceptionLookupCache.get(exceptionType);
        if (method == null) {
            method = getMappedMethod(exceptionType);
            this.exceptionLookupCache.put(exceptionType, method);
        }
        return (method != NO_MATCHING_EXCEPTION_HANDLER_METHOD ? method : null);
    }

    /**
     * Return the {@link Method} mapped to the given exception type, or
     * {@link #NO_MATCHING_EXCEPTION_HANDLER_METHOD} if none.
     */
    private Method getMappedMethod(Class<? extends Throwable> exceptionType) {
        List<Class<? extends Throwable>> matches = new ArrayList<>();
        for (Class<? extends Throwable> mappedException : this.mappedMethods.keySet()) {
            if (mappedException.isAssignableFrom(exceptionType)) {
                matches.add(mappedException);
            }
        }
        if (!matches.isEmpty()) {
            if (matches.size() > 1) {
                matches.sort(new ExceptionDepthComparator(exceptionType));
            }
            return this.mappedMethods.get(matches.get(0));
        } else {
            return NO_MATCHING_EXCEPTION_HANDLER_METHOD;
        }
    }

    /**
     * For the {@link #NO_MATCHING_EXCEPTION_HANDLER_METHOD} constant.
     */
    @SuppressWarnings("unused")
    private void noMatchingExceptionHandler() {
    }
}
