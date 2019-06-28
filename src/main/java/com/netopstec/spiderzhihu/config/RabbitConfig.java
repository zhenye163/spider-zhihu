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
    public Queue ipProxyQueue() {
        return new Queue(RabbitConstants.QUEUE_IP_PROXY, false);
    }

    @Bean
    public Exchange ipProxyExchange() {
        return new DirectExchange(RabbitConstants.EXCHANGE_IP_PROXY);
    }

    @Bean
    public Binding ipProxyBinding() {
        return BindingBuilder.bind(ipProxyQueue()).to(ipProxyExchange()).with(RabbitConstants.KEY_IP_PROXY).noargs();
    }

}
