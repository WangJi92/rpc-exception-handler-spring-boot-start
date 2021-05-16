package com.github.wangji92.sample.provider.service;

import com.github.wangji92.common.BaseResult;
import com.github.wangji92.common.PageData;
import com.github.wangji92.common.PageResult;
import com.github.wangji92.sample.provider.spi.DemoService;
import com.google.common.collect.Lists;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.Random;

/**
 * @author 汪小哥
 * @date 16-05-2021
 */
@DubboService(version = "1.0.0")
public class DefaultDemoService implements DemoService {
    @Override
    public BaseResult<String> helloBaseResult(String name) {
        int i = new Random().nextInt(100);
        if (i % 3 == 0) {
            throw new RuntimeException("异常异常");
        } else if (i % 3 == 1) {
            throw new IllegalArgumentException("异常异常");
        }
        return BaseResult.success(name);
    }

    @Override
    public BaseResult<PageData<String>> helloPageResult(String name) {
        int i = new Random().nextInt(100);
        if (i % 3 == 0) {
            throw new RuntimeException("异常异常");
        } else if (i % 3 == 1) {
            throw new IllegalArgumentException("异常异常");
        }
        PageData<String> pageData = new PageData<>();
        pageData.setData(Lists.newArrayList(name));
        pageData.setCount(1);
        return PageResult.success(pageData);
    }
}
