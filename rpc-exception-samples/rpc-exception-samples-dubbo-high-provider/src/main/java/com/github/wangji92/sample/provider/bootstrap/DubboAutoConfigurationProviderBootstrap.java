package com.github.wangji92.sample.provider.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author 汪小哥
 * @date 16-05-2021
 */
@EnableAutoConfiguration
@ComponentScan(basePackages = {"com.github.wangji92.sample"})
public class DubboAutoConfigurationProviderBootstrap {
    public static void main(String[] args) {

        SpringApplication.run(DubboAutoConfigurationProviderBootstrap.class, args);
    }


}
