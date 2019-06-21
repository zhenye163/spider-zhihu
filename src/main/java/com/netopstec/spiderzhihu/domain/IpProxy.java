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
public class IpProxy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String ip;
    private String port;
    private String address;
    private String type;
    private String speed;
    private String connectionTime;
    private String activeTime;
    private String authTime;
}
