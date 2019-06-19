package com.netopstec.spiderzhihu.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author zhenye 2019/6/19
 */
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserInfo {
    String avatar_url_template;
    String name;
    String headline;
    Integer gender;
    String user_type;
    String id;
}
