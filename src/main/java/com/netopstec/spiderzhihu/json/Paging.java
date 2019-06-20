package com.netopstec.spiderzhihu.json;

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
    private Boolean is_end;
    private Long totals;
    private String previous;
    private Boolean is_start;
    private String next;
}
