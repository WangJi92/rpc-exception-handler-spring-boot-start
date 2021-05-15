package com.github.wangji92.rpc.annotation;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.OrderComparator;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.OrderUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 构建全局处理 dubbo 异常 元数据 {@linkplain org.springframework.web.method.ControllerAdviceBean}
 * {@linkplain }
 *
 * @author 汪小哥
 * @date 11-05-2021
 */
public class RpcServiceAdviceBean implements Ordered {

    /**
     * Reference to the actual bean instance or a {@code String} representing
     * the bean name.
     */
    private final Object beanOrName;

    /**
     * Reference to the resolved bean instance, potentially lazily retrieved
     * via the {@code BeanFactory}.
     */
    @Nullable
    private Object resolvedBean;

    @Nullable
    private final Class<?> beanType;

    private final RpcServiceExceptionHandlerTypePredicate beanTypePredicate;

    @Nullable
    private final BeanFactory beanFactory;

    @Nullable
    private Integer order;


    public RpcServiceAdviceBean(Object bean) {
        Assert.notNull(bean, "Bean must not be null");
        this.beanOrName = bean;
        this.resolvedBean = bean;
        this.beanType = ClassUtils.getUserClass(bean.getClass());
        this.beanTypePredicate = createBeanTypePredicate(this.beanType);
        this.beanFactory = null;
    }


    /**
     * @param beanName
     * @param beanFactory
     * @param rpcServiceAdvice
     */
    public RpcServiceAdviceBean(String beanName, BeanFactory beanFactory, @Nullable RpcServiceAdvice rpcServiceAdvice) {
        Assert.hasText(beanName, "Bean name must contain text");
        Assert.notNull(beanFactory, "BeanFactory must not be null");
        Assert.isTrue(beanFactory.containsBean(beanName), () -> "BeanFactory [" + beanFactory + "] does not contain specified controller advice bean '" + beanName + "'");

        this.beanOrName = beanName;
        this.beanType = getBeanType(beanName, beanFactory);
        this.beanTypePredicate = (rpcServiceAdvice != null ? createBeanTypePredicate(rpcServiceAdvice) : createBeanTypePredicate(this.beanType));
        this.beanFactory = beanFactory;
    }


    /**
     * @return
     */
    @Override
    public int getOrder() {
        if (this.order == null) {
            Object resolvedBean = resolveBean();
            if (resolvedBean instanceof Ordered) {
                this.order = ((Ordered) resolvedBean).getOrder();
            } else if (this.beanType != null) {
                this.order = OrderUtils.getOrder(this.beanType, Ordered.LOWEST_PRECEDENCE);
            } else {
                this.order = Ordered.LOWEST_PRECEDENCE;
            }
        }
        return this.order;
    }

    /**
     * Return the type of the contained bean.
     * <p>If the bean type is a CGLIB-generated class, the original user-defined
     * class is returned.
     */
    @Nullable
    public Class<?> getBeanType() {
        return this.beanType;
    }

    /**
     * Get the bean instance for this {@code ControllerAdviceBean}, if necessary
     * resolving the bean name through the {@link BeanFactory}.
     * <p>As of Spring Framework 5.2, once the bean instance has been resolved it
     * will be cached, thereby avoiding repeated lookups in the {@code BeanFactory}.
     */
    public Object resolveBean() {
        if (this.resolvedBean == null) {
            // this.beanOrName must be a String representing the bean name if
            // this.resolvedBean is null.
            this.resolvedBean = obtainBeanFactory().getBean((String) this.beanOrName);
        }
        return this.resolvedBean;
    }

    private BeanFactory obtainBeanFactory() {
        Assert.state(this.beanFactory != null, "No BeanFactory set");
        return this.beanFactory;
    }

    /**
     * Check whether the given bean type should be advised by this
     *
     * @param beanType the type of the bean to check
     * @since 4.0
     */
    public boolean isApplicableToBeanType(@Nullable Class<?> beanType) {
        return this.beanTypePredicate.test(beanType);
    }


    @Override
    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof RpcServiceAdviceBean)) {
            return false;
        }
        RpcServiceAdviceBean otherAdvice = (RpcServiceAdviceBean) other;
        return (this.beanOrName.equals(otherAdvice.beanOrName) && this.beanFactory == otherAdvice.beanFactory);
    }

    @Override
    public int hashCode() {
        return this.beanOrName.hashCode();
    }

    @Override
    public String toString() {
        return this.beanOrName.toString();
    }


    /**
     * Find beans annotated with {@link RpcServiceAdvice @RpcServiceAdvice} in the
     * given {@link ApplicationContext} and wrap them as {@code RpcServiceAdvice}
     * instances.
     * <p>As of Spring Framework 5.2, the {@code RpcServiceAdvice} instances
     * in the returned list are sorted using {@link OrderComparator#sort(List)}.
     *
     * @see #getOrder()
     * @see OrderComparator
     * @see Ordered
     */
    public static List<RpcServiceAdviceBean> findAnnotatedBeans(ApplicationContext context) {
        List<RpcServiceAdviceBean> adviceBeans = new ArrayList<>();
        for (String name : BeanFactoryUtils.beanNamesForTypeIncludingAncestors(context, Object.class)) {
            RpcServiceAdvice controllerAdvice = context.findAnnotationOnBean(name, RpcServiceAdvice.class);
            if (controllerAdvice != null) {
                // Use the @ControllerAdvice annotation found by findAnnotationOnBean()
                // in order to avoid a subsequent lookup of the same annotation.
                adviceBeans.add(new RpcServiceAdviceBean(name, context, controllerAdvice));
            }
        }
        OrderComparator.sort(adviceBeans);
        return adviceBeans;
    }

    @Nullable
    private static Class<?> getBeanType(String beanName, BeanFactory beanFactory) {
        Class<?> beanType = beanFactory.getType(beanName);
        return (beanType != null ? ClassUtils.getUserClass(beanType) : null);
    }

    private static RpcServiceExceptionHandlerTypePredicate createBeanTypePredicate(@Nullable Class<?> beanType) {
        RpcServiceAdvice rpcServiceAdvice = (beanType != null ?
                AnnotatedElementUtils.findMergedAnnotation(beanType, RpcServiceAdvice.class) : null);
        return createBeanTypePredicate(rpcServiceAdvice);
    }

    private static RpcServiceExceptionHandlerTypePredicate createBeanTypePredicate(@Nullable RpcServiceAdvice controllerAdvice) {
        if (controllerAdvice != null) {
            return RpcServiceExceptionHandlerTypePredicate.builder()
                    .basePackage(controllerAdvice.basePackages())
                    .basePackageClass(controllerAdvice.basePackageClasses())
                    .assignableType(controllerAdvice.assignableTypes())
                    .annotation(controllerAdvice.annotations())
                    .build();
        }
        return RpcServiceExceptionHandlerTypePredicate.forAnyHandlerType();
    }
}
