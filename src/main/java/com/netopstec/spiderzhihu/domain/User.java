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
    @Column(name="zhihu_user_id", unique=true)
    private String zhihuUserId;
    private String name;
    private String headline;
    private Integer gender;
    private String zhihuUserType;
    private String urlToken;
    private String avatarUrl;
    private Integer isAdvertiser;
    private Integer isVip;
}
