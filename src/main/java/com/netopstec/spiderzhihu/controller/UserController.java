package com.netopstec.spiderzhihu.controller;

import com.netopstec.spiderzhihu.crawler.FolloweeCrawler;
import com.netopstec.spiderzhihu.crawler.FollowerCrawler;
import com.netopstec.spiderzhihu.crawler.UserCrawler;
import com.netopstec.spiderzhihu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public void getUserFollowerInfoList (String urlToken, @RequestParam(required = false) Integer offset) {
        if (offset == null) {
            offset = 0;
        }
        FollowerCrawler.getUserFollowerInfoListFromZhihu(urlToken, offset);
    }

    @GetMapping("/followees")
    public void getUserFolloweeInfoList (String urlToken, @RequestParam(required = false) Integer offset) {
        if (offset == null) {
            offset = 0;
        }
        FolloweeCrawler.getUserFolloweeInfoListFromZhihu(urlToken, offset);
    }

    @Autowired
    private UserService userService;

    @GetMapping("/filter")
    public void deleteDuplicateUserList () {
        userService.removeDuplicateUserList();
    }

    @GetMapping("/transfer")
    public void transferDataFromDBToES() {
        userService.transferDataFromDBToES();
    }
}
