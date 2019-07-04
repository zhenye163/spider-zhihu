package com.netopstec.spiderzhihu.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author zhenye 2019/6/20
 */
public interface IpProxyRepository extends JpaRepository<IpProxy, Long> {

    List<IpProxy> findByIpAndPort(String ip, String port);

    @Query(nativeQuery = true, value = "select * from ip_proxy where status = 1 order by success_times desc")
    List<IpProxy> findHighAvailableProxyIpList();
}
