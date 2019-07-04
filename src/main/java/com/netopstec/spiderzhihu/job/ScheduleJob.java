package com.netopstec.spiderzhihu.job;

import com.netopstec.spiderzhihu.common.RabbitConstants;
import com.netopstec.spiderzhihu.common.RedisConstants;
import com.netopstec.spiderzhihu.crawler.IpProxyXiCiCrawler;
import com.netopstec.spiderzhihu.domain.IpProxy;
import com.netopstec.spiderzhihu.domain.IpProxyRepository;
import com.netopstec.spiderzhihu.util.JsonUtil;
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
     * 每15分钟检测一次DB中所有代理的可用性，会删除DB中不可用(失败3次以上)的代理
     */
    @Scheduled(cron = "0 */15 * * * ?")
    public void intervalCheckProxyIpIsActiveInDB () {
        log.info("基于Spring提供的调度任务，测试DB中的代理IP是否可用");
        List<IpProxy> ipProxyList = ipProxyRepository.findAll();
        for (IpProxy ipProxy : ipProxyList) {
            rabbitTemplate.convertAndSend(RabbitConstants.QUEUE_CHECK_PROXY_IP_AND_SAVE_TO_DB, ipProxy);
        }
    }

    /**
     * 每10分钟将DB中的前10条高可用IP导入redis中
     */
    @Scheduled(cron = "0 */10 * * * ?")
    public void intervalTransferActiveProxyIpFromDBToRedis() {
        log.info("基于Spring提供的调度任务，将DB中的前10条高可用IP导入redis中");
        List<IpProxy> ipProxyList = ipProxyRepository.findHighAvailableProxyIpList();
        redisTemplate.delete(RedisConstants.PROXY_IP_HASH_NAME);
        for (int i = 0,j = 0; i < ipProxyList.size() && j < 10; i++,j++) {
            IpProxy ipProxy = ipProxyList.get(i);
            redisTemplate.opsForHash().put(RedisConstants.PROXY_IP_HASH_NAME, String.valueOf(ipProxy.getId()), JsonUtil.obj2String(ipProxy));
        }
    }

    /**
     * 每30分钟，将西祠网首页的代理导入DB中
     */
    @Scheduled(cron = "0 */30 * * * ?")
    public void intervalGetProxyIpFromXiciWebIndex() {
        log.info("基于Spring提供的调度任务，将西刺代理网首页的代理保存进DB");
        IpProxyXiCiCrawler.getProxyIpFromXiciWebByPageNum(1);
    }
}
