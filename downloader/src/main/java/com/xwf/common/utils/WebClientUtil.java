package com.xwf.common.utils;

import com.gargoylesoftware.htmlunit.ProxyConfig;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.xwf.common.http.PooledClientFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by weifengxu on 2018/8/26.
 */
public class WebClientUtil {
    public WebClient webClient;

    public WebClientUtil() {
        webClient = getWebClient();
    }


    public void setProxy(Map proxy) {
        if (proxy != null) {
            String ip = (String) proxy.get("ip");
            Integer port = Integer.parseInt((String) proxy.get("port"));
            webClient.getOptions().setProxyConfig(new ProxyConfig(ip, port));

        }
    }

    private WebClient getWebClient() {
        return PooledClientFactory.getInstance().getClient();
    }


    public String getHtml(String url) throws IOException {

        webClient.getOptions().setJavaScriptEnabled(false);
        HtmlPage rootPage = webClient.getPage(url);
        webClient.getOptions().setJavaScriptEnabled(true);

        return rootPage.asXml();


    }

    private Map<String, String> getHeader() {
        Map hearder = new HashMap<String, String>();
        hearder.put("Host", "music.163.com");
        hearder.put("Referer", "https://music.163.com/");
        hearder.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");

        return hearder;
    }
}
