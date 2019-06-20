package com.netopstec.spiderzhihu.crawler;

import cn.wanghaomiao.seimi.annotation.Crawler;
import cn.wanghaomiao.seimi.def.BaseSeimiCrawler;
import cn.wanghaomiao.seimi.struct.Response;
import com.netopstec.spiderzhihu.domain.User;
import com.netopstec.spiderzhihu.domain.UserRepository;
import com.netopstec.spiderzhihu.json.FollowInfo;
import com.netopstec.spiderzhihu.json.UserInfo;
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
    }
}
