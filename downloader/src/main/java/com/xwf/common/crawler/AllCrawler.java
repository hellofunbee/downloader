package com.xwf.common.crawler;

import com.xwf.common.Callback.MCallback;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * Created by weifengxu on 17/7/9.
 */
public class AllCrawler implements PageProcessor {
    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);
    private MCallback mCallback;

    public AllCrawler() {
    }

    public AllCrawler(MCallback mCallback) {
        this.mCallback = mCallback;
    }

    public void process(Page page) {
//        page.addTargetRequests(page.getHtml().links().regex("(https://github\\.com/\\w+/\\w+)").all());
//        page.putField("author", page.getUrl().regex("https://github\\.com/(\\w+)/.*").toString());
//        page.putField("name", page.getHtml().xpath("//h1[@class='public']/strong/a/text()").toString());
//        if (page.getResultItems().get("name") == null) {
//            //skip this page
//            page.setSkip(true);
//        }
//        page.putField("readme", page.getHtml().xpath("//div[@id='readme']/tidyText()"));
//        System.out.println(page.getHtml());
        String img = page.getHtml().xpath("//meta[@property='og:video']/@content").toString();
        System.out.println(page.getHtml());
        if (mCallback != null) {
            mCallback.call(img);
        }
    }

    public Site getSite() {
        return site;
    }
}
