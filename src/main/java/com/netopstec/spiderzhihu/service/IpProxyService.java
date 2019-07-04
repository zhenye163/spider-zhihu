package com.netopstec.spiderzhihu.service;

import com.netopstec.spiderzhihu.common.ProxyIpStatus;
import com.netopstec.spiderzhihu.common.RedisConstants;
import com.netopstec.spiderzhihu.domain.IpProxy;
import com.netopstec.spiderzhihu.domain.IpProxyRepository;
import com.netopstec.spiderzhihu.util.HttpUtil;
import com.netopstec.spiderzhihu.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private StringRedisTemplate redisTemplate;

    @Transactional(rollbackFor = Exception.class)
    public void checkProxyIpAndSaveToDB(IpProxy ipProxy) {
        List<IpProxy> isExistedIpProxyList = ipProxyRepository.findByIpAndPort(ipProxy.getIp(), ipProxy.getPort());
        Integer preSuccessTimes = 0, preFailTimes = 0;
        if (isExistedIpProxyList.size() > 0) {
            ipProxy = isExistedIpProxyList.get(0);
            preSuccessTimes = ipProxy.getSuccessTimes();
            preFailTimes = ipProxy.getFailTimes();
        }
        boolean isActive = HttpUtil.checkIpIsActive(ipProxy.getIp(), Integer.valueOf(ipProxy.getPort()), Proxy.Type.HTTP);
        if (isActive) {
            ipProxy.setStatus(ProxyIpStatus.SUCCESS.getValue());
            ipProxy.setSuccessTimes(++preSuccessTimes);
            ipProxy.setFailTimes(0);
            ipProxyRepository.save(ipProxy);
        } else if (preFailTimes < 2) {
            ipProxy.setStatus(ProxyIpStatus.FAIL.getValue());
            ipProxy.setSuccessTimes(0);
            ipProxy.setFailTimes(++preFailTimes);
            ipProxyRepository.save(ipProxy);
        } else {
            ipProxyRepository.delete(ipProxy);
        }
    }

    /**
     * 获取一条可用的HTTP代理
     * @return 可用的代理
     */
    public IpProxy getActiveProxyIp() {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(RedisConstants.PROXY_IP_HASH_NAME);
        List ipProxyList = new ArrayList<>(entries.values());
        if (entries.size() == 0) {
            return null;
        } else {
            Random random = new Random();
            int index = random.nextInt(ipProxyList.size());
            return JsonUtil.string2Obj((String) ipProxyList.get(index), IpProxy.class);
        }
    }
}
