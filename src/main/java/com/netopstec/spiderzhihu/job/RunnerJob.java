package com.netopstec.spiderzhihu.job;

import com.netopstec.spiderzhihu.crawler.IpProxyXiCiCrawler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 项目启动时执行的初始化任务
 * @author zhenye 2019/7/4
 */
@Slf4j
@Component
public class RunnerJob implements CommandLineRunner {

    @Override
    public void run(String... args){
        log.info("项目启动时，会将西祠网的前10页代理保存进DB");
        for (int i = 1; i <= 10; i++) {
            IpProxyXiCiCrawler.getProxyIpFromXiciWebByPageNum(i);
        }
    }
}
