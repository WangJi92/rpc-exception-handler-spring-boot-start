package com.github.wangji92.sample.provider.exception;

import com.github.wangji92.common.BaseResult;
import com.github.wangji92.rpc.annotation.RpcServiceAdvice;
import com.github.wangji92.rpc.annotation.RpcServiceExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;

import java.lang.reflect.Method;

/**
 * @author 汪小哥
 * @date 14-05-2021
 */
@RpcServiceAdvice
@Slf4j
public class DubboGlobalExceptionAdvice {

    @RpcServiceExceptionHandler(value = RuntimeException.class)
    public BaseResult handlerEx(Method handlerMethod, Invoker<?> invoker, Invocation invocation, RuntimeException ex) {
        log.info("method={} params={} ex={}", handlerMethod.getName(), invocation.getArguments(), ex.getClass().getName());
        return BaseResult.fail("not ok", "");
    }

    @RpcServiceExceptionHandler(value = IllegalArgumentException.class)
    public BaseResult handlerIllegalArgumentException(Method handlerMethod, Invoker<?> invoker, Invocation invocation, IllegalArgumentException ex) {
        log.info("method={} params={} ex={}", handlerMethod.getName(), invocation.getArguments(), ex.getClass().getName());
        return BaseResult.fail("not ok", "");
    }
}
