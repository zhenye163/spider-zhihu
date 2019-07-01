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

    @RabbitListener(queues = RabbitConstants.QUEUE_IP_PROXY_SAVE_IF_ACTIVE)
    public void handlerIpProxySaveIfActive(IpProxy ipProxy){
        log.info("消费者拿到代理[{}:{}]，如果该代理可用会将其保存到DB中...", ipProxy.getIp(), ipProxy.getPort());
        ipProxyService.saveProxyIpIfIsActive(ipProxy);
    }

    @RabbitListener(queues = RabbitConstants.QUEUE_IP_PROXY_DELETE_IF_INACTIVE)
    public void handlerIpProxyDeleteIfInactive(IpProxy ipProxy){
        log.info("消费者拿到代理[{}:{}]，如果该代理不可用会将其删除...", ipProxy.getIp(), ipProxy.getPort());
        ipProxyService.deleteProxyIpIfIsInactive(ipProxy);
    }
}
