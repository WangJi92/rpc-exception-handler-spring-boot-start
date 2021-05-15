package com.github.wangji92.rpc.annotation;

import java.lang.annotation.*;

/**
 * Exceptions that can be handled {@linkplain org.springframework.web.bind.annotation.ExceptionHandler}
 *
 * @author 汪小哥
 * @date 14-05-2021
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RpcServiceExceptionHandler {
    /**
     * Exceptions handled by the annotated method. If empty, will default to any
     * exceptions listed in the method argument list.
     */
    Class<? extends Throwable>[] value() default {};
}
