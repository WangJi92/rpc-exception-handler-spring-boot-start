package com.github.wangji92.dubbo.utils;

import org.apache.dubbo.config.invoker.DelegateProviderMetaDataInvoker;
import org.apache.dubbo.registry.integration.RegistryProtocol;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.proxy.AbstractProxyInvoker;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

/**
 * 获取服务提供者的 target service
 *
 * @author 汪小哥
 * @date 13-05-2021
 */
public class ProviderInvokerTargetUtils {

    /**
     * 获取 invoker 目标service
     *
     * @param invoker
     * @return
     */
    public static Object getServiceTarget(Invoker<?> invoker) {
        Object target = null;
        if (!(invoker instanceof RegistryProtocol.InvokerDelegate)) {
            return target;
        }
        Invoker delegateProviderMetaDataInvokerObject = ((RegistryProtocol.InvokerDelegate) invoker).getInvoker();
        if (!(delegateProviderMetaDataInvokerObject instanceof DelegateProviderMetaDataInvoker)) {
            return target;
        }

        DelegateProviderMetaDataInvoker delegateProviderMetaDataInvoker = (DelegateProviderMetaDataInvoker) ((RegistryProtocol.InvokerDelegate) invoker).getInvoker();

        Field proxyInvokerField = ReflectionUtils.findField(delegateProviderMetaDataInvoker.getClass(), "invoker");
        if (proxyInvokerField == null) {
            return target;
        }
        ReflectionUtils.makeAccessible(proxyInvokerField);
        AbstractProxyInvoker proxyInvoker = (AbstractProxyInvoker) ReflectionUtils.getField(proxyInvokerField, delegateProviderMetaDataInvoker);
        if (proxyInvoker == null) {
            return target;
        }
        Field proxyServiceField = ReflectionUtils.findField(proxyInvoker.getClass(), "proxy");
        if (proxyServiceField == null) {
            return target;
        }
        ReflectionUtils.makeAccessible(proxyServiceField);
        target = ReflectionUtils.getField(proxyServiceField, proxyInvoker);
        return target;
    }
}
