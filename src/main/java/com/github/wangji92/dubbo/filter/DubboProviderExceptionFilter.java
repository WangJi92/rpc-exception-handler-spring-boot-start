package com.github.wangji92.dubbo.filter;

import com.github.wangji92.dubbo.annotation.DubboExceptionHandlerExceptionResolver;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

import java.lang.reflect.Method;

/**
 * 异常处理器
 *
 * @author 汪小哥
 * @date 11-05-2021
 */
@Activate(group = CommonConstants.PROVIDER, order = 99999)
@Slf4j
public class DubboProviderExceptionFilter implements Filter {

    private DubboExceptionHandlerExceptionResolver dubboExceptionHandlerExceptionResolver;

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {

        Result responseResult = new AppResponse(invocation);
        try {
            responseResult = invoker.invoke(invocation);
        } catch (Exception e) {
            responseResult.setException(e);
        }

        if (!responseResult.hasException()) {
            return responseResult;
        }
        if (getDubboExceptionHandlerExceptionResolver() == null) {
            log.warn("Exception filter not  find dubbo exception handler exception resolver");
            return responseResult;
        }

        Throwable throwable = responseResult.getException();
        if (throwable instanceof RpcException) {
            return responseResult;
        }

        try {
            Method method = invoker.getInterface().getMethod(invocation.getMethodName(), invocation.getParameterTypes());
            Object handlerErrorResult = getDubboExceptionHandlerExceptionResolver().resolveException(method, invoker, invocation, throwable);
            if (handlerErrorResult != null) {
                Class<?> handlerErrorResultClass = handlerErrorResult.getClass();
                // 返回的数据类型 和 返回值的类型一致
                if (method.getReturnType().isAssignableFrom(handlerErrorResultClass)) {
                    responseResult.setException(null);
                    responseResult.setValue(handlerErrorResult);
                } else {
                    log.warn("Exception filter handler result return type must is assignable from original method return type  when called by " + RpcContext.getContext().getRemoteHost() + ". service: " + invoker.getInterface().getName() + ", method: " + invocation.getMethodName() + ", throwable: " + throwable.getClass().getName() + ": " + throwable.getMessage(), throwable);
                }
            }
        } catch (NoSuchMethodException e) {
            log.warn("Fail to Exception filter  when called by " + RpcContext.getContext().getRemoteHost() + ". service: " + invoker.getInterface().getName() + ", method: " + invocation.getMethodName() + ", throwable: " + e.getClass().getName() + ": " + e.getMessage(), e);
            return responseResult;
        } catch (Throwable e) {
            log.error("Fail to Exception filter when called by " + RpcContext.getContext().getRemoteHost() + ". service: " + invoker.getInterface().getName() + ", method: " + invocation.getMethodName() + ", throwable: " + e.getClass().getName() + ": " + e.getMessage(), e);
        }
        return responseResult;
    }

    private DubboExceptionHandlerExceptionResolver getDubboExceptionHandlerExceptionResolver() {
        return dubboExceptionHandlerExceptionResolver;
    }

    public void setDubboExceptionHandlerExceptionResolver(DubboExceptionHandlerExceptionResolver dubboExceptionHandlerExceptionResolver) {
        this.dubboExceptionHandlerExceptionResolver = dubboExceptionHandlerExceptionResolver;
    }

}
