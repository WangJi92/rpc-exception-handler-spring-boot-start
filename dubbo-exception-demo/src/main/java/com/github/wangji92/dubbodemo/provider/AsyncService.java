package com.github.wangji92.dubbodemo.provider;

/**
 * @author 汪小哥
 * @date 13-05-2021
 */
public interface AsyncService {


    String sayHello(String name);

    String sayHelloTimeout(String name);
}
