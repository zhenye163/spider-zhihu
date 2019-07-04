package com.netopstec.spiderzhihu.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 基础类
 * @author zhenye 2019/7/4
 */
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Basic {
    private String id;
    private String type;
    private String url;
    private String name;
    private String avatar_url;
}
