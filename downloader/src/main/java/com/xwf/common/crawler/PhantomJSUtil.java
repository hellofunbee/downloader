package com.xwf.common.crawler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by weifengxu on 17/7/11.
 */
public class PhantomJSUtil {
    private static String projectPath = System.getProperty("user.dir");
    private static String jsPath = PhantomJSUtil.class.getClassLoader().getResource("all.js").getPath();
    private static String exePath = "/Volumes/自媒体/tool/phantomjs-2.1.1-macosx/bin/phantomjs";
//    private static String exePath = "/usr/java/phantomjs-2.1.1-linux-x86_64/bin/phantomjs";
    private static List<JSONObject> arr;
    private static Integer count = 0;

    public static void main(String[] args) throws Exception {
        getTarget("https://www.instagram.com/leomessi/");
        System.out.println(arr.toString());
    }

    /**
     * 程序调用入口
     *
     * @param url 传入url
     * @return
     * @throws Exception
     */
    public static List<Object> getImgs(String url) throws Exception {
        arr = new ArrayList<JSONObject>();
        count = 0;

        getTarget(url);
        List<Object> str = new ArrayList<Object>();
        if (arr.size() > 0) {
            for (JSONObject jo : arr) {
                JSONObject data = (JSONObject) jo.get("data");
                if (data != null && data.get("imgs") != null) {
                    JSONArray urls = (JSONArray) data.get("imgs");
                    if (urls != null && urls.size() > 0) {
                        for (int i = 0; i < urls.size(); i++) {
                            str.add(urls.get(i));
                        }
                    }

                }
            }
        }
        return str;
    }

    /**
     * 递归解析多页的数据 比较耗时
     *
     * @param url
     * @throws Exception
     */
    private static void getTarget(String url) throws Exception {
        String html = getParseredHtml2(url);
        count++;
        System.out.println("html: " + html);
        if (html != null && !"".equals(html)) {
            JSONObject jo = JSONObject.parseObject(html);
            if ("00".equals(jo.get("returnCode"))) {
                arr.add(jo);
                if (count < 2) {
                    JSONObject data = (JSONObject) jo.get("data");
                    if (data != null && data.get("more") != null) {
                        getTarget(data.getString("more"));
                    }
                }
            }
        }

    }


    // 调用phantomjs程序，并传入js文件，并通过流拿回需要的数据。
    public static String getParseredHtml2(String url) throws IOException {
        Runtime rt = Runtime.getRuntime();
        Process p = rt.exec(exePath + " " + jsPath + " " + url);
        System.out.println(exePath + " " + jsPath + " " + url);
        InputStream is = p.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuffer sbf = new StringBuffer();
        String tmp = "";
        while ((tmp = br.readLine()) != null) {
            sbf.append(tmp);
        }
        return sbf.toString();
    }


}
