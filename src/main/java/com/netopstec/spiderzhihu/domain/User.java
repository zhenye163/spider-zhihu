package com.netopstec.spiderzhihu.domain;

import lombok.Data;

import javax.persistence.*;

/**
 * @author zhenye 2019/6/19
 */
@Entity
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
