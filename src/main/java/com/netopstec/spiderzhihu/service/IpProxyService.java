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
import java.util.Random;

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
     * 获取一条可用的代理
     * @return 可用的代理
     */
    public IpProxy getActiveProxyIp() {
        List<IpProxy> ipProxyList = ipProxyRepository.findAll();
        if (ipProxyList.size() == 0) {
            return null;
        } else {
            Random random = new Random();
            int index = random.nextInt(ipProxyList.size());
            return ipProxyList.get(index);
        }
    }
}
