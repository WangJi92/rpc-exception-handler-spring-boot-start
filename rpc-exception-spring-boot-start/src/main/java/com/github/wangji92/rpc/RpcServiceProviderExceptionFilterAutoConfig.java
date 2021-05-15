package com.github.wangji92.rpc;

import com.github.wangji92.rpc.annotation.DefaultRpcServiceHandlerExceptionResolver;
import com.github.wangji92.rpc.constant.RpcServiceConstant;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class RpcServiceProviderExceptionFilterAutoConfig {

    @Bean(name = RpcServiceConstant.RPC_SERVICE_HANDLER_EXCEPTION_RESOLVER_BEAN_NAME)
    @ConditionalOnProperty(prefix = "rpc.service.provider.exception", value = "enable", havingValue = "true", matchIfMissing = false)
    public DefaultRpcServiceHandlerExceptionResolver defaultRpcServiceHandlerExceptionResolver() {
        log.info("rpc service exception filter start");
        return new DefaultRpcServiceHandlerExceptionResolver();
    }
}
