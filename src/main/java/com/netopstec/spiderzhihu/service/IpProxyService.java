package com.netopstec.spiderzhihu.service;

import com.netopstec.spiderzhihu.domain.IpProxy;
import com.netopstec.spiderzhihu.domain.IpProxyRepository;
import com.netopstec.spiderzhihu.util.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.Proxy;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author zhenye 2019/6/21
 */
@Service
@Slf4j
public class IpProxyService {

    @Autowired
    private IpProxyRepository ipProxyRepository;

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
     * 删除不可用的免费代理IP
     */
    public void deleteInactiveProxyIpList() {
        // todo 这里需要改成多线程的方式校验并删除不可用的代理，否则效率太低
        List<IpProxy> ipProxyList = ipProxyRepository.findAll();
        List<IpProxy> inactiveIpProxyList = new CopyOnWriteArrayList<>();
        for (IpProxy ipProxy : ipProxyList) {
            boolean isActive = HttpUtil.checkIpIsActive(ipProxy.getIp(), Integer.valueOf(ipProxy.getPort()), Proxy.Type.HTTP);
            if (!isActive) {
                inactiveIpProxyList.add(ipProxy);
            }
        }
        log.info("本次要删除的不可用代理IP数为：{}", inactiveIpProxyList.size());
        if (inactiveIpProxyList.size() > 0) {
            ipProxyRepository.deleteInBatch(inactiveIpProxyList);
        }
    }

    /**
     * 获取一条可用的代理
     * @return 可用的代理
     */
    public IpProxy getActiveProxyIp() {
        List<IpProxy> ipProxyList = ipProxyRepository.findAll();
        if (ipProxyList.size() == 0) {
            return null;
        } else {
            return ipProxyList.get(0);
        }
    }
}
