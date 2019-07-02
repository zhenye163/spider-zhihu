package com.netopstec.spiderzhihu.common;

/**
 * Rabbit相关的常量类
 * @author zhenye 2019/6/28
 */
public class RabbitConstants {
    /**
     * 队列名称(从web爬取可用代理，保存至DB)
     */
    public static final String QUEUE_SAVE_ACTIVE_PROXY_IP_TO_DB = "queue-save-active-proxy-ip-to-db";

    /**
     * 交换机名称(从web爬取可用代理，保存至DB)
     */
    public static final String EXCHANGE_SAVE_ACTIVE_PROXY_IP_TO_DB = "exchange-save-active-proxy-ip-to-db";

    /**
     * 路由key((从web爬取可用代理，保存至DB)
     */
    public static final String KEY_SAVE_ACTIVE_PROXY_IP_TO_DB = "key-save-active-proxy-ip-to-db";

    /**
     * 队列名称(定时删除DB中的不可用代理)
     */
    public static final String QUEUE_DELETE_INACTIVE_PROXY_IP_IN_DB = "queue-delete-inactive-proxy-ip-in-db";

    /**
     * 交换机名称(定时删除DB中的不可用代理)
     */
    public static final String EXCHANGE_IP_PROXY_DELETE_IF_INACTIVE = "exchange-delete-inactive-proxy-ip-in-db";

    /**
     * 路由key(定时删除DB中的不可用代理)
     */
    public static final String KEY_IP_PROXY_DELETE_IF_INACTIVE = "key-delete-inactive-proxy-ip-in-db";

    /**
     * 队列名称(从DB中选取可用代理，保存至redis)
     */
    public static final String QUEUE_SAVE_ACTIVE_PROXY_IP_TO_REDIS = "queue-save-active-proxy-ip-to-redis";

    /**
     * 交换机名称(从DB中选取可用代理，保存至redis)
     */
    public static final String EXCHANGE_SAVE_ACTIVE_PROXY_IP_TO_REDIS = "exchange-save-active-proxy-ip-to-redis";

    /**
     * 路由key(从DB中选取可用代理，保存至redis)
     */
    public static final String KEY_SAVE_ACTIVE_PROXY_IP_TO_REDIS = "key-save-active-proxy-ip-to-redis";
}
