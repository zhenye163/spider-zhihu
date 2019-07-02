package com.netopstec.spiderzhihu.job;

import cn.wanghaomiao.seimi.spring.common.CrawlerCache;
import cn.wanghaomiao.seimi.struct.Request;
import com.netopstec.spiderzhihu.common.HttpConstants;
import com.netopstec.spiderzhihu.common.RabbitConstants;
import com.netopstec.spiderzhihu.common.RedisConstants;
import com.netopstec.spiderzhihu.domain.IpProxy;
import com.netopstec.spiderzhihu.domain.IpProxyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 基于Spring提供的注解@Scheduled，实现定时任务
 * @author zhenye 2019/7/2
 */
@Slf4j
@Component
public class ScheduleJob {

    @Autowired
    private IpProxyRepository ipProxyRepository;
    @Autowired
    private AmqpTemplate rabbitTemplate;
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 每20分钟检测一次DB中所有代理的可用性。
     * 删除无用的代理；当可用代理不足50条时，启动爬虫重新爬取代理网的可用代理
     */
    @Scheduled(cron = "0 */20 * * * ?")
    public void intervalTransferActiveProxyIpFromWebToDB () {
        log.info("基于Spring提供的调度任务，测试DB中的代理IP");
        List<IpProxy> ipProxyList = ipProxyRepository.findAll();
        if (ipProxyList.size() <= 50) {
            log.info("DB中可用代理已经不足50条，再次启动爬虫爬取可用代理...");
            Request request1 = Request.build( HttpConstants.IP89_IP_PROXY_URL_PREFIX , "start");
            request1.setCrawlerName("ipProxy-89ip-crawler");
            CrawlerCache.consumeRequest(request1);

            Request request2 = Request.build( HttpConstants.XICI_IP_PROXY_URL_PREFIX , "start");
            request2.setCrawlerName("ipProxy-xici-crawler");
            CrawlerCache.consumeRequest(request2);

            Request request3 = Request.build( HttpConstants.YUN_IP_PROXY_URL_PREFIX , "start");
            request3.setCrawlerName("ipProxy-yun-crawler");
            CrawlerCache.consumeRequest(request3);
        }
        for (IpProxy ipProxy : ipProxyList) {
            rabbitTemplate.convertAndSend(RabbitConstants.QUEUE_DELETE_INACTIVE_PROXY_IP_IN_DB, ipProxy);
        }
    }

    /**
     * 每5分钟将DB的10条可用代理数放入redis中
     */
    @Scheduled(cron = "0 */5 * * * ?")
    public void intervalTransferActiveProxyIpFromDBToRedis() {
        log.info("基于Spring提供的调度任务，将DB中的10条可用IP导入redis中");
        redisTemplate.delete(RedisConstants.PROXY_IP_HASH_NAME);
        RedisConstants.activeProxyIpSize.set(0);
        List<IpProxy> ipProxyList = ipProxyRepository.findAll();
        for (IpProxy ipProxy : ipProxyList) {
            rabbitTemplate.convertAndSend(RabbitConstants.QUEUE_SAVE_ACTIVE_PROXY_IP_TO_REDIS, ipProxy);
        }
    }
}
