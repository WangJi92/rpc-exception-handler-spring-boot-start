package com.github.wangji92.dubbo.annotation;

import org.springframework.web.bind.annotation.ExceptionHandler;

import java.lang.annotation.*;

/**
 * 相同的含义 解决相同的问题 {@link ExceptionHandler}
 *
 * @author 汪小哥
 * @date 14-05-2021
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DubboExceptionHandler {
    /**
     * Exceptions handled by the annotated method. If empty, will default to any
     * exceptions listed in the method argument list.
     */
    Class<? extends Throwable>[] value() default {};
}
