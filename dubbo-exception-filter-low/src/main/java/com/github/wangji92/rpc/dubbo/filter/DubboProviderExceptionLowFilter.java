package com.github.wangji92.rpc.dubbo.filter;

import com.github.wangji92.rpc.constant.RpcServiceConstant;
import com.github.wangji92.rpc.dubbo.utils.ProviderInvokerTargetUtils;
import com.github.wangji92.rpc.spi.RpcServiceHandlerExceptionResolver;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Exception Handling filter
 *
 * @author 汪小哥
 * @date 11-05-2021
 */
@Activate(group = "provider", order = RpcServiceConstant.DUBBO_PROVIDER_EXCEPTION_FILTER_ORDER)
@Slf4j
public class DubboProviderExceptionLowFilter implements Filter {
    /**
     * dubbo Service Provider Instance
     */
    private final Map<Class<?>, Object> serviceTargetProviderCache = new ConcurrentHashMap<>(64);

    /**
     * Can't find ，cache a fake one
     */
    public Object virtualServiceTarget = new Object();

    /**
     * this field name equals to RpcServiceHandlerExceptionResolver bean name {@linkplain org.apache.dubbo.config.spring.extension.SpringExtensionFactory#getExtension(Class, String) }
     */
    private RpcServiceHandlerExceptionResolver rpcServiceHandlerExceptionResolver;

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        return invoker.invoke(invocation);
    }

    @Override
    public Result onResponse(Result result, Invoker<?> invoker, Invocation invocation) {
        if (result.hasException()) {
            return this.handlerError(invoker, invocation, result);
        }
        return result;
    }


    private Result handlerError(Invoker<?> invoker, Invocation invocation, Result responseResult) {
        Throwable throwable = responseResult.getException();
        if (throwable instanceof RpcException) {
            return responseResult;
        }
        if (getRpcServiceHandlerExceptionResolver() == null) {
            log.warn("Exception filter not  find dubbo exception handler exception resolver you can setting dubbo.provider.exception.enable = true");
            return responseResult;
        }
        try {
            Method method = invoker.getInterface().getMethod(invocation.getMethodName(), invocation.getParameterTypes());
            Object dubboServiceTarget = this.getDubboServiceTarget(invoker);
            Object handlerErrorResult = getRpcServiceHandlerExceptionResolver().resolveException(method, throwable, dubboServiceTarget, new Object[]{invoker, invocation});
            if (handlerErrorResult != null) {
                return new RpcResult(handlerErrorResult);
            }
        } catch (NoSuchMethodException e) {
            log.warn("Fail to Exception filter  when called by {} service:{} method:{} throwable={}", RpcContext.getContext().getRemoteHost(), invoker.getInterface().getName(), invocation.getMethodName(), throwable.getClass().getName() + ": " + throwable.getMessage(), e);
        } catch (Throwable e) {
            log.error("Fail to Exception filter when called by{} service:{} method:{} throwable={}", RpcContext.getContext().getRemoteHost(), invoker.getInterface().getName(), invocation.getMethodName(), throwable.getClass().getName() + ": " + throwable.getMessage(), e);
        }
        return responseResult;
    }

    /**
     * Get the instance object of the dubbo service provider
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


    private RpcServiceHandlerExceptionResolver getRpcServiceHandlerExceptionResolver() {
        return rpcServiceHandlerExceptionResolver;
    }

    public void setRpcServiceHandlerExceptionResolver(RpcServiceHandlerExceptionResolver rpcServiceHandlerExceptionResolver) {
        this.rpcServiceHandlerExceptionResolver = rpcServiceHandlerExceptionResolver;
    }
}
