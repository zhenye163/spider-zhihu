package com.netopstec.spiderzhihu.handler;

import com.netopstec.spiderzhihu.common.RabbitConstants;
import com.netopstec.spiderzhihu.domain.IpProxy;
import com.netopstec.spiderzhihu.service.IpProxyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 消费者接收并处理消息的类
 * @author zhenye 2019/6/28
 */
@Component
@Slf4j
public class IpProxyHandler {

    @Autowired
    IpProxyService ipProxyService;

    @RabbitListener(queues = RabbitConstants.QUEUE_SAVE_ACTIVE_PROXY_IP_TO_DB)
    public void handlerSaveActiveProxyIpToDB(IpProxy ipProxy){
        log.info("[将可用代理保存进DB]消费者即将验证代理[{}:{}]是否可用...", ipProxy.getIp(), ipProxy.getPort());
        ipProxyService.saveActiveProxyIpToDB(ipProxy);
    }

    @RabbitListener(queues = RabbitConstants.QUEUE_DELETE_INACTIVE_PROXY_IP_IN_DB)
    public void handlerDeleteInactiveProxyIpInDB(IpProxy ipProxy){
        log.info("[删除DB中不可用代理]消费者即将验证代理[{}:{}]是否可用...", ipProxy.getIp(), ipProxy.getPort());
        ipProxyService.deleteInactiveProxyIpInDB(ipProxy);
    }

    @RabbitListener(queues = RabbitConstants.QUEUE_SAVE_ACTIVE_PROXY_IP_TO_REDIS)
    public void handlerSaveActiveProxyIpToRedis(IpProxy ipProxy){
        log.info("[将可用代理保存进redis]消费者即将验证代理[{}:{}]是否可用...", ipProxy.getIp(), ipProxy.getPort());
        ipProxyService.saveActiveProxyIpToRedis(ipProxy);
    }
}
