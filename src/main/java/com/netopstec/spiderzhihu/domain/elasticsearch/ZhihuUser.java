package com.netopstec.spiderzhihu.domain.elasticsearch;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

/**
 * @author zhenye 2019/7/29
 */
@Getter
@Setter
@ToString
@Document(indexName = "user", type = "userInfo")
public class ZhihuUser {
    @Id
    private Long id;
    /**
     * 昵称
     */
    private String name;
    /**
     * 用户持有的唯一token
     */
    private String urlToken;
    /**
     * 性别
     */
    private Integer gender;
    /**
     * 回答数
     */
    private Integer answerCount;
    /**
     * 文章数
     */
    private Integer articleCount;
    /**
     * 关注数
     */
    private Integer followerCount;
    /**
     * 现居地
     */
    private String home;
    /**
     * 所在行业
     */
    private String industry;
    /**
     * 所在公司
     */
    private String company;
    /**
     * 所处职位
     */
    private String job;
    /**
     * 大学
     */
    private String university;
    /**
     * 专业
     */
    private String major;
}
