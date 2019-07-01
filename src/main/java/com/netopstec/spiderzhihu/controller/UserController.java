package com.netopstec.spiderzhihu.controller;

import cn.wanghaomiao.seimi.spring.common.CrawlerCache;
import cn.wanghaomiao.seimi.struct.Request;
import com.netopstec.spiderzhihu.common.HttpConstants;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhenye 2019/6/21
 */
@RestController
@RequestMapping("users")
public class UserController {

    @GetMapping("")
    public void getUserInfo(String crawlerName, String urlToken) {
        Request req = Request.build(HttpConstants.ZHIHU_USER_BASEINFO_URL_PREFIX + urlToken, "start", 2);
        req.setCrawlerName(crawlerName);
        CrawlerCache.consumeRequest(req);
    }

    @GetMapping("/followers")
    public void getUserFollowerInfoList (String crawlerName, String urlToken) {
        Request req = Request.build(HttpConstants.ZHIHU_USER_BASEINFO_URL_PREFIX + urlToken + "/followers", "start", 2);
        req.setCrawlerName(crawlerName);
        CrawlerCache.consumeRequest(req);
    }

    @GetMapping("/followees")
    public void getUserFolloweeInfoList (String crawlerName, String urlToken) {
        Request req = Request.build(HttpConstants.ZHIHU_USER_BASEINFO_URL_PREFIX + urlToken + "/followees", "start", 2);
        req.setCrawlerName(crawlerName);
        CrawlerCache.consumeRequest(req);
    }

    @GetMapping("/all")
    public void getAllUser() {
        // todo 爬取知乎所有用户的数据
    }
}
