package com.github.wangji92.dubbodemo.provider;

import com.github.wangji92.dubbo.annotation.DubboExceptionHandler;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * @author 汪小哥
 * @date 13-05-2021
 */
@Component
public class AsyncServiceImpl implements AsyncService, Serializable {
    @Override
    public String sayHello(String name) {
        System.out.println("async provider received: " + name);
        if (1 == 1) {
            throw new RuntimeException("222");
        }

        return "hello, " + name;
    }

    @Override
    public String sayHelloTimeout(String name) {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "timed value";
    }

    @DubboExceptionHandler(value = RuntimeException.class)
    public AsyncServiceImpl handlerEx(Method handlerMethod, Invoker<?> invoker, Invocation invocation, Exception ex) {
        System.out.println("handler");
        return new AsyncServiceImpl();
    }
}
