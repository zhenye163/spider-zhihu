package com.netopstec.spiderzhihu.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.netopstec.spiderzhihu.domain.User;
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
    private String name;
    private String url_token;


    public static User toEntity(UserInfo userInfo) {
        User user = new User();
        user.setName(userInfo.getName());
        user.setUrlToken(userInfo.getUrl_token());
        return user;
    }
}
