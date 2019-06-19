package com.netopstec.spiderzhihu.json;

import com.alibaba.fastjson.annotation.JSONField;
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
public class Paging {
    Boolean is_end;
    Long totals;
    String previous;
    Boolean is_start;
    String next;
}
