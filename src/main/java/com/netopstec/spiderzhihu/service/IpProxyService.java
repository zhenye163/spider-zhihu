package com.netopstec.spiderzhihu.service;

import cn.wanghaomiao.seimi.spring.common.CrawlerCache;
import cn.wanghaomiao.seimi.struct.Request;
import com.netopstec.spiderzhihu.common.HttpConstants;
import com.netopstec.spiderzhihu.common.RabbitConstants;
import com.netopstec.spiderzhihu.domain.IpProxy;
import com.netopstec.spiderzhihu.domain.IpProxyRepository;
import com.netopstec.spiderzhihu.util.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.Proxy;
import java.util.List;
import java.util.Random;

/**
 * @author zhenye 2019/6/21
 */
@Service
@Slf4j
public class IpProxyService {

    @Autowired
    private IpProxyRepository ipProxyRepository;
    @Autowired
    private AmqpTemplate rabbitTemplate;

    @Transactional(rollbackFor = Exception.class)
    public void saveProxyIpIfIsActive(IpProxy ipProxy) {
        List<IpProxy> isExistedIpProxyList = ipProxyRepository.findByIpAndPort(ipProxy.getIp(), ipProxy.getPort());
        if (isExistedIpProxyList.size() > 0) {
            return;
        }
        boolean isActive = HttpUtil.checkIpIsActive(ipProxy.getIp(), Integer.valueOf(ipProxy.getPort()), Proxy.Type.HTTP);
        if (isActive) {
            ipProxyRepository.save(ipProxy);
        }
    }

    /**
     * 基于Spring提供的调度任务，每20分钟检测一次DB中所有代理的可用性。
     * 删除无用的代理；当可用代理不足50条时，启动爬虫重新爬取代理网的可用代理
     */
    @Scheduled(cron = "0 */20 * * * ?")
    public void intervalCheckProxyIp () {
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
            rabbitTemplate.convertAndSend(RabbitConstants.QUEUE_IP_PROXY_DELETE_IF_INACTIVE, ipProxy);
        }
    }

    /**
     * 删除无用的代理；当可用代理不足10条时，启动爬虫重新爬取西刺代理网的可用代理
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteProxyIpIfIsInactive(IpProxy ipProxy) {
        boolean isActive = HttpUtil.checkIpIsActive(ipProxy.getIp(), Integer.valueOf(ipProxy.getPort()), Proxy.Type.HTTP);
        if (!isActive) {
            ipProxyRepository.delete(ipProxy);
        }
    }

    /**
     * 获取一条可用的HTTP代理(HTTPS的代理，使用方式不一样，待续)
     * @return 可用的代理
     */
    public IpProxy getActiveProxyIp() {
        List<IpProxy> ipProxyList = ipProxyRepository.findByType("HTTP");
        if (ipProxyList.size() == 0) {
            return null;
        } else {
            Random random = new Random();
            int index = random.nextInt(ipProxyList.size());
            return ipProxyList.get(index);
        }
    }
}
