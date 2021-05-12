package com.github.wangji92.dubbo;

import com.github.wangji92.dubbo.annotation.DubboAdvice;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@DubboAdvice
public class DubboExceptionHandlerSpringBootStartApplication {

    public static void main(String[] args) {
        SpringApplication.run(DubboExceptionHandlerSpringBootStartApplication.class, args);
    }

}
