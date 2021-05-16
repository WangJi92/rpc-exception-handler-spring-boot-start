package com.github.wangji92.sample.consumer.bootstrap;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.service.GenericService;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author 汪小哥
 * @date 16-05-2021
 */
@Slf4j
public class GenericCallConsumer {

    private static GenericService genericService;

    /**
     * 泛化调用测试
     *
     * @param args
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setName("generic-call-consumer");
        ReferenceConfig<GenericService> referenceConfig = new ReferenceConfig<>();
        referenceConfig.setInterface("com.github.wangji92.sample.provider.spi.DemoService");
        referenceConfig.setApplication(applicationConfig);
        referenceConfig.setGeneric(true);
        referenceConfig.setAsync(true);
        referenceConfig.setTimeout(70000);
        referenceConfig.setVersion("1.0.0");
        referenceConfig.setGroup("DUBBO_GROUP");
        referenceConfig.setUrl("dubbo://127.0.0.1:12345");

        genericService = referenceConfig.get();
        helloBaseResult();
        helloPageResult();
    }


    public static void helloBaseResult() throws InterruptedException, ExecutionException {
        Object result = genericService.$invoke("helloBaseResult", new String[]{"java.lang.String"}, new Object[]{"world"});

        CompletableFuture<Object> future = RpcContext.getContext().getCompletableFuture();

        Object baseResult = future.get();
        log.info("helloBaseResult {}", JSON.toJSONString(baseResult));


    }

    public static void helloPageResult() throws InterruptedException, ExecutionException {
        Object result = genericService.$invoke("helloPageResult", new String[]{"java.lang.String"}, new Object[]{"world"});

        CompletableFuture<Object> future = RpcContext.getContext().getCompletableFuture();

        Object baseResult = future.get();
        log.info("helloPageResult {}", JSON.toJSONString(baseResult));


    }
}
