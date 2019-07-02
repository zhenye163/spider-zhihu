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
 * 西刺网（https://www.xicidaili.com/wt/）
 * @author zhenye 2019/6/20
 */
@Slf4j
@Crawler(name = "ipProxy-xici-crawler", useUnrepeated = false)
public class IpProxyXiCiCrawler extends BaseSeimiCrawler {

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
        return new String[]{ HttpConstants.XICI_IP_PROXY_URL_PREFIX };
    }

    private static Integer pageNum = 1;

    @Override
    public void start(Response response) {
        log.info("正在爬取西刺免费代理第{}页的代理IP...", pageNum);
        JXDocument jxDocument = response.document();
        JXNode node = jxDocument.selNOne("//*[@id=\"ip_list\"]");
        Elements ipProxyList = node.asElement().children().get(0).children();
        for(int i = 1; i < ipProxyList.size();i++) {
            Elements ipInfo = ipProxyList.get(i).children();
            String proxyIp = ipInfo.get(1).text();
            String proxyPort = ipInfo.get(2).text();
            String proxyAddress = ipInfo.get(3).text();
            String proxyType = ipInfo.get(5).text();

            IpProxy ipProxy = new IpProxy();
            ipProxy.setIp(proxyIp);
            ipProxy.setPort(proxyPort);
            ipProxy.setAddress(proxyAddress);
            ipProxy.setType(proxyType);
            // 将爬取到的代理放到消息队列中
            rabbitTemplate.convertAndSend(RabbitConstants.QUEUE_SAVE_ACTIVE_PROXY_IP_TO_DB, ipProxy);
        }
        if (pageNum < 10) {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                log.error("线程阻塞异常");
            }
            pageNum++;
            push(Request.build(HttpConstants.XICI_IP_PROXY_URL_PREFIX + pageNum, IpProxyXiCiCrawler::start));
        } else {
            pageNum = 1;
            log.info("已经爬取完前10页的所有西刺免费代理");
        }
    }

}
