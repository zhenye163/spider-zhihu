package com.netopstec.spiderzhihu.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @author zhenye 2019/6/20
 */
@Entity
@Getter
@Setter
@ToString
public class FollowerRelation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 关注者id
     */
    private Long followerId;
    /**
     * 被关注者id
     */
    private Long followeeId;
}
