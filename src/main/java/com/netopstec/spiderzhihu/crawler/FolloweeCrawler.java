package com.netopstec.spiderzhihu.crawler;

import cn.wanghaomiao.seimi.annotation.Crawler;
import cn.wanghaomiao.seimi.def.BaseSeimiCrawler;
import cn.wanghaomiao.seimi.spring.common.CrawlerCache;
import cn.wanghaomiao.seimi.struct.Request;
import cn.wanghaomiao.seimi.struct.Response;
import com.netopstec.spiderzhihu.domain.IpProxy;
import com.netopstec.spiderzhihu.domain.User;
import com.netopstec.spiderzhihu.domain.UserRepository;
import com.netopstec.spiderzhihu.json.FollowInfo;
import com.netopstec.spiderzhihu.json.UserInfo;
import com.netopstec.spiderzhihu.service.IpProxyService;
import com.netopstec.spiderzhihu.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.seimicrawler.xpath.JXDocument;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 爬取被关注者信息的爬虫类（“excited-vczh”关注的用户信息）
 * @author zhenye 2019/6/20
 */
@Slf4j
@Crawler(name = "user-followee-crawler")
public class FolloweeCrawler extends BaseSeimiCrawler{

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private IpProxyService ipProxyService;

    @Override
    public String proxy() {
        IpProxy ipProxy = ipProxyService.getActiveProxyIp();
        if (ipProxy != null) {
            log.info("本次用的代理是: {}:{}", ipProxy.getIp(), ipProxy.getPort());
            return ipProxy.getType().toLowerCase() + "://" + ipProxy.getIp() + ":" + ipProxy.getPort();
        }
        log.info("由于没有一个可用的代理IP，因此用的是本机IP。注意可能会被加入黑名单。");
        return super.proxy();
    }

    private static Integer LIMIT = 20;
    private static Integer OFFSET = 0;

    @Override
    public String[] startUrls() {
        return new String[]{"https://www.zhihu.com/api/v4/members/excited-vczh/followees?limit=20&offset=0"};
    }

    @Override
    public void start(Response response) {
        JXDocument document = response.document();
        String followeeListJson = document.selN("body").get(0).asElement().text();
        FollowInfo followInfo = JsonUtil.string2Obj(followeeListJson, FollowInfo.class);
        Long totals = followInfo.getPaging().getTotals();
        log.info("要爬取当前用户关注的知乎用户总数量为：" + totals);
        if (totals == 0) {
            return;
        }
        List<UserInfo> userInfoList = followInfo.getData();
        List<User> userList = new ArrayList<>();
        for (UserInfo userInfo : userInfoList) {
            User user = UserInfo.toEntity(userInfo);
            userList.add(user);
        }
        // 很明显的，每个知乎用户的有唯一的zhihu_user_id和url_token。因此入库之前需要去重（以zhihu_user_id过滤即可）。
        List<String> zhihuUserIdList = userList.stream()
                .map(User::getZhihuUserId)
                .collect(Collectors.toList());
        List<User> duplicateUserList = userRepository.findByZhihuUserIdIn(zhihuUserIdList);
        List<String> duplicateZhihuUserIdList = duplicateUserList.stream()
                .map(User::getZhihuUserId)
                .collect(Collectors.toList());
        List<User> thisTimeToAddUserList = userList.stream()
                .filter(user -> !duplicateZhihuUserIdList.contains(user.getZhihuUserId()))
                .collect(Collectors.toList());
        log.info("本次要保存用户信息的知乎用户总数量为：" + thisTimeToAddUserList.size());
        userRepository.saveAll(thisTimeToAddUserList);

        Integer hasGetTotal = FolloweeCrawler.OFFSET + FolloweeCrawler.LIMIT;
        if (hasGetTotal < totals) {
            log.info("已经爬取的数据条数{}，需要爬取的数据条数{}，因此还需要爬取下一页的数据", hasGetTotal, totals);
            FolloweeCrawler.OFFSET += FolloweeCrawler.LIMIT;
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                log.error("阻塞线程时出错");
            }
            // 间隔0.5秒后，再爬取下一页的数据（反正IP被封）
            saveNextPageFolloweeInfo();
        } else {
            log.info("已经爬取完所有数据...");
        }
    }

    /**
     * 爬取下一页的被关注者信息
     */
    private void saveNextPageFolloweeInfo() {
        String url = "https://www.zhihu.com/api/v4/members/excited-vczh/followees?limit=" + FolloweeCrawler.LIMIT + "&offset=" + FolloweeCrawler.OFFSET;
        Request request = new Request(url, "start");
        push(request);
        request.setCrawlerName("user-followee-crawler");
        CrawlerCache.consumeRequest(request);
    }
}