package com.xwf.common.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Record;
import com.xwf.common.Callback.MCallback;
import com.xwf.common.crawler.PhantomJSUtil;
import com.xwf.common.crawler.PicCrawler;
import com.xwf.common.crawler.VideoCrawler;
import com.xwf.common.dao.TvDao;
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
        String tv_id = getPara("tv_id");//要查询的话
        Record tv = null;
        if (tv_id != null) {
            tv = TvDao.findById(tv_id);
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
                List<Map> videos = Searcher.search2(w, false, tv);
                CommonUtils.sort(videos, "type", 1);
                JSONArray ps = new JSONArray();
                for (Map m : videos) {

                    String path = (String) m.get("file");
                    JSONObject o = new JSONObject();
                    o.put("path", CommonUtils.toWebUrl(path));
                    o.put("type", (300 - (Integer) m.get("type")) / 2);
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


        List<Map> result = new ArrayList<Map>();
        List<Record> tvs = TvDao.findAll();

        for (Record tv : tvs) {

            Map m = new HashMap();
            m.put("tv_id", tv.get("tv_id"));
            m.put("name", tv.get("tv_name"));

            result.add(m);


        }

        jo.put("returnCode", "00");
        jo.put("data", result);
        renderJson(jo);


    }


}
