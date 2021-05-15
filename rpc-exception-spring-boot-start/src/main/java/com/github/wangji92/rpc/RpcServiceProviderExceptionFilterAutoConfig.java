package com.github.wangji92.rpc;

import com.github.wangji92.rpc.annotation.DefaultRpcServiceHandlerExceptionResolver;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 启动类
 *
 * @author 汪小哥
 * @date 13-05-2021
 */
@Configuration
@EnableConfigurationProperties(RpcServiceProviderExceptionFilterProperties.class)
public class RpcServiceProviderExceptionFilterAutoConfig {

    @Bean
    @ConditionalOnProperty(prefix = "rpc.service.provider.exception", value = "enable", havingValue = "true", matchIfMissing = false)
    public DefaultRpcServiceHandlerExceptionResolver defaultRpcServiceHandlerExceptionResolver() {
        return new DefaultRpcServiceHandlerExceptionResolver();
    }
}
