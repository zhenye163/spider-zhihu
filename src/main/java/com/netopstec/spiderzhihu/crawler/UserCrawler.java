package com.netopstec.spiderzhihu.crawler;

import cn.wanghaomiao.seimi.annotation.Crawler;
import cn.wanghaomiao.seimi.def.BaseSeimiCrawler;
import cn.wanghaomiao.seimi.struct.Response;
import com.netopstec.spiderzhihu.domain.User;
import com.netopstec.spiderzhihu.domain.UserRepository;
import com.netopstec.spiderzhihu.json.UserInfo;
import com.netopstec.spiderzhihu.util.JsonUtil;
import org.seimicrawler.xpath.JXDocument;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 爬取知乎用户信息的爬虫类
 * @author zhenye 2019/6/20
 */
@Crawler(name = "user-crawler")
public class UserCrawler extends BaseSeimiCrawler{

    @Autowired
    private UserRepository userRepository;

    @Override
    public String[] startUrls() {
        return new String[]{"https://www.zhihu.com/api/v4/members/excited-vczh"};
    }

    @Override
    public void start(Response response) {
        JXDocument document = response.document();
        String zhihuUserInfoJson = document.selN("body").get(0).asElement().text();
        UserInfo userInfo = JsonUtil.string2Obj(zhihuUserInfoJson, UserInfo.class);
        User user = UserInfo.toEntity(userInfo);
        User duplicateUser  = userRepository.findByZhihuUserId(user.getZhihuUserId());
        // 每个知乎用户有唯一的id和url_token，这里用id进行去重
        if (duplicateUser == null) {
            userRepository.save(user);
        }
    }
}
