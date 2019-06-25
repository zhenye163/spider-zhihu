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
    private String urlToken;
}
