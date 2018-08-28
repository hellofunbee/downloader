package com.xwf.common.utils;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.ProxyConfig;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

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
        //构造一个webClient 模拟Chrome 浏览器
        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        //屏蔽日志信息
        LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log",
                "org.apache.commons.logging.impl.NoOpLog");

        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit")
                .setLevel(Level.OFF);

        java.util.logging.Logger.getLogger("org.apache.commons.httpclient")
                .setLevel(Level.OFF);
        //支持JavaScript
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setActiveXNative(false);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setTimeout(10000);


        //设置一个运行JavaScript的时间
//                webClient.waitForBackgroundJavaScript(5000);
        webClient.getOptions().setJavaScriptEnabled(true);

        return webClient;
    }


    public String getHtml(String url) throws IOException {

        webClient.getOptions().setJavaScriptEnabled(false);
        HtmlPage rootPage = webClient.getPage(url);
        webClient.getOptions().setJavaScriptEnabled(true);

        return rootPage.asXml();


    }

    private static Map<String, String> getHeader() {
        Map hearder = new HashMap<String, String>();
        hearder.put("Host", "music.163.com");
        hearder.put("Referer", "https://music.163.com/");
        hearder.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");

        return hearder;
    }
}
