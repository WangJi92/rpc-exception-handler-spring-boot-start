package com.github.wangji92.rpc.spi;

import java.lang.reflect.Method;

/**
 * rpc 异常处理器
 *
 * @author 汪小哥
 * @date 15-05-2021
 */
public interface RpcServiceHandlerExceptionResolver {
    /**
     * 异常处理器spi
     *
     * @param rpcServiceMethod
     * @param throwable
     * @param rpcServiceTarget
     * @param args
     * @return
     * @throws Throwable
     */
    Object resolveException(Method rpcServiceMethod, Throwable throwable, Object rpcServiceTarget, Object[] args) throws Throwable;
}
