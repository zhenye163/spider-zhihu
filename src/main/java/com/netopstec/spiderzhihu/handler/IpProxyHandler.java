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

    @RabbitListener(queues = RabbitConstants.QUEUE_CHECK_PROXY_IP_AND_SAVE_TO_DB)
    public void handlerCheckProxyIpAndSaveToDB(IpProxy ipProxy){
        log.debug("[将代理保存进DB]消费者即将保存并验证该代理[{}:{}]是否可用...", ipProxy.getIp(), ipProxy.getPort());
        ipProxyService.checkProxyIpAndSaveToDB(ipProxy);
    }
}
