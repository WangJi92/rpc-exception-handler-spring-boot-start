package com.github.wangji92.rpc;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 开关
 *
 * @author 汪小哥
 * @date 13-05-2021
 */
@Configuration
@ConfigurationProperties(prefix = "rpc.service.provider.exception")
@Data
public class RpcServiceProviderExceptionFilterProperties {

    private boolean enable = true;
}
