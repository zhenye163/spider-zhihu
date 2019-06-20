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
    private String id;
    private String name;
    private String headline;
    private Integer gender;
    private String user_type;
    private String url_token;
    private String avatar_url;
    private Boolean is_advertiser;
    private VipInfo vip_info;


    public static User toEntity(UserInfo userInfo) {
        User user = new User();
        user.setZhihuUserId(userInfo.getId());
        user.setName(userInfo.getName());
        user.setHeadline(userInfo.getHeadline());
        user.setGender(userInfo.getGender());
        user.setZhihuUserType(userInfo.getUser_type());
        user.setUrlToken(userInfo.getUrl_token());
        user.setAvatarUrl(userInfo.getAvatar_url());
        user.setIsAdvertiser(userInfo.getIs_advertiser() ? 1 : 0);
        user.setIsVip(userInfo.getVip_info().getIs_vip() ? 1 : 0);
        return user;
    }
}
