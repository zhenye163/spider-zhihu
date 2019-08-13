package com.netopstec.spiderzhihu.domain.elasticsearch;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

/**
 * @author zhenye 2019/7/29
 */
@Getter
@Setter
@ToString
@Document(indexName = "user", type = "userInfo")
@Setting(settingPath = "json/user_setting.json")
@Mapping(mappingPath = "json/user_mapping.json")
public class ZhihuUser {
    /*
    ES中，text和keyword类型字段的区别：
    text 数据类型被用来索引长文本，比如说电子邮件的主体部分或者一款产品的介绍。
    这些文本会被分析，在建立索引前会将这些文本进行分词，转化为词的组合，建立索引。
    允许ES来检索这些词语。text数据类型不能用来排序和聚合。

    keyword 数据类型用来建立电子邮箱地址、姓名、邮政编码和标签等数据，不需要进行分词。
    可以被用来检索过滤、排序和聚合。
    keyword 类型字段只能用本身来进行检索。

    字符串型字段，系统默认是指定为“text”类型。

    实际案例中，具体指定某个字段的数据类型，需要判断该字段是否需要分词。
    如果该字段值的分词没有意义，即选用keyword类型，否则使用text类型。
    如：某人的姓名/邮箱/地址/职业等信息，分词没有实际意义。这些值的整体才构成意义，因此，选用keyword类型。
     */

    @Id
    private Long id;
    /**
     * 昵称
     */
    private String name;
    /**
     * 用户持有的唯一token
     */
    private String urlToken;
    /**
     * 性别
     */
    private Integer gender;
    /**
     * 回答数
     */
    private Integer answerCount;
    /**
     * 文章数
     */
    private Integer articleCount;
    /**
     * 关注数
     */
    private Integer followerCount;
    /**
     * 现居地
     */
    private String home;
    /**
     * 所在行业
     */
    private String industry;
    /**
     * 所在公司
     */
    private String company;
    /**
     * 所处职位
     */
    private String job;
    /**
     * 大学
     */
    private String university;
    /**
     * 专业
     */
    private String major;
}
