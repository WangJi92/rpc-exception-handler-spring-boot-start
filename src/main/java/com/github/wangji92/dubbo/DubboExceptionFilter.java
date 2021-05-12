package com.github.wangji92.dubbo;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.rpc.*;
import org.springframework.web.method.annotation.ExceptionHandlerMethodResolver;

import java.lang.reflect.Method;

/**
 * @author 汪小哥
 * @date 11-05-2021
 */
@Slf4j
public class DubboExceptionFilter implements Filter {
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
//        log.debug("BizExceptionFilter:{},{}", invoker.getInterface(),
//                JsonUtil.jsonFromObject(invocation.getArguments()));
//        Result result = invoker.invoke(invocation);
//        Object realResult = result.getValue();
//        if (result.hasException()) {
//            try {
//                ExceptionHandlerMethodResolver resolver=new ExceptionHandlerMethodResolver(this.getClass());
//                Exception exception=(Exception) result.getException();
//                Method method=resolver.resolveMethod(exception);
//                realResult = method.invoke(this, exception);
//
//
//
//                return new RpcResult(realResult);
//            } catch (Throwable e) {
//                log.error("Exception handler error. Caused Exception:{}", result.getException());
//            }
//        }
//
//        return new RpcResult(rsp);
        return null;
    }
}
