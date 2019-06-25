package com.netopstec.spiderzhihu.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author zhenye 2019/6/19
 */
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByUrlTokenIn(List<String> urlTokenList);

    User findByUrlToken(String urlToken);
}
