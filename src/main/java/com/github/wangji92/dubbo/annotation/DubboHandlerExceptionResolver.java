package com.github.wangji92.dubbo.annotation;

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
     * @param dubboMethod
     * @param invoker
     * @param invocation
     * @param throwable
     * @return
     * @throws Throwable
     */
    Object resolveException(Method dubboMethod, Invoker<?> invoker, Invocation invocation, Throwable throwable) throws Throwable;
}
