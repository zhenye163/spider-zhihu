package com.netopstec.spiderzhihu.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author zhenye 2019/6/19
 */
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUrlToken(String urlToken);

    @Query(nativeQuery = true, value = "SELECT id FROM USER ORDER BY id DESC limit 1")
    Long selectLargestId();

    @Query(nativeQuery = true, value = "SELECT * FROM USER ORDER BY id limit ?1, ?2")
    List<User> findByPageQuery(Integer offset, Integer pageSize);
}
