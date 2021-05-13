package com.github.wangji92.dubbo.expectionhandler;

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
     * @param throwable
     * @return
     * @throws Throwable
     */
    Object resolveException(Method method, Invoker<?> invoker, Invocation invocation, Throwable throwable) throws Throwable;
}
