package com.github.wangji92.rpc.annotation;

import com.github.wangji92.rpc.spi.RpcServiceHandlerExceptionResolver;
import lombok.extern.slf4j.Slf4j;
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
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@literal ExceptionHandlerExceptionResolver}
 *
 * @author 汪小哥
 * @date 11-05-2021
 */
@Slf4j
public class DefaultRpcServiceHandlerExceptionResolver implements RpcServiceHandlerExceptionResolver, InitializingBean, ApplicationContextAware {

    private ApplicationContext applicationContext;
    /**
     * 全局异常处理器
     */
    private final Map<RpcServiceAdviceBean, RpcServiceExceptionHandlerMethodResolver> exceptionHandlerAdviceCache = new LinkedHashMap<>();
    /**
     * dubbo service  target class 中的异常处理器
     */
    private final Map<Class<?>, RpcServiceExceptionHandlerMethodResolver> exceptionHandlerCache = new ConcurrentHashMap<>(64);
    /**
     * dubbo 服务 提供者 实例
     */
    private final Map<Class<?>, Object> serviceTargetProviderCache = new ConcurrentHashMap<>(64);

    /**
     * 找不到缓存一个假的
     */
    public Object virtualServiceTarget = new Object();

    @Override
    public Object resolveException(Method rpcServiceMethod, Throwable throwable, Object rpcServiceTarget, Object[] args) throws Throwable {
        if (rpcServiceTarget != null) {
            Class<?> handlerType = AopProxyUtils.ultimateTargetClass(rpcServiceTarget);
            RpcServiceExceptionHandlerMethodResolver resolver = this.exceptionHandlerCache.get(handlerType);
            if (resolver == null) {
                resolver = new RpcServiceExceptionHandlerMethodResolver(handlerType);
                this.exceptionHandlerCache.put(handlerType, resolver);
            }
            Method method = resolver.resolveMethodByThrowable(throwable);
            if (method != null && ClassUtils.isAssignable(rpcServiceMethod.getReturnType(), method.getReturnType())) {
                return this.doInvokerErrorHandlerMethod(method, rpcServiceTarget, rpcServiceMethod, throwable, args);
            }
        }

        for (Map.Entry<RpcServiceAdviceBean, RpcServiceExceptionHandlerMethodResolver> entry : this.exceptionHandlerAdviceCache.entrySet()) {
            RpcServiceAdviceBean advice = entry.getKey();
            if (advice.isApplicableToBeanType(rpcServiceMethod.getClass())) {
                RpcServiceExceptionHandlerMethodResolver resolver = entry.getValue();
                Method method = resolver.resolveMethodByThrowable(throwable);
                if (method != null && ClassUtils.isAssignable(rpcServiceMethod.getReturnType(), method.getReturnType())) {
                    return this.doInvokerErrorHandlerMethod(method, advice.resolveBean(), rpcServiceMethod, throwable, args);
                }
            }
        }
        log.warn("Dubbo provider exception filter can't resolver not find [@RpcServiceExceptionHandler on method and method return class type assign same] service={} method={} method return type={} ", rpcServiceMethod.getDeclaringClass().getName(), rpcServiceMethod.getName(), rpcServiceMethod.getReturnType().getName());
        return null;
    }


    /**
     * 调用异常处理的方法
     *
     * @param method
     * @param methodTarget
     * @param rpcServiceMethod
     * @param throwable
     * @param providerArgs
     * @return
     */
    private Object doInvokerErrorHandlerMethod(Method method, Object methodTarget, Method rpcServiceMethod, Throwable throwable, Object[] providerArgs) {
        try {
            Class<?>[] parameterTypes = method.getParameterTypes();
            Object[] args = new Object[parameterTypes.length];
            for (int index = 0; index < parameterTypes.length; index++) {
                // simple to  resolve parameter
                if (ClassUtils.isAssignable(Throwable.class, parameterTypes[index])) {
                    args[index] = throwable;
                    continue;
                }
                if (ClassUtils.isAssignable(Method.class, parameterTypes[index])) {
                    args[index] = rpcServiceMethod;
                    continue;
                }

                for (Object providerArg : providerArgs) {
                    if (ClassUtils.isAssignable(providerArg.getClass(), parameterTypes[index])) {
                        args[index] = throwable;
                        break;
                    }
                }
                if (args[index] != null) {
                    continue;
                }
                throw new IllegalStateException("Can't Resolve ClassType =" + parameterTypes[index].getName());
            }
            ReflectionUtils.makeAccessible(method);
            return ReflectionUtils.invokeMethod(method, methodTarget, args);
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
        List<RpcServiceAdviceBean> annotatedBeans = RpcServiceAdviceBean.findAnnotatedBeans(applicationContext);
        for (RpcServiceAdviceBean adviceBean : annotatedBeans) {
            Class<?> beanType = adviceBean.getBeanType();
            if (beanType == null) {
                throw new IllegalStateException("Unresolvable type for ControllerAdviceBean: " + adviceBean);
            }
            RpcServiceExceptionHandlerMethodResolver resolver = new RpcServiceExceptionHandlerMethodResolver(beanType);
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
