package com.github.wangji92.rpc.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * {@linkplain org.springframework.web.bind.annotation.ControllerAdvice}
 *
 * @author 汪小哥
 * @date 15-05-2021
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface RpcServiceAdvice {

    /**
     * Alias for the {@link #basePackages} attribute.
     * <p>Allows for more concise annotation declarations &mdash; for example,
     * {@code @RpcServiceAdvice("org.my.pkg")} is equivalent to
     * {@code @RpcServiceAdvice(basePackages = "org.my.pkg")}.
     *
     * @see #basePackages
     * @since 4.0
     */
    @AliasFor("basePackages")
    String[] value() default {};

    /**
     * Array of base packages.
     * <p>Controllers that belong to those base packages or sub-packages thereof
     * will be included &mdash; for example,
     * {@code @RpcServiceAdvice(basePackages = "org.my.pkg")} or
     * {@code @RpcServiceAdvice(basePackages = {"org.my.pkg", "org.my.other.pkg"})}.
     * <p>{@link #value} is an alias for this attribute, simply allowing for
     * more concise use of the annotation.
     * <p>Also consider using {@link #basePackageClasses} as a type-safe
     * alternative to String-based package names.
     */
    @AliasFor("value")
    String[] basePackages() default {};

    /**
     * Type-safe alternative to {@link #basePackages} for specifying the packages
     */
    Class<?>[] basePackageClasses() default {};

    /**
     * Array of classes.
     * are assignable to at least one of the given types
     */
    Class<?>[] assignableTypes() default {};

    /**
     * Array of annotation types.
     * <p>Controllers that are annotated with at least one of the supplied annotation
     */
    Class<? extends Annotation>[] annotations() default {};
}
