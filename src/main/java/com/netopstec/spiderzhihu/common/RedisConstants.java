package com.netopstec.spiderzhihu.common;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zhenye 2019/7/2
 */
public class RedisConstants {

    /**
     * 通过AtomicInteger的原子性，保证redis中有10条可用代理
     */
    public static AtomicInteger activeProxyIpSize = new AtomicInteger(0);

    /**
     * 数据存放在redis的hash表名称
     */
    public final static String PROXY_IP_HASH_NAME = "proxy-ip";
}
