package com.github.wangji92.dubbo.annotation;

import com.github.wangji92.dubbo.utils.ProviderInvokerTargetUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcContext;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@literal ExceptionHandlerExceptionResolver}
 *
 * @author 汪小哥
 * @date 11-05-2021
 */
@Slf4j
public class DubboExceptionHandlerExceptionResolver implements DubboHandlerExceptionResolver, InitializingBean, ApplicationContextAware {

    private ApplicationContext applicationContext;
    /**
     * 全局异常处理器
     */
    private final Map<DubboAdviceBean, DubboExceptionHandlerMethodResolver> exceptionHandlerAdviceCache = new LinkedHashMap<>();
    /**
     * dubbo service  target class 中的异常处理器
     */
    private final Map<Class<?>, DubboExceptionHandlerMethodResolver> exceptionHandlerCache = new ConcurrentHashMap<>(64);
    /**
     * dubbo 服务 提供者 实例
     */
    private final Map<Class<?>, Object> serviceTargetProviderCache = new ConcurrentHashMap<>(64);

    /**
     * 找不到缓存一个假的
     */
    public Object virtualServiceTarget = new Object();

    @Override
    public Object resolveException(Method dubboMethod, Invoker<?> invoker, Invocation invocation, Throwable throwable) throws Throwable {

        Object serviceTarget = this.getDubboServiceTarget(invoker);
        if (serviceTarget != null && !Objects.equals(virtualServiceTarget, serviceTarget)) {
            Class<?> handlerType = AopProxyUtils.ultimateTargetClass(serviceTarget);
            DubboExceptionHandlerMethodResolver resolver = this.exceptionHandlerCache.get(handlerType);
            if (resolver == null) {
                resolver = new DubboExceptionHandlerMethodResolver(handlerType);
                this.exceptionHandlerCache.put(handlerType, resolver);
            }
            Method method = resolver.resolveMethodByThrowable(throwable);
            if (method != null && ClassUtils.isAssignable(dubboMethod.getReturnType(), method.getReturnType())) {
                return this.doInvokerErrorHandlerMethod(dubboMethod, invoker, invocation, throwable, method, serviceTarget);
            }
        }
        if (serviceTarget == virtualServiceTarget) {
            log.warn("Can't find service  provider target {} service:{} method:{} method return type={} ", RpcContext.getContext().getRemoteHost(), invoker.getInterface().getName(), invocation.getMethodName(), dubboMethod.getReturnType().getName());
            return null;
        }

        for (Map.Entry<DubboAdviceBean, DubboExceptionHandlerMethodResolver> entry : this.exceptionHandlerAdviceCache.entrySet()) {
            DubboAdviceBean advice = entry.getKey();
            if (advice.isApplicableToBeanType(dubboMethod.getClass())) {
                DubboExceptionHandlerMethodResolver resolver = entry.getValue();
                Method method = resolver.resolveMethodByThrowable(throwable);
                if (method != null && ClassUtils.isAssignable(dubboMethod.getReturnType(), method.getReturnType())) {
                    return this.doInvokerErrorHandlerMethod(dubboMethod, invoker, invocation, throwable, method, advice.resolveBean());
                }
            }
        }
        log.warn("Dubbo provider exception filter can't resolver not find [@DubboExceptionHandler on method and method return class type assign same]  {} service:{} method:{} method return type={} ", RpcContext.getContext().getRemoteHost(), invoker.getInterface().getName(), invocation.getMethodName(), dubboMethod.getReturnType().getName());
        return null;
    }

    /**
     * 获取dubbo 服务提供者的 实例对象
     *
     * @param invoker
     * @return
     */
    private Object getDubboServiceTarget(Invoker<?> invoker) {
        Object serviceTarget = serviceTargetProviderCache.get(invoker.getInterface());
        if (serviceTarget == null) {
            synchronized (invoker.getInterface()) {
                serviceTarget = ProviderInvokerTargetUtils.getServiceTarget(invoker);
                if (serviceTarget == null) {
                    serviceTarget = virtualServiceTarget;
                }
                serviceTargetProviderCache.put(invoker.getInterface(), serviceTarget);
            }
        }
        return serviceTarget;
    }

    /**
     * 调用异常处理器
     *
     * @param handlerMethod
     * @param invoker
     * @param invocation
     * @param throwable
     * @param method
     * @param serviceTarget
     * @return
     * @throws Throwable
     */
    private Object doInvokerErrorHandlerMethod(Method handlerMethod, Invoker<?> invoker, Invocation invocation, Throwable throwable, Method method, Object serviceTarget) throws Throwable {
        try {
            Class<?>[] parameterTypes = method.getParameterTypes();
            Object[] args = new Object[parameterTypes.length];
            for (int index = 0; index < parameterTypes.length; index++) {
                // simple to  resolve parameter
                if (ClassUtils.isAssignable(handlerMethod.getClass(), parameterTypes[index])) {
                    args[index] = handlerMethod;
                } else if (ClassUtils.isAssignable(Invoker.class, parameterTypes[index])) {
                    args[index] = invoker;
                } else if (ClassUtils.isAssignable(Invocation.class, parameterTypes[index])) {
                    args[index] = invocation;
                } else if (ClassUtils.isAssignable(Throwable.class, parameterTypes[index])) {
                    args[index] = throwable;
                } else {
                    throw new IllegalStateException("Can't Resolve ClassType =" + parameterTypes[index].getName());
                }
            }
            ReflectionUtils.makeAccessible(method);
            return ReflectionUtils.invokeMethod(method, serviceTarget, args);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        initExceptionHandlerAdviceCache();
    }

    /**
     * 初始化全局缓存
     */
    private void initExceptionHandlerAdviceCache() {
        List<DubboAdviceBean> annotatedBeans = DubboAdviceBean.findAnnotatedBeans(applicationContext);
        for (DubboAdviceBean adviceBean : annotatedBeans) {
            Class<?> beanType = adviceBean.getBeanType();
            if (beanType == null) {
                throw new IllegalStateException("Unresolvable type for ControllerAdviceBean: " + adviceBean);
            }
            DubboExceptionHandlerMethodResolver resolver = new DubboExceptionHandlerMethodResolver(beanType);
            if (resolver.hasExceptionMappings()) {
                this.exceptionHandlerAdviceCache.put(adviceBean, resolver);
            }
        }
        int handlerSize = this.exceptionHandlerAdviceCache.size();
        if (handlerSize == 0) {
            log.debug("ControllerAdvice beans: none");
        } else {
            log.debug("DubboAdvice beans:{} @DubboAdvice", handlerSize);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
