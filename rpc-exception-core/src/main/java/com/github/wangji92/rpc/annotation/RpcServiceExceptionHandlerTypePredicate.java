package com.github.wangji92.rpc.annotation;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.function.Predicate;

/**
 * copy from {@linkplain org.springframework.web.method.HandlerTypePredicate}
 *
 * @author 汪小哥
 * @date 14-05-2021
 */
public class RpcServiceExceptionHandlerTypePredicate implements Predicate<Class<?>> {

    private final Set<String> basePackages;

    private final List<Class<?>> assignableTypes;

    private final List<Class<? extends Annotation>> annotations;

    /**
     * Private constructor. See static factory methods.
     */
    private RpcServiceExceptionHandlerTypePredicate(Set<String> basePackages, List<Class<?>> assignableTypes,
                                                    List<Class<? extends Annotation>> annotations) {

        this.basePackages = Collections.unmodifiableSet(basePackages);
        this.assignableTypes = Collections.unmodifiableList(assignableTypes);
        this.annotations = Collections.unmodifiableList(annotations);
    }


    @Override
    public boolean test(@Nullable Class<?> controllerType) {
        if (!hasSelectors()) {
            return true;
        } else if (controllerType != null) {
            for (String basePackage : this.basePackages) {
                if (controllerType.getName().startsWith(basePackage)) {
                    return true;
                }
            }
            for (Class<?> clazz : this.assignableTypes) {
                if (ClassUtils.isAssignable(clazz, controllerType)) {
                    return true;
                }
            }
            for (Class<? extends Annotation> annotationClass : this.annotations) {
                if (AnnotationUtils.findAnnotation(controllerType, annotationClass) != null) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasSelectors() {
        return (!this.basePackages.isEmpty() || !this.assignableTypes.isEmpty() || !this.annotations.isEmpty());
    }


    // Static factory methods

    /**
     * {@code Predicate} that applies to any handlers.
     */
    public static RpcServiceExceptionHandlerTypePredicate forAnyHandlerType() {
        return new RpcServiceExceptionHandlerTypePredicate(
                Collections.emptySet(), Collections.emptyList(), Collections.emptyList());
    }

    /**
     * Match handlers declared under a base package, e.g. "org.example".
     *
     * @param packages one or more base package names
     */
    public static RpcServiceExceptionHandlerTypePredicate forBasePackage(String... packages) {
        return new Builder().basePackage(packages).build();
    }

    /**
     * Type-safe alternative to {@link #forBasePackage(String...)} to specify a
     * base package through a class.
     *
     * @param packageClasses one or more base package classes
     */
    public static RpcServiceExceptionHandlerTypePredicate forBasePackageClass(Class<?>... packageClasses) {
        return new Builder().basePackageClass(packageClasses).build();
    }

    /**
     * Match handlers that are assignable to a given type.
     *
     * @param types one or more handler super types
     */
    public static RpcServiceExceptionHandlerTypePredicate forAssignableType(Class<?>... types) {
        return new Builder().assignableType(types).build();
    }

    /**
     * Match handlers annotated with a specific annotation.
     *
     * @param annotations one or more annotations to check for
     */
    @SafeVarargs
    public static RpcServiceExceptionHandlerTypePredicate forAnnotation(Class<? extends Annotation>... annotations) {
        return new Builder().annotation(annotations).build();
    }

    /**
     * Return a builder for a {@code HandlerTypePredicate}.
     */
    public static Builder builder() {
        return new Builder();
    }


    /**
     * A  builder.
     */
    public static class Builder {

        private final Set<String> basePackages = new LinkedHashSet<>();

        private final List<Class<?>> assignableTypes = new ArrayList<>();

        private final List<Class<? extends Annotation>> annotations = new ArrayList<>();

        /**
         * Match handlers declared under a base package, e.g. "org.example".
         *
         * @param packages one or more base package classes
         */
        public Builder basePackage(String... packages) {
            Arrays.stream(packages).filter(StringUtils::hasText).forEach(this::addBasePackage);
            return this;
        }

        /**
         * Type-safe alternative to {@link #forBasePackage(String...)} to specify a
         * base package through a class.
         *
         * @param packageClasses one or more base package names
         */
        public Builder basePackageClass(Class<?>... packageClasses) {
            Arrays.stream(packageClasses).forEach(clazz -> addBasePackage(ClassUtils.getPackageName(clazz)));
            return this;
        }

        private void addBasePackage(String basePackage) {
            this.basePackages.add(basePackage.endsWith(".") ? basePackage : basePackage + ".");
        }

        /**
         * Match handlers that are assignable to a given type.
         *
         * @param types one or more handler super types
         */
        public Builder assignableType(Class<?>... types) {
            this.assignableTypes.addAll(Arrays.asList(types));
            return this;
        }

        /**
         * Match types that are annotated with one of the given annotations.
         *
         * @param annotations one or more annotations to check for
         */
        @SuppressWarnings("unchecked")
        public final Builder annotation(Class<? extends Annotation>... annotations) {
            this.annotations.addAll(Arrays.asList(annotations));
            return this;
        }

        public RpcServiceExceptionHandlerTypePredicate build() {
            return new RpcServiceExceptionHandlerTypePredicate(this.basePackages, this.assignableTypes, this.annotations);
        }
    }
}
