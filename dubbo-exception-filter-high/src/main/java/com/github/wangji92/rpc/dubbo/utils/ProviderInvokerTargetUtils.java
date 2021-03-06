package com.github.wangji92.rpc.dubbo.utils;

import org.apache.dubbo.config.invoker.DelegateProviderMetaDataInvoker;
import org.apache.dubbo.registry.integration.RegistryProtocol;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.proxy.AbstractProxyInvoker;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

/**
 * Get invoker target service
 *
 * @author 汪小哥
 * @date 13-05-2021
 */
public class ProviderInvokerTargetUtils {

    /**
     * Get invoker target service
     *
     * @param invoker
     * @return
     */
    public static Object getServiceTarget(Invoker<?> invoker) {
        // in filter invoker maybe  is RegistryProtocol.InvokerDelegate or DelegateProviderMetaDataInvoker
        if (!(invoker instanceof RegistryProtocol.InvokerDelegate || invoker instanceof DelegateProviderMetaDataInvoker)) {
            return null;
        }
        DelegateProviderMetaDataInvoker delegateProviderMetaDataInvoker = null;
        if (invoker instanceof RegistryProtocol.InvokerDelegate) {
            Invoker delegateProviderMetaDataInvokerObject = ((RegistryProtocol.InvokerDelegate) invoker).getInvoker();
            if (!(delegateProviderMetaDataInvokerObject instanceof DelegateProviderMetaDataInvoker)) {
                return null;
            }
            delegateProviderMetaDataInvoker = (DelegateProviderMetaDataInvoker) ((RegistryProtocol.InvokerDelegate) invoker).getInvoker();
        } else {
            // dubbo.registry.address=N/A
            delegateProviderMetaDataInvoker = (DelegateProviderMetaDataInvoker) invoker;
        }
        Field proxyInvokerField = ReflectionUtils.findField(delegateProviderMetaDataInvoker.getClass(), "invoker");
        if (proxyInvokerField == null) {
            return null;
        }
        ReflectionUtils.makeAccessible(proxyInvokerField);
        AbstractProxyInvoker proxyInvoker = (AbstractProxyInvoker) ReflectionUtils.getField(proxyInvokerField, delegateProviderMetaDataInvoker);
        if (proxyInvoker == null) {
            return null;
        }
        Field proxyServiceField = ReflectionUtils.findField(proxyInvoker.getClass(), "proxy");
        if (proxyServiceField == null) {
            return null;
        }
        ReflectionUtils.makeAccessible(proxyServiceField);

        return ReflectionUtils.getField(proxyServiceField, proxyInvoker);
    }
}
