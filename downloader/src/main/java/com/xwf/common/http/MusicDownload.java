package com.xwf.common.http;

import com.xwf.common.utils.CommonUtils;
import com.xwf.common.utils.ThreadPoolUtils;
import com.xwf.common.utils.WebClientUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Created by weifengxu on 2018/8/29.
 */
public class MusicDownload {


    public static void main(String args[]) throws IOException {
        ThreadPoolUtils poo = new ThreadPoolUtils(0,10);

        String target = "";
        String path = CloudMusicDownload.class.getClassLoader().getResource("test.html").getPath();
        String html = org.apache.commons.io.FileUtils.readFileToString(new File(path));
        Document doc = Jsoup.parse(html);
        Elements elements = doc.select("a.s-fc1");

        Map proxy = getProxy();
        int i = 0;
        for (Element e : elements) {
            poo.execute(new CloudMusicDownload(new WebClientUtil(), e, proxy));

            System.out.println(i++);

        }
    }

    private static Map getProxy() {

        try {
            return CommonUtils.getProxy(1);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("获取代理ip失败！");
            return null;
        }

    }
}
