package com.github.wangji92.dubbo;

import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;

import java.lang.reflect.Method;

/**
 * @author 汪小哥
 * @date 11-05-2021
 */
public interface DubboHandlerExceptionResolver {

    /**
     * 处理异常
     *
     * @param method
     * @param invoker
     * @param invocation
     * @param ex
     * @return
     */
    Object resolveException(Method method, Invoker<?> invoker, Invocation invocation, Exception ex);
}
