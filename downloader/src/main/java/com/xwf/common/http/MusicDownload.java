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
import java.net.URLDecoder;
import java.util.Map;

/**
 * Created by weifengxu on 2018/8/29.
 */
public class MusicDownload {


    public static void main(String args[]) throws IOException {
        boolean isReverse = false;
        if ("0".equals(args[0])) {
            isReverse = true;
        }

        int _s = Integer.parseInt(args[1]);
        int _e = args[2] == null ? -1 : Integer.parseInt(args[2]);


        ThreadPoolUtils poo = new ThreadPoolUtils(0, 10);

        String target = "";
        String path = CloudMusicDownload.class.getClassLoader().getResource("test.html").getPath();
        String html = org.apache.commons.io.FileUtils.readFileToString(new File(path));
        Document doc = Jsoup.parse(html);
        Elements dls = doc.select("dl.f-cb");

        //倒序
        if (isReverse) {
            dls = reverse(dls);
        }
        int i = 0;
        for (int s = 0; s < dls.size(); s++) {
            Element dl = dls.get(s);

            System.out.println("*************************" + dl.getElementsByTag("dt").text() + "*************************");

            Elements as = dl.select("a.s-fc1");
            if (isReverse) {
                as = reverse(as);
            }

            Map proxy = getProxy();
            for (Element a : as) {
                if (i < _s)
                    continue;
                if (_e != -1 && i > _e)
                    break;

                i++;
                String href = a.attr("href");
                href = href.substring(href.indexOf("=") + 1);
                href = URLDecoder.decode(href, "utf-8");
                System.out.println("[" + i + "]" + href);
                poo.execute(new CloudMusicDownload(new WebClientUtil(), a, proxy));

            }
        }

    }

    private static Elements reverse(Elements es) {
        Elements tdls = new Elements();
        for (int i = es.size() - 1; i >= 0; i--) {
            tdls.add(es.get(i));

        }


        return tdls;
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
