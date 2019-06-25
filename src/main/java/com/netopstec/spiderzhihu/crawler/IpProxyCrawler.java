package com.netopstec.spiderzhihu.crawler;

import cn.wanghaomiao.seimi.annotation.Crawler;
import cn.wanghaomiao.seimi.def.BaseSeimiCrawler;
import cn.wanghaomiao.seimi.struct.Request;
import cn.wanghaomiao.seimi.struct.Response;
import com.netopstec.spiderzhihu.domain.IpProxy;
import com.netopstec.spiderzhihu.service.IpProxyService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.select.Elements;
import org.seimicrawler.xpath.JXDocument;
import org.seimicrawler.xpath.JXNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;
import java.util.List;

/**
 * 使用代理的策略需要改变：
 * 1. 在项目启动的时候，使用多线程从西次代理中爬取足够多的可用代理！（20页过滤出可用代理）
 * 2. 获取代理爬取别的资源时，直接从数据库中拿数据即可，报错503时切换另外一条可用代理。
 *
 * 原因：
 * 1. `proxy()`方法会调用两次，在这里获取代理时还对代理的可用性进行检验的话，爬取速率极低；
 * 2. 免费代理的数量太少了，而且基本都是在短时间内就会失效。删除了无效代理后，可用代理池代理
 * 数目很快就会变为0，因此需要调整线程池的维护。
 *
 * 爬取可免费代理的服务器IP地址的爬虫类
 * @author zhenye 2019/6/20
 */
@Slf4j
@Crawler(name = "ipProxy-crawler", useUnrepeated = false)
public class IpProxyCrawler extends BaseSeimiCrawler {

    @Autowired
    private IpProxyService ipProxyService;

    @Override
    public String[] startUrls() {
        return new String[]{"https://www.xicidaili.com/wt/"};
    }

    @Override
    public void start(Response response) {
        log.info("正在爬取代理IP...");
        JXDocument jxDocument = response.document();
        JXNode node = jxDocument.selNOne("//*[@id=\"ip_list\"]");
        Elements ipProxyList = node.asElement().children().get(0).children();
        List<IpProxy> proxyIpList = new ArrayList<>();
        for(int i = 1; i < ipProxyList.size();i++) {
            Elements ipInfo = ipProxyList.get(i).children();
            String proxyIp = ipInfo.get(1).text();
            String proxyPort = ipInfo.get(2).text();
            String proxyAddress = ipInfo.get(3).text();
            String proxyType = ipInfo.get(5).text();
            String proxySpeed = ipInfo.get(6).child(0).attr("title");
            String proxyConnectionTime = ipInfo.get(7).child(0).attr("title");
            String proxyActiveTime = ipInfo.get(8).text();
            String proxyAuthTime = ipInfo.get(9).text();

            IpProxy ipProxy = new IpProxy();
            ipProxy.setIp(proxyIp);
            ipProxy.setPort(proxyPort);
            ipProxy.setAddress(proxyAddress);
            ipProxy.setType(proxyType);
            ipProxy.setSpeed(proxySpeed);
            ipProxy.setConnectionTime(proxyConnectionTime);
            ipProxy.setActiveTime(proxyActiveTime);
            ipProxy.setAuthTime(proxyAuthTime);

            proxyIpList.add(ipProxy);
        }
        // 测试这些代理IP是否可用；如果可用，则刷新数据库中的备用代理IP
         ipProxyService.saveActiveProxyIpList(proxyIpList);
    }

    /**
     * 基于Spring提供的调度任务，每第[0,10,20,30,40,50]分钟新增可用代理IP
     */
    @Scheduled(cron = "0 */10 * * * ?")
    public void intervalAddActiveProxyIp() {
        log.info("基于Spring提供的调度任务，新增可用的代理IP");
        push(Request.build("https://www.xicidaili.com/wt/", IpProxyCrawler::start));
    }

    /**
     * 基于Spring提供的调度任务，每个小时开头删除无用的代理IP
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void intervalRemoveInactiveProxyIp () {
        log.info("基于Spring提供的调度任务，删除不可用的代理IP");
        ipProxyService.deleteInactiveProxyIpList();
    }
}
