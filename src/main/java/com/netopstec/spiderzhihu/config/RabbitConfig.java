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

    @Bean
    public Queue ipProxySaveIfActiveQueue() {
        return new Queue(RabbitConstants.QUEUE_IP_PROXY_SAVE_IF_ACTIVE, false);
    }

    @Bean
    public Exchange ipProxySaveIfActiveExchange() {
        return new DirectExchange(RabbitConstants.EXCHANGE_IP_PROXY_SAVE_IF_ACTIVE);
    }

    @Bean
    public Binding ipProxySaveIfActiveBinding() {
        return BindingBuilder
                .bind(ipProxySaveIfActiveQueue())
                .to(ipProxySaveIfActiveExchange())
                .with(RabbitConstants.KEY_IP_PROXY_SAVE_IF_ACTIVE)
                .noargs();
    }

    @Bean
    public Queue ipProxyDeleteIfInActiveQueue() {
        return new Queue(RabbitConstants.QUEUE_IP_PROXY_DELETE_IF_INACTIVE, false);
    }

    @Bean
    public Exchange ipProxyDeleteIfInActiveExchange() {
        return new DirectExchange(RabbitConstants.EXCHANGE_IP_PROXY_DELETE_IF_INACTIVE);
    }

    @Bean
    public Binding ipProxyDeleteIfInActiveBinding() {
        return BindingBuilder
                .bind(ipProxyDeleteIfInActiveQueue())
                .to(ipProxyDeleteIfInActiveExchange())
                .with(RabbitConstants.KEY_IP_PROXY_DELETE_IF_INACTIVE)
                .noargs();
    }
}
