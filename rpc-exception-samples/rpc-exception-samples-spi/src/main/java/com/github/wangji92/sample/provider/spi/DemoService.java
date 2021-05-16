package com.github.wangji92.sample.provider.spi;

import com.github.wangji92.common.BaseResult;
import com.github.wangji92.common.PageData;

/**
 * @author 汪小哥
 * @date 16-05-2021
 */
public interface DemoService<S> {

    /**
     * 标准简单的服务返回值
     *
     * @param name
     * @return
     */
    BaseResult<String> helloBaseResult(String name);


    /**
     * 分页返回
     *
     * @param name
     * @return
     */
    BaseResult<PageData<String>> helloPageResult(String name);
}
