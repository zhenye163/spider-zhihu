package com.netopstec.spiderzhihu.common;

/**
 * Rabbit相关的常量类
 * @author zhenye 2019/6/28
 */
public class RabbitConstants {
    /**
     * 队列名称(如果代理可用，新增)
     */
    public static final String QUEUE_IP_PROXY_SAVE_IF_ACTIVE = "queue-ip-proxy-save-if-active";

    /**
     * 交换机名称(如果代理可用，新增)
     */
    public static final String EXCHANGE_IP_PROXY_SAVE_IF_ACTIVE = "exchange-ip-proxy-save-if-active";

    /**
     * 路由key(如果代理可用，新增)
     */
    public static final String KEY_IP_PROXY_SAVE_IF_ACTIVE = "key-ip-proxy-save-if-active";

    /**
     * 队列名称(如果代理不可用，删除)
     */
    public static final String QUEUE_IP_PROXY_DELETE_IF_INACTIVE = "queue-ip-proxy-delete-if-inactive";

    /**
     * 交换机名称(如果代理不可用，删除)
     */
    public static final String EXCHANGE_IP_PROXY_DELETE_IF_INACTIVE = "exchange-ip-proxy-delete-if-inactive";

    /**
     * 路由key(如果代理不可用，删除)
     */
    public static final String KEY_IP_PROXY_DELETE_IF_INACTIVE = "key-ip-proxy-delete-if-inactive";
}
