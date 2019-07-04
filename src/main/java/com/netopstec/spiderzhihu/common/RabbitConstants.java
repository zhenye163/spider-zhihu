package com.netopstec.spiderzhihu.common;

/**
 * Rabbit相关的常量类
 * @author zhenye 2019/6/28
 */
public class RabbitConstants {
    /**
     * 队列名称(从web爬取代理校验其是否可用，并保存进DB)
     */
    public static final String QUEUE_CHECK_PROXY_IP_AND_SAVE_TO_DB = "queue-check-proxy-ip-and-save-to-db";

    /**
     * 交换机名称(从web爬取代理校验其是否可用，并保存进DB)
     */
    public static final String EXCHANGE_CHECK_PROXY_IP_AND_SAVE_TO_DB = "exchange-check-proxy-ip-and-save-to-db";

    /**
     * 路由key(从web爬取代理校验其是否可用，并保存进DB)
     */
    public static final String KEY_CHECK_PROXY_IP_AND_SAVE_TO_DB = "key-check-proxy-ip-and-save-to-db";
}
