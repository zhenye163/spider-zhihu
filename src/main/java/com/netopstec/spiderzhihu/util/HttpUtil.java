package com.netopstec.spiderzhihu.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
/**
 * @author zhenye 2019/6/28
 */
public class HttpUtil {

    private static final String TEST_WEB_URL = "http://www.baidu.com/";

    /**
     * 测试某个代理是否可用
     * @param ip ip地址
     * @param port 端口号
     * @param proxyType 代理类型
     * @return 测试结果
     */
    public static boolean checkIpIsActive(String ip, Integer port, Proxy.Type proxyType) {
        boolean isActive;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(TEST_WEB_URL);
            InetSocketAddress addr = new InetSocketAddress(ip, port);
            Proxy proxy = new Proxy(proxyType, addr);
            connection = (HttpURLConnection) url.openConnection(proxy);
            connection.setConnectTimeout(4 * 1000);
            connection.setInstanceFollowRedirects(false);
            connection.setReadTimeout(6 * 1000);
            int resCode = connection.getResponseCode();
            isActive =  resCode == 200;
        }catch (IOException e1){
            isActive = false;
        }finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return isActive;
    }
}
