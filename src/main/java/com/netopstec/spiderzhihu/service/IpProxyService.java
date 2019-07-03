package com.netopstec.spiderzhihu.service;

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
    public void saveActiveProxyIpToDB(IpProxy ipProxy) {
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
     * 删除无用的代理
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteInactiveProxyIpInDB(IpProxy ipProxy) {
        boolean isActive = HttpUtil.checkIpIsActive(ipProxy.getIp(), Integer.valueOf(ipProxy.getPort()), Proxy.Type.HTTP);
        if (!isActive) {
            ipProxyRepository.delete(ipProxy);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveActiveProxyIpToRedis(IpProxy ipProxy) {
        if (RedisConstants.activeProxyIpSize.get() < 10) {
            boolean isActive = HttpUtil.checkIpIsActive(ipProxy.getIp(), Integer.valueOf(ipProxy.getPort()), Proxy.Type.HTTP);
            if (isActive) {
                if (RedisConstants.activeProxyIpSize.get() < 10) {
                    redisTemplate.opsForHash().put(RedisConstants.PROXY_IP_HASH_NAME, String.valueOf(ipProxy.getId()), JsonUtil.obj2String(ipProxy));
                    RedisConstants.activeProxyIpSize.incrementAndGet();
                }
            } else {
                ipProxyRepository.delete(ipProxy);
            }
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
