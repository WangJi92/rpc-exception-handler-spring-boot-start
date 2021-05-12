package com.github.wangji92.dubbo;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.rpc.*;

/**
 * @author 汪小哥
 * @date 11-05-2021
 */
@Slf4j
public class DubboExceptionFilter2 implements Filter {
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
//        try {
//            // 服务调用
//            Result result = invoker.invoke(invocation);
//            // 有异常，并且非泛化调用
//            if (result.hasException() && GenericService.class != invoker.getInterface()) {
//                try {
//                    Throwable exception = result.getException();
//
//                    // directly throw if it's checked exception
//                    // 如果是checked异常，直接抛出
//                    if (!(exception instanceof RuntimeException) && (exception instanceof Exception)) {
//                        return result;
//                    }
//                    // directly throw if the exception appears in the signature
//                    // 在方法签名上有声明，直接抛出
//                    try {
//                        Method method = invoker.getInterface().getMethod(invocation.getMethodName(), invocation.getParameterTypes());
//                        Class<?>[] exceptionClassses = method.getExceptionTypes();
//                        for (Class<?> exceptionClass : exceptionClassses) {
//                            if (exception.getClass().equals(exceptionClass)) {
//                                return result;
//                            }
//                        }
//                    } catch (NoSuchMethodException e) {
//                        return result;
//                    }
//
//                    // 未在方法签名上定义的异常，在服务器端打印 ERROR 日志
//                    // for the exception not found in method's signature, print ERROR message in server's log.
//                    log.error("Got unchecked and undeclared exception which called by " + RpcContext.getContext().getRemoteHost()
//                            + ". service: " + invoker.getInterface().getName() + ", method: " + invocation.getMethodName()
//                            + ", exception: " + exception.getClass().getName() + ": " + exception.getMessage(), exception);
//
//                    // 异常类和接口类在同一 jar 包里，直接抛出
//                    // directly throw if exception class and interface class are in the same jar file.
//                    String serviceFile = ReflectUtils.getCodeBase(invoker.getInterface());
//                    String exceptionFile = ReflectUtils.getCodeBase(exception.getClass());
//                    if (serviceFile == null || exceptionFile == null || serviceFile.equals(exceptionFile)) {
//                        return result;
//                    }
//                    // 是JDK自带的异常，直接抛出
//                    // directly throw if it's JDK exception
//                    String className = exception.getClass().getName();
//                    if (className.startsWith("java.") || className.startsWith("javax.")) {
//                        return result;
//                    }
//                    // 是Dubbo本身的异常，直接抛出
//                    // directly throw if it's dubbo exception
//                    if (exception instanceof RpcException) {
//                        return result;
//                    }
//
//                    // 否则，包装成RuntimeException抛给客户端
//                    // otherwise, wrap with RuntimeException and throw back to the client
//                    return new RpcResult(new RuntimeException(StringUtils.toString(exception)));
//                } catch (Throwable e) {
//                    logger.warn("Fail to ExceptionFilter when called by " + RpcContext.getContext().getRemoteHost()
//                            + ". service: " + invoker.getInterface().getName() + ", method: " + invocation.getMethodName()
//                            + ", exception: " + e.getClass().getName() + ": " + e.getMessage(), e);
//                    return result;
//                }
//            }
//            // 返回
//            return result;
//        } catch (RuntimeException e) {
//            logger.error("Got unchecked and undeclared exception which called by " + RpcContext.getContext().getRemoteHost()
//                    + ". service: " + invoker.getInterface().getName() + ", method: " + invocation.getMethodName()
//                    + ", exception: " + e.getClass().getName() + ": " + e.getMessage(), e);
//            throw e;
//        }
        return null;

    }
}
