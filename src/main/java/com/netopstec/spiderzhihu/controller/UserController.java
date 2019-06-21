package com.netopstec.spiderzhihu.controller;

import cn.wanghaomiao.seimi.spring.common.CrawlerCache;
import cn.wanghaomiao.seimi.struct.Request;
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
        Request req = Request.build("https://www.zhihu.com/api/v4/members/" + urlToken, "start", 2);
        req.setCrawlerName(crawlerName);
        CrawlerCache.consumeRequest(req);
    }

    @GetMapping("/followers")
    public void getUserFollowerInfoList (String crawlerName, String urlToken) {
        Request req = Request.build("https://www.zhihu.com/api/v4/members/" + urlToken + "/followers", "start", 2);
        req.setCrawlerName(crawlerName);
        CrawlerCache.consumeRequest(req);
    }

    @GetMapping("/followees")
    public void getUserFolloweeInfoList (String crawlerName, String urlToken) {
        Request req = Request.build("https://www.zhihu.com/api/v4/members/" + urlToken + "/followees", "start", 2);
        req.setCrawlerName(crawlerName);
        CrawlerCache.consumeRequest(req);
    }

    @GetMapping("/all")
    public void getAllUser() {
        // todo 爬取知乎所有用户的数据
    }
}
