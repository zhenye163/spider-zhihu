package com.netopstec.spiderzhihu.crawler;

import cn.wanghaomiao.seimi.annotation.Crawler;
import cn.wanghaomiao.seimi.def.BaseSeimiCrawler;
import cn.wanghaomiao.seimi.struct.Response;
import com.netopstec.spiderzhihu.common.HttpConstants;
import com.netopstec.spiderzhihu.domain.User;
import com.netopstec.spiderzhihu.domain.UserRepository;
import com.netopstec.spiderzhihu.json.UserInfo;
import com.netopstec.spiderzhihu.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.seimicrawler.xpath.JXDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * 爬取知乎用户信息$rootName的爬虫类
 * @author zhenye 2019/6/20
 */
@Slf4j
@Crawler(name = "user-crawler")
public class UserCrawler extends BaseSeimiCrawler{

    @Value("${zhihu.root.name}")
    private String rootName;
    @Autowired
    private UserRepository userRepository;

    @Override
    public String[] startUrls() {
        return new String[]{ HttpConstants.ZHIHU_USER_BASEINFO_URL_PREFIX + rootName };
    }

    @Override
    public void start(Response response) {
        log.info("正确爬取[{}]用户的基本信息...", rootName);
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
}
