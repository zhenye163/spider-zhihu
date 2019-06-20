package com.netopstec.spiderzhihu.json;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.util.List;

/**
 * @author zhenye 2019/6/19
 */
@Getter
@Setter
@ToString
public class FollowInfo {
    private Paging paging;
    private List<UserInfo> data;
}
