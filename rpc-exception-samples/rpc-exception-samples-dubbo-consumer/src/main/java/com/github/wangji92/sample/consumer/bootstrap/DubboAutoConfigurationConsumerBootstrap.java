package com.github.wangji92.sample.consumer.bootstrap;

import com.github.wangji92.common.BaseResult;
import com.github.wangji92.common.PageData;
import com.github.wangji92.sample.provider.spi.DemoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * @author 汪小哥
 * @date 16-05-2021
 */
@EnableAutoConfiguration
@Slf4j
public class DubboAutoConfigurationConsumerBootstrap {


    @DubboReference(version = "1.0.0", url = "dubbo://127.0.0.1:12345")
    private DemoService demoService;

    public static void main(String[] args) {
        SpringApplication.run(DubboAutoConfigurationConsumerBootstrap.class).close();
    }

    @Bean
    @SuppressWarnings("unchecked")
    public ApplicationRunner runner() {
        return args -> {
            BaseResult<String> baseResult = demoService.helloBaseResult("helloPageResult");
            log.info("baseResult {} {} {}", baseResult.getSuccess(), baseResult.getCode(), baseResult.getMsg());


            BaseResult<PageData<String>> helloPageResult = demoService.helloPageResult("helloPageResult");
            log.info("helloPageResult {} {} {}", helloPageResult.getSuccess(), helloPageResult.getCode(), helloPageResult.getMsg());
        };
    }


}
