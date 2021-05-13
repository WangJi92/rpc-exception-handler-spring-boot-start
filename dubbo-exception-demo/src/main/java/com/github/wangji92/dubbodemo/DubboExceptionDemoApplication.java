package com.github.wangji92.dubbodemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource(locations = {"classpath*:spring/async-provider.xml"})
public class DubboExceptionDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DubboExceptionDemoApplication.class, args);
    }

}
