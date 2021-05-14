package com.github.wangji92.dubbodemo;

import com.github.wangji92.dubbo.annotation.DubboAdvice;
import com.github.wangji92.dubbo.annotation.DubboExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;

import java.lang.reflect.Method;

/**
 * @author 汪小哥
 * @date 14-05-2021
 */
@DubboAdvice
@Slf4j
public class DubboGlobalExceptionAdvice {

    @DubboExceptionHandler(value = RuntimeException.class)
    public String handlerEx(Method handlerMethod, Invoker<?> invoker, Invocation invocation, RuntimeException ex) {
        log.info("method={} params={} ex={}", handlerMethod.getName(), invocation.getArguments(), ex);
        return "DubboGlobalExceptionAdvice";
    }
}
