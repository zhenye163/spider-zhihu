package com.netopstec.spiderzhihu.crawler;

import cn.wanghaomiao.seimi.annotation.Crawler;
import cn.wanghaomiao.seimi.def.BaseSeimiCrawler;
import cn.wanghaomiao.seimi.struct.Request;
import cn.wanghaomiao.seimi.struct.Response;
import com.netopstec.spiderzhihu.common.HttpConstants;
import com.netopstec.spiderzhihu.common.RabbitConstants;
import com.netopstec.spiderzhihu.domain.IpProxy;
import com.netopstec.spiderzhihu.service.IpProxyService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.select.Elements;
import org.seimicrawler.xpath.JXDocument;
import org.seimicrawler.xpath.JXNode;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 爬取可免费代理的服务器IP地址的爬虫类
 * 89免费代理（http://www.89ip.cn/）
 * @author zhenye 2019/7/1
 */
@Slf4j
@Crawler(name = "ipProxy-89ip-crawler", useUnrepeated = false)
public class IpProxy89ipCrawler extends BaseSeimiCrawler {

    @Autowired
    private AmqpTemplate rabbitTemplate;
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
            log.info("本次用的代理是: [{}:{}]", ipProxy.getIp(), ipProxy.getPort());
            return ipProxy.getType().toLowerCase() + "://" + ipProxy.getIp() + ":" + ipProxy.getPort();
        }
        log.info("由于没有一个可用的代理IP，因此用的是本机IP。注意可能会被加入黑名单。");
        return super.proxy();
    }

    @Override
    public String[] startUrls() {
        return new String[]{ HttpConstants.IP89_IP_PROXY_URL_PREFIX };
    }

    private static Integer pageNum = 1;

    @Override
    public void start(Response response) {
        log.info("正在爬取89ip免费代理第{}页的代理IP...", pageNum);
        JXDocument jxDocument = response.document();
        if (jxDocument == null) {
            pageNum++;
            push(Request.build(HttpConstants.YUN_IP_PROXY_URL_PREFIX + "?stype=1&page=" + pageNum, IpProxyYunCrawler::start));
            return;
        }
        JXNode node = jxDocument.selNOne("//*[@class=\"layui-table\"]");
        Elements ipProxyList = node.asElement().children().get(1).children();
        for(int i = 1; i < ipProxyList.size();i++) {
            Elements ipInfo = ipProxyList.get(i).children();
            String proxyIp = ipInfo.get(0).text();
            String proxyPort = ipInfo.get(1).text();
            String proxyAddress = ipInfo.get(2).text();

            IpProxy ipProxy = new IpProxy();
            ipProxy.setIp(proxyIp);
            ipProxy.setPort(proxyPort);
            ipProxy.setAddress(proxyAddress);
            ipProxy.setType("HTTP");
            // 将爬取到的代理放到消息队列中
            rabbitTemplate.convertAndSend(RabbitConstants.QUEUE_IP_PROXY_SAVE_IF_ACTIVE, ipProxy);
        }
        if (pageNum < 20) {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                log.error("线程阻塞异常");
            }
            pageNum++;
            push(Request.build(HttpConstants.IP89_IP_PROXY_URL_PREFIX + "index_" + pageNum + ".html", IpProxy89ipCrawler::start));
        } else {
            pageNum = 1;
            log.info("已经爬取完前100页的所有89ip免费代理");
        }
    }
}
