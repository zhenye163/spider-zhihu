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
        return new Queue(RabbitConstants.QUEUE_SAVE_ACTIVE_PROXY_IP_TO_DB, false);
    }

    @Bean
    public Exchange saveActiveProxyIpToDBExchange() {
        return new DirectExchange(RabbitConstants.EXCHANGE_SAVE_ACTIVE_PROXY_IP_TO_DB);
    }

    @Bean
    public Binding saveActiveProxyIpToDBBinding() {
        return BindingBuilder
                .bind(saveActiveProxyIpToDBQueue())
                .to(saveActiveProxyIpToDBExchange())
                .with(RabbitConstants.KEY_SAVE_ACTIVE_PROXY_IP_TO_DB)
                .noargs();
    }

    /**
     * RabbitMQ队列（定期删除DB中不可用代理的队列，让Spring管理）
     */
    @Bean
    public Queue deleteInactiveProxyIpInDBQueue() {
        return new Queue(RabbitConstants.QUEUE_DELETE_INACTIVE_PROXY_IP_IN_DB, false);
    }

    @Bean
    public Exchange deleteInactiveProxyIpInDBExchange() {
        return new DirectExchange(RabbitConstants.EXCHANGE_IP_PROXY_DELETE_IF_INACTIVE);
    }

    @Bean
    public Binding deleteInactiveProxyIpInDBBinding() {
        return BindingBuilder
                .bind(deleteInactiveProxyIpInDBQueue())
                .to(deleteInactiveProxyIpInDBExchange())
                .with(RabbitConstants.KEY_IP_PROXY_DELETE_IF_INACTIVE)
                .noargs();
    }

    @Bean
    public Queue saveActiveProxyIpToRedisQueue() {
        return new Queue(RabbitConstants.QUEUE_SAVE_ACTIVE_PROXY_IP_TO_REDIS, false);
    }

    /**
     * RabbitMQ队列（将可用代理保存进redis的队列，让Spring管理）
     */
    @Bean
    public Exchange saveActiveProxyIpToRedisExchange() {
        return new DirectExchange(RabbitConstants.EXCHANGE_SAVE_ACTIVE_PROXY_IP_TO_REDIS);
    }

    @Bean
    public Binding saveActiveProxyIpToRedisBinding() {
        return BindingBuilder
                .bind(saveActiveProxyIpToRedisQueue())
                .to(saveActiveProxyIpToRedisExchange())
                .with(RabbitConstants.KEY_SAVE_ACTIVE_PROXY_IP_TO_REDIS)
                .noargs();
    }
}
