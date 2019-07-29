package com.netopstec.spiderzhihu.domain.elasticsearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @author zhenye 2019/7/29
 */
@Repository
public interface ZhihuUserRepository extends ElasticsearchRepository<ZhihuUser, Long> {
}
