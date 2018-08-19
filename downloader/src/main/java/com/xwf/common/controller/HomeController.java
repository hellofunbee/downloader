package com.xwf.common.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.xwf.common.Callback.MCallback;
import com.xwf.common.crawler.PhantomJSUtil;
import com.xwf.common.crawler.PicCrawler;
import com.xwf.common.crawler.VideoCrawler;
import com.xwf.common.utils.CommonUtils;
import com.xwf.common.utils.Searcher;
import com.xwf.common.utils.ZipCompressor;
import us.codecraft.webmagic.Spider;

import java.io.File;
import java.util.*;

/**
 * Created by weifengxu on 17/7/8.
 */
public class HomeController extends Controller {
    public void index() throws Exception {
        render("sercher/searchMain.html");
//        render("downloader/pic.html");
    }

    @ActionKey("picurl")
    public void getPicurl() {
        Spider.create(new PicCrawler(new MCallback() {
            public void call(String url) {
                JSONObject jo = new JSONObject();
                jo.put("returnCode", "00");
                jo.put("url", url);
                renderJson(jo);
            }
        })).addUrl(getPara("url")).thread(1).run();

    }

    @ActionKey("videourl")
    public void getVideourl() {
        Spider.create(new VideoCrawler(new MCallback() {
            public void call(String url) {
                JSONObject jo = new JSONObject();
                jo.put("returnCode", "00");
                jo.put("url", url);
                renderJson(jo);
            }
        })).addUrl(getPara("url")).thread(1).run();
    }

    @ActionKey("allurl")
    public void getAllurl() throws Exception {
        List<Object> urls = PhantomJSUtil.getImgs(getPara("url"));
        JSONObject jo = null;
        if (urls != null && urls.size() > 0) {
            jo = new JSONObject();
            jo.put("returnCode", "00");
            jo.put("data", urls);
        } else {
            jo = new JSONObject();
            jo.put("returnCode", "01");
        }

        renderJson(jo);
    }


    @ActionKey("getWords")
    public void getWords() {
        List<Map> data = new ArrayList<Map>();
        JSONObject jo = new JSONObject();

        String word = getPara("word");//要查询的话
        String movie_path = getPara("path");//要查询的话
        if (movie_path == null) {
            movie_path = CommonUtils.getPathByKey("base_path");
        }

        if (word == null || word == "") {
            jo.put("returnCode", "01");
        } else {
            String[] words;
            if (word.indexOf(",") != -1 || word.indexOf("，") != -1) {
                word = word.replace("，", ",");
                words = word.split(",");
            } else {
                words = new String[]{word};
            }
            for (String w : words) {

                //查询
                List<Map> videos = Searcher.search(".mp4",w, false, movie_path);
                CommonUtils.sort(videos,"type",1);
              JSONArray ps = new JSONArray();
                for (Map m : videos) {
                    String path = ((File) m.get("file")).getAbsolutePath();
                    path = CommonUtils.toWebUrl(path);
                    JSONObject o = new JSONObject();
                    o.put("path",path);
                    o.put("type",(300-(Integer) m.get("type"))/2);
                    ps.add(o);

                }

                Map m = new HashMap();
                m.put("word", w);
                m.put("videos", ps.toString());
                data.add(m);
            }

            jo.put("returnCode", "00");
        }
        jo.put("data", data);

        renderJson(jo);
    }

    @ActionKey("download_zip")
    public void download_zip() {
        String para = getPara("fileUrls");
        if (para == null)
            render("空空如也！");
        else {
            String zip_path = CommonUtils.getPathByKey("zip_path") + UUID.randomUUID() + ".zip";
            String p = CommonUtils.tolocalUrl(para);

            File[] fs;
            if (p.indexOf(",") == -1) {
                fs = new File[]{new File(p)};
            } else {
                String[] ps = p.split(",");
                fs = new File[ps.length];
                for (int i = 0; i < ps.length; i++) {
                    fs[i] = new File(ps[i]);
                }

            }
            ZipCompressor.zipFiles(fs, new File(zip_path));
            renderFile(new File(zip_path));

        }

    }

    /**
     * 获取主目录
     */
    @ActionKey("getDirs")
    public void getDirs() {
        JSONObject jo = new JSONObject();

        String base_path = CommonUtils.getPathByKey("base_path");
        List<Map> result = new ArrayList<Map>();
        File[] files = new File(base_path).listFiles();

        for (File f : files) {

            if (f.isDirectory() && f.getName().indexOf("ziptemp") == -1&&f.getName().indexOf("music") == -1) {
                Map m = new HashMap();
                m.put("path", f.getAbsolutePath());
                m.put("name", f.getName());

                result.add(m);
            }

        }

        jo.put("returnCode", "00");
        jo.put("data", result);
        renderJson(jo);


    }


}
