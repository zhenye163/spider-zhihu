package com.netopstec.spiderzhihu.controller;

import com.netopstec.spiderzhihu.crawler.FolloweeCrawler;
import com.netopstec.spiderzhihu.crawler.FollowerCrawler;
import com.netopstec.spiderzhihu.crawler.UserCrawler;
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
    public void getUserBriefInfo(String urlToken) {
        UserCrawler.getUserInfoFromZhihu(urlToken);
    }

    @GetMapping("/followers")
    public void getUserFollowerInfoList (String urlToken) {
        FollowerCrawler.getUserFollowerInfoListFromZhihu(urlToken);
    }

    @GetMapping("/followees")
    public void getUserFolloweeInfoList (String urlToken) {
        FolloweeCrawler.getUserFolloweeInfoListFromZhihu(urlToken);
    }
}
