package com.github.wangji92.dubbo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author 汪小哥
 * @date 11-05-2021
 */
@Slf4j
public class DubboMappingHandlerAdapter implements InitializingBean, ApplicationContextAware {


    @Override
    public void afterPropertiesSet() throws Exception {


    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

    }
}
