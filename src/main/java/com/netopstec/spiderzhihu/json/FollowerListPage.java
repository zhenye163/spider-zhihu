package com.netopstec.spiderzhihu.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author zhenye 2019/6/19
 */
@Getter
@Setter
@ToString
public class FollowerListPage {
    Paging paging;
    List<UserInfo> data;
}
