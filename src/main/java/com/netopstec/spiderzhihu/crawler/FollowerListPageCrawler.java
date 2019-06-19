package com.netopstec.spiderzhihu.crawler;

import cn.wanghaomiao.seimi.annotation.Crawler;
import cn.wanghaomiao.seimi.def.BaseSeimiCrawler;
import cn.wanghaomiao.seimi.struct.Response;
import com.netopstec.spiderzhihu.json.FollowerListPage;
import com.netopstec.spiderzhihu.util.JsonUtil;
import org.seimicrawler.xpath.JXDocument;

/**
 * @author zhenye 2019/6/19
 */
@Crawler(name = "followerListPage")
public class FollowerListPageCrawler extends BaseSeimiCrawler {
    @Override
    public String[] startUrls() {
        return new String[]{"https://www.zhihu.com/api/v4/members/excited-vczh/followers?limit=10&offset=10"};
    }

    @Override
    public void start(Response response) {
        JXDocument document = response.document();
        String userInfoJsonStr = document.selN("body").get(0).asElement().text();
        FollowerListPage followerListPage = JsonUtil.string2Obj(userInfoJsonStr, FollowerListPage.class);
        System.out.println(followerListPage.getData());
    }
}
