package com.github.wangji92.dubbo;

import com.github.wangji92.dubbo.annotation.DubboExceptionHandlerExceptionResolver;
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
@EnableConfigurationProperties(DubboProviderExceptionFilterProperties.class)
public class DubboProviderExceptionFilterAutoConfig {


    @Bean
    @ConditionalOnProperty(prefix = "dubbo.provider.exception", value = "enable", havingValue = "true", matchIfMissing = false)
    public DubboExceptionHandlerExceptionResolver dubboExceptionHandlerExceptionResolver() {
        return new DubboExceptionHandlerExceptionResolver();
    }
}
