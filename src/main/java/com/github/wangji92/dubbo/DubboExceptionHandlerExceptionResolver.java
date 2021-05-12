package com.github.wangji92.dubbo;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
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


    private final Map<Class<?>, ExceptionHandlerMethodResolver> exceptionHandlerCache =
            new ConcurrentHashMap<>(64);

    @Override
    public Object resolveException(Method method, Invoker<?> invoker, Invocation invocation, Exception ex) {

        for (Map.Entry<DubboAdviceBean, ExceptionHandlerMethodResolver> entry : this.exceptionHandlerAdviceCache.entrySet()) {
            DubboAdviceBean advice = entry.getKey();
            if (advice.isApplicableToBeanType(method.getClass())) {
                ExceptionHandlerMethodResolver resolver = entry.getValue();
                Method handlerException = resolver.resolveMethod(ex);
                if (handlerException != null) {
                    try {
                        //todo 完善
                        Object invoke = handlerException.invoke(this, method, invoker, invocation, ex);

                        invocation.getInvoker().invoke(invocation);
                        return invoke;

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        return null;
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
