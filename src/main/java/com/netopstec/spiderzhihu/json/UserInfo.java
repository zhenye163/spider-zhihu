package com.netopstec.spiderzhihu.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.netopstec.spiderzhihu.domain.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 用户信息json对应的实体类
 * @author zhenye 2019/6/19
 */
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserInfo extends Basic{
    private String url_token;
    private Integer gender;
    private Integer answer_count;
    private Integer articles_count;
    private Integer follower_count;
    private List<Location> locations;
    private Business business;
    private List<Employment> employments;
    private List<Education> educations;

    public static User toEntity(UserInfo userInfo) {
        User user = new User();
        // 昵称
        user.setName(userInfo.getName());
        // url_token---用户拥有唯一的url_token
        user.setUrlToken(userInfo.getUrl_token());
        // 性别(1: 男，0: 女，-1: 未填)
        user.setGender(userInfo.getGender());
        // 回答数
        user.setAnswerCount(userInfo.getAnswer_count());
        // 文章数
        user.setArticleCount(userInfo.getArticles_count());
        // 关注数
        user.setFollowerCount(userInfo.getFollower_count());
        if (userInfo.locations.size() > 0) {
            // 现居地
            user.setHome(userInfo.locations.get(0).getName());
        }
        // 行业
        user.setIndustry(userInfo.getBusiness().getName());
        if (userInfo.getEmployments().size() > 0) {
            // 公司
            if (userInfo.getEmployments().get(0).getCompany() != null) {
                user.setCompany(userInfo.getEmployments().get(0).getCompany().getName());
            }
            // 岗位
            if (userInfo.getEmployments().get(0).getJob() != null) {
                user.setJob(userInfo.getEmployments().get(0).getJob().getName());
            }
        }
        if (userInfo.getEducations().size() > 0) {
            // 大学
            if (userInfo.getEducations().get(0).getSchool() != null) {
                user.setUniversity(userInfo.getEducations().get(0).getSchool().getName());
            }
            // 专业
            if (userInfo.getEducations().get(0).getMajor() != null) {
                user.setMajor(userInfo.getEducations().get(0).getMajor().getName());
            }
        }
        return user;
    }
}
