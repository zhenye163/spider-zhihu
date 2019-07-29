package com.netopstec.spiderzhihu.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * @author zhenye 2019/7/29
 */
@Slf4j
@Component
public class ElasticConfig implements InitializingBean {
    static {
        System.setProperty("es.set.netty.runtime.available.processors", "false");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("解决由于netty版本冲突导致项目无法启动");
        log.info("设置es.set.netty.runtime.available.processors的值为：[{}]",System.getProperty("es.set.netty.runtime.available.processors"));
    }
}
