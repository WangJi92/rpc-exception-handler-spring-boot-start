package com.github.wangji92.dubbo;

import com.github.wangji92.dubbo.utils.ProviderInvokerTargetUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.method.annotation.ExceptionHandlerMethodResolver;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 汪小哥
 * @date 11-05-2021
 */
@Component
@Slf4j
public class DubboExceptionHandlerExceptionResolver implements DubboHandlerExceptionResolver, InitializingBean, ApplicationContextAware {
    private ApplicationContext applicationContext;
    /**
     * 处理的类
     */
    private final Map<DubboAdviceBean, ExceptionHandlerMethodResolver> exceptionHandlerAdviceCache = new LinkedHashMap<>();

    /**
     * 缓存类信息
     */
    private final Map<Class<?>, ExceptionHandlerMethodResolver> exceptionHandlerCache =
            new ConcurrentHashMap<>(64);

    @Override
    public Object resolveException(Method handlerMethod, Invoker<?> invoker, Invocation invocation, Exception ex) {
        Class<?> handlerType = null;
        Object serviceTarget = ProviderInvokerTargetUtils.getServiceTarget(invoker);
        if (serviceTarget != null) {
            handlerType = AopProxyUtils.ultimateTargetClass(serviceTarget);
            ExceptionHandlerMethodResolver resolver = this.exceptionHandlerCache.get(handlerType);
            if (resolver == null) {
                resolver = new ExceptionHandlerMethodResolver(handlerType);
                this.exceptionHandlerCache.put(handlerType, resolver);
            }
            Method method = resolver.resolveMethod(ex);
            if (method != null) {
                return doInvokerErrorHandlerMethod(handlerMethod, invoker, invocation, ex, method);
            }
        }

        for (Map.Entry<DubboAdviceBean, ExceptionHandlerMethodResolver> entry : this.exceptionHandlerAdviceCache.entrySet()) {
            DubboAdviceBean advice = entry.getKey();
            if (advice.isApplicableToBeanType(handlerMethod.getClass())) {
                ExceptionHandlerMethodResolver resolver = entry.getValue();
                Method method = resolver.resolveMethod(ex);
                if (method != null) {
                    return doInvokerErrorHandlerMethod(handlerMethod, invoker, invocation, ex, method);
                }
            }
        }
        return null;
    }

    private Object doInvokerErrorHandlerMethod(Method handlerMethod, Invoker<?> invoker, Invocation invocation, Exception ex, Method method) {
        try {
            //todo 完善
            Object invoke = method.invoke(this, handlerMethod, invoker, invocation, ex);
            return invoke;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        initExceptionHandlerAdviceCache();
    }

    private void initExceptionHandlerAdviceCache() {
        List<DubboAdviceBean> annotatedBeans = DubboAdviceBean.findAnnotatedBeans(applicationContext);

        for (DubboAdviceBean adviceBean : annotatedBeans) {
            Class<?> beanType = adviceBean.getBeanType();
            if (beanType == null) {
                throw new IllegalStateException("Unresolvable type for ControllerAdviceBean: " + adviceBean);
            }
            ExceptionHandlerMethodResolver resolver = new ExceptionHandlerMethodResolver(beanType);
            if (resolver.hasExceptionMappings()) {
                this.exceptionHandlerAdviceCache.put(adviceBean, resolver);
            }
        }
        int handlerSize = this.exceptionHandlerAdviceCache.size();
        if (handlerSize == 0) {
            log.debug("ControllerAdvice beans: none");
        } else {
            log.debug("DubboAdvice beans: " + handlerSize + " @ExceptionHandler ");
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
