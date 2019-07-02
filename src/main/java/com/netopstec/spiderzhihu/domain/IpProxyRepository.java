package com.netopstec.spiderzhihu.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author zhenye 2019/6/20
 */
public interface IpProxyRepository extends JpaRepository<IpProxy, Long> {

    List<IpProxy> findByIpAndPort(String ip, String port);
}
