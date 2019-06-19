package com.netopstec.spiderzhihu.domain;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author zhenye 2019/6/19
 */
public interface UserRepository extends JpaRepository<User, Long> {
}
