package com.netopstec.spiderzhihu.service;

import com.netopstec.spiderzhihu.domain.IpProxy;
import com.netopstec.spiderzhihu.domain.IpProxyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhenye 2019/6/21
 */
@Service
@Slf4j
public class IpProxyService {

    @Autowired
    private IpProxyRepository ipProxyRepository;

    /**
     * 保存可用的免费代理IP
     * @param proxyIpList
     */
    public void saveActiveProxyIpList(List<IpProxy> proxyIpList) {
        if (proxyIpList.size() == 0) {
            return;
        }
        List<IpProxy> activeIpProxyList = new ArrayList<>();
        for (IpProxy ipProxy : proxyIpList) {
           boolean isActive = checkIpProxyIsActive(ipProxy);
           if (isActive) {
               activeIpProxyList.add(ipProxy);
           }
        }
        if (activeIpProxyList.size() > 0) {
            for (IpProxy ipProxy : activeIpProxyList) {
                List<IpProxy> existedIpProxy = ipProxyRepository.findByIpAndPort(ipProxy.getIp(), ipProxy.getPort());
                if (existedIpProxy.size() == 0) {
                    log.info("代理[{}:{}]可用，新增一条代理记录", ipProxy.getIp(), ipProxy.getPort());
                    ipProxyRepository.save(ipProxy);
                }
            }
        }
    }

    /**
     * 删除不可用的免费代理IP
     */
    public void deleteInactiveProxyIpList() {
        List<IpProxy> ipProxyList = ipProxyRepository.findAll();
        List<IpProxy> inactiveIpProxyList = new ArrayList<>();
        for (IpProxy ipProxy : ipProxyList) {
            boolean isActive = checkIpProxyIsActive(ipProxy);
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
     * 测试IP是否可用
     * @param ipProxy
     * @return
     */
    private boolean checkIpProxyIsActive(IpProxy ipProxy) {
        boolean isActive;
        String ip = ipProxy.getIp();
        Integer port = Integer.valueOf(ipProxy.getPort());
        HttpURLConnection connection = null;
        try {
            URL url = new URL("http://www.baidu.com/");
            InetSocketAddress addr = new InetSocketAddress(ip, port);
            Proxy proxy = new Proxy(Proxy.Type.HTTP, addr);
            connection = (HttpURLConnection) url.openConnection(proxy);
            connection.setConnectTimeout(4 * 1000);
            connection.setInstanceFollowRedirects(false);
            connection.setReadTimeout(6 * 1000);
            int resCode = connection.getResponseCode();
            isActive =  resCode == 200;
        }catch (IOException e1){
            isActive = false;
        }finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return isActive;
    }

    /**
     * 获取一条可用的代理
     * @return 可用的代理
     */
    public IpProxy getActiveProxyIp() {
        IpProxy activeProxyIp = null;
        List<IpProxy> ipProxyList = ipProxyRepository.findAll();
        List<IpProxy> inactiveIpProxyList = new ArrayList<>();
        for (int i = 0; i < ipProxyList.size(); i++) {
            IpProxy ipProxy = ipProxyList.get(i);
            boolean isActive = checkIpProxyIsActive(ipProxy);
            if (isActive) {
                activeProxyIp = ipProxy;
                break;
            } else {
                inactiveIpProxyList.add(ipProxy);
            }
        }
        if (inactiveIpProxyList.size() > 0) {
            log.info("检测到有[{}]条不可用的代理，清除这些不可用的代理。", inactiveIpProxyList.size());
            ipProxyRepository.deleteInBatch(inactiveIpProxyList);
        }
        return activeProxyIp;
    }
}
