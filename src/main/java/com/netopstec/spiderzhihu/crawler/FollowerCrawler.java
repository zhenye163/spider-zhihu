package com.netopstec.spiderzhihu.crawler;

import cn.wanghaomiao.seimi.annotation.Crawler;
import cn.wanghaomiao.seimi.def.BaseSeimiCrawler;
import cn.wanghaomiao.seimi.spring.common.CrawlerCache;
import cn.wanghaomiao.seimi.struct.Request;
import cn.wanghaomiao.seimi.struct.Response;
import com.netopstec.spiderzhihu.common.HttpConstants;
import com.netopstec.spiderzhihu.domain.*;
import com.netopstec.spiderzhihu.json.FollowInfo;
import com.netopstec.spiderzhihu.json.UserInfo;
import com.netopstec.spiderzhihu.service.IpProxyService;
import com.netopstec.spiderzhihu.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.seimicrawler.xpath.JXDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

/**
 * 爬取关注者信息的爬虫类（关注了$rootName的用户信息）
 * @author zhenye 2019/6/19
 */
@Slf4j
@Crawler(name = "user-follower-crawler", useUnrepeated = false, httpTimeOut = 8000)
public class FollowerCrawler extends BaseSeimiCrawler {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FollowerRelationRepository followerRelationRepository;
    @Autowired
    private IpProxyService ipProxyService;

    @Override
    public String getUserAgent() {
        return HttpConstants.refreshMyUserAgent();
    }

    @Override
    public String proxy() {
        IpProxy ipProxy = ipProxyService.getActiveProxyIp();
        if (ipProxy != null) {
            log.debug("本次用的代理是: [{}:{}]", ipProxy.getIp(), ipProxy.getPort());
            return ipProxy.getType().toLowerCase() + "://" + ipProxy.getIp() + ":" + ipProxy.getPort();
        }
        log.info("由于没有一个可用的代理IP，因此用的是本机IP。注意可能会被加入黑名单。");
        return super.proxy();
    }

    @Override
    public void handleErrorRequest(Request request) {
        log.info("爬虫出现异常，继续发送爬虫请求直至爬取到关注该用户的所有知乎用户数据。");
        saveNextPageFollowerInfo();
    }

    @Override
    public String[] startUrls() {
        if (USER_URL_TOKEN  == null || "".equals(USER_URL_TOKEN )) {
            log.error("要爬取关注当前用户的知乎用户信息，需要传入该知乎用户的url_token信息，否则无法爬取数据...");
            return null;
        }
        log.info("正在爬取关注[{}]的知乎用户信息...", USER_URL_TOKEN );
        return new String[]{
                HttpConstants.ZHIHU_USER_BASEINFO_URL_PREFIX + USER_URL_TOKEN + "/followers" + HttpConstants.ZHIHU_USER_INFO_SUFFIX + "&limit=" + LIMIT + "&offset=" + OFFSET
        };
    }

    @Override
    public void start(Response response) {
        User followeeUser = userRepository.findByUrlToken(USER_URL_TOKEN );
        if (followeeUser == null) {
            log.error("要预先保存[{}]的用户信息，否则无法保证关联的关注关系", USER_URL_TOKEN );
            return;
        }
        JXDocument document = response.document();
        String followerListJson = document.selN("body").get(0).asElement().text();
        // 爬取的知乎用户数据中，有些headline字段的值可能有双引号。删除内部的内容防止解析报错
        followerListJson = JsonUtil.removeTheStringFieldValue(followerListJson, false, "headline", "gender");
        FollowInfo followInfo = JsonUtil.string2Obj(followerListJson, FollowInfo.class);
        Long totals = followInfo.getPaging().getTotals();
        log.info("总共要爬取关注当前用户的知乎用户总数量为：" + totals);
        if (totals == 0) {
            return;
        }
        List<UserInfo> userInfoList = followInfo.getData();
        for (UserInfo userInfo : userInfoList) {
            User user = UserInfo.toEntity(userInfo);
            try {
                userRepository.save(user);
                FollowerRelation followerRelation = new FollowerRelation();
                followerRelation.setFolloweeId(followeeUser.getId());
                followerRelation.setFollowerId(user.getId());
                followerRelationRepository.save(followerRelation);
            } catch (DataIntegrityViolationException e) {
                log.debug("不满足user.url_token的唯一约束，即之前已经保存过该用户[{}]的信息...", user.getUrlToken());
            }
        }
        Integer hasGetTotal = OFFSET + LIMIT;
        if (hasGetTotal < totals) {
             log.info("已经爬取的数据条数[{}]，需要爬取的数据条数[{}]，因此还需要爬取下一页的数据", hasGetTotal, totals);
            OFFSET += LIMIT;
            saveNextPageFollowerInfo();
        } else {
            log.info("已经爬取完关注[{}]的所有知乎用户的信息...", USER_URL_TOKEN );
        }
    }

    /**
     * 爬取下一页的关注者信息
     */
    private void saveNextPageFollowerInfo() {
        String url = HttpConstants.ZHIHU_USER_BASEINFO_URL_PREFIX + USER_URL_TOKEN + "/followers" + HttpConstants.ZHIHU_USER_INFO_SUFFIX + "&limit=" + LIMIT + "&offset=" + OFFSET;
        Request request = Request.build(url, "start");
        push(request);
    }

    private static String USER_URL_TOKEN;
    private static Integer LIMIT;
    private static Integer OFFSET;

    /**
     * 从知乎，获取一个用户的所有关注者的信息
     * @param urlToken 该用户的token(每个知乎用户有唯一的url_token)
     */
    public static void getUserFollowerInfoListFromZhihu(String urlToken) {
        USER_URL_TOKEN = urlToken;
        LIMIT = 20;
        OFFSET = 0;
        String url = HttpConstants.ZHIHU_USER_BASEINFO_URL_PREFIX + USER_URL_TOKEN + "/followers" + HttpConstants.ZHIHU_USER_INFO_SUFFIX + "&limit=" + LIMIT + "&offset=" + OFFSET;
        Request request = Request.build(url, "start");
        request.setCrawlerName("user-follower-crawler");
        CrawlerCache.consumeRequest(request);
    }
}
