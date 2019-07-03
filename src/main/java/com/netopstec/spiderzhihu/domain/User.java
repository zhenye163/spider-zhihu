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
    private String name;
    @Column(unique = true)
    private String urlToken;
}
