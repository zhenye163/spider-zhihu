package com.netopstec.spiderzhihu.config;

import com.netopstec.spiderzhihu.common.RabbitConstants;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 消息队列RabbitMQ的相关配置
 * @author zhenye 2019/6/28
 */
@Configuration
public class RabbitConfig {

    /**
     * RabbitMQ队列（将可用代理保存进DB的队列，让Spring管理）
     */
    @Bean
    public Queue saveActiveProxyIpToDBQueue() {
        return new Queue(RabbitConstants.QUEUE_CHECK_PROXY_IP_AND_SAVE_TO_DB, false);
    }

    @Bean
    public Exchange saveActiveProxyIpToDBExchange() {
        return new DirectExchange(RabbitConstants.EXCHANGE_CHECK_PROXY_IP_AND_SAVE_TO_DB);
    }

    @Bean
    public Binding saveActiveProxyIpToDBBinding() {
        return BindingBuilder
                .bind(saveActiveProxyIpToDBQueue())
                .to(saveActiveProxyIpToDBExchange())
                .with(RabbitConstants.KEY_CHECK_PROXY_IP_AND_SAVE_TO_DB)
                .noargs();
    }
}
