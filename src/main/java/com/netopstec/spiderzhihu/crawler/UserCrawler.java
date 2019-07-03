package com.netopstec.spiderzhihu.crawler;

import cn.wanghaomiao.seimi.annotation.Crawler;
import cn.wanghaomiao.seimi.def.BaseSeimiCrawler;
import cn.wanghaomiao.seimi.spring.common.CrawlerCache;
import cn.wanghaomiao.seimi.struct.Request;
import cn.wanghaomiao.seimi.struct.Response;
import com.netopstec.spiderzhihu.common.HttpConstants;
import com.netopstec.spiderzhihu.domain.User;
import com.netopstec.spiderzhihu.domain.UserRepository;
import com.netopstec.spiderzhihu.json.UserInfo;
import com.netopstec.spiderzhihu.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.seimicrawler.xpath.JXDocument;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 爬取知乎用户信息$rootName的爬虫类
 * @author zhenye 2019/6/20
 */
@Slf4j
@Crawler(name = "user-crawler", httpTimeOut = 5000)
public class UserCrawler extends BaseSeimiCrawler{

    @Autowired
    private UserRepository userRepository;

    @Override
    public String[] startUrls() {
        return new String[]{ HttpConstants.ZHIHU_USER_BASEINFO_URL_PREFIX + USER_URL_TOKEN };
    }

    @Override
    public void start(Response response) {
        log.info("正确爬取[{}]用户的基本信息...", USER_URL_TOKEN);
        JXDocument document = response.document();
        String zhihuUserInfoJson = document.selN("body").get(0).asElement().text();
        UserInfo userInfo = JsonUtil.string2Obj(zhihuUserInfoJson, UserInfo.class);
        User user = UserInfo.toEntity(userInfo);
        User duplicateUser  = userRepository.findByUrlToken(user.getUrlToken());
        // 每个知乎用户有唯一的id和url_token，这里用id进行去重
        if (duplicateUser == null) {
            userRepository.save(user);
        }
    }

    private static String USER_URL_TOKEN;

    /**
     * 从知乎，获取一个用户的简要信息
     * @param urlToken 该用户的token(每个知乎用户有唯一的url_token)
     */
    public static void getUserBriefInfoFromZhihu(String urlToken) {
        USER_URL_TOKEN = urlToken;
        String url = HttpConstants.ZHIHU_USER_BASEINFO_URL_PREFIX + USER_URL_TOKEN;
        Request request = Request.build(url, "start");
        request.setCrawlerName("user-crawler");
        CrawlerCache.consumeRequest(request);
    }
}
