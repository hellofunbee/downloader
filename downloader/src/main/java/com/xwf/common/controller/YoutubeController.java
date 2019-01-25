package com.xwf.common.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.Controller;
import com.xwf.common.utils.CommonUtils;
import com.xwf.common.utils.Searcher;
import com.xwf.common.utils.VideoCut_Stream;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by weifengxu on 17/7/8.
 */

public class YoutubeController extends Controller {

    final static int MAX_QUEUE_BATCH = 200;
    final static int MAX_RESULT_BATCH = 30;

    final static int MAX_QUEUE = 100000;
    final static int MAX_RESULT = 200;

    public void index() {
        render("sercher/search_yb.html");
    }

    /**
     * YouTube批量截取视频片段 多词搜索
     */
    public void yb_batch() {
        render("sercher/search_yb_batch.html");
    }

    /**
     * 查询词语
     * @throws IOException
     */

    public void getWords() throws IOException {
        List<Map> data = new ArrayList<Map>();
        JSONObject jo = new JSONObject();

        String word = getPara("word");//要查询的话
        String movie_path = getPara("path");//要查询的话
        if (movie_path == null) {
            movie_path = CommonUtils.getPathByKey("srt_path");
        }

        if (word == null || word == "") {
            jo.put("returnCode", "01");
        } else {

            //查询
            List<Map> videos = Searcher.search_subs(".srt", word, false, movie_path, MAX_QUEUE);

            CommonUtils.sort(videos, "type", 1);
            JSONArray ps = new JSONArray();

            int i = 0;
            for (Map m : videos) {
                if (i++ > MAX_RESULT) break;
                File file = ((File) m.get("file"));

                String path = file.getAbsolutePath();
                path = CommonUtils.toWebUrl(path);

                String mp3_name = file.getName();

                JSONObject o = new JSONObject();
                o.put("name", mp3_name);
                o.put("word", m.get("word"));
                o.put("s", m.get("s"));
                o.put("e", m.get("e"));
                o.put("key", word);
                o.put("path", path);
                o.put("type", (300 - (Integer) m.get("type")) / 2);
                ps.add(o);
            }
            Map m = new HashMap();
            m.put("word", word);
            m.put("videos", ps.toString());
            data.add(m);
        }
        jo.put("returnCode", "00");
        jo.put("data", data);

        renderJson(jo);
    }

    /**
     * 截取youtube视频片段
     * 多个关键词直接下载
     */
    public void getWords_batch() throws Exception {

        String download = getPara("download");

        List<Map> data = new ArrayList<Map>();
        JSONObject jo = new JSONObject();

        String word = getPara("word");//要查询的话
        String movie_path = getPara("path");//要查询的话
        if (movie_path == null) {
            movie_path = CommonUtils.getPathByKey("srt_path");
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

            List<List<Map>> vvss = Searcher.search_subs_batch(".srt", words, false, movie_path, MAX_QUEUE_BATCH);

            for (int j = 0; j < words.length; j++) {
                String w = words[j];
                //查询
                List<Map> videos = vvss.get(j);
                CommonUtils.sort(videos, "type", 1);
                JSONArray ps = new JSONArray();
                int i = 0;


                for (Map m : videos) {

                    if (i++ > MAX_RESULT_BATCH) break;
                    File file = ((File) m.get("file"));
                    String name = file.getName();

                    JSONObject o = new JSONObject();
                    o.put("name", name);
                    o.put("word", m.get("word"));
                    o.put("s", m.get("s"));
                    o.put("e", m.get("e"));
                    o.put("key", w);
                    o.put("path", file.getAbsolutePath());
                    o.put("type", (300 - (Integer) m.get("type")) / 2);
                    ps.add(o);

                }
                Map m = new HashMap();
                m.put("word", w);
                m.put("videos", ps.toString());
                data.add(m);
            }
        }
        jo.put("data", data);
        jo.put("returnCode", "00");
        renderJson(jo);

    }

    private String getybid(String name) {
        name = name.substring(name.length() - 18, name.length() - 7);
        return name;
    }

    /**
     * 截取youtube视频片段
     */
    public void ybstreamCut() {
        try {
            String s = getPara("s");
            String e = getPara("e");
            String name = getPara("name");
            String key = getPara("key");
            String word = getPara("word");

            name = getybid(name);
            VideoCut_Stream.cut(name, s, e, key, word);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * YouTube批量截取视频片段
     *
     * @throws Exception
     */
    public void ybstreamCuts() throws Exception {
        JSONArray aar = (JSONArray) JSONArray.parse(getPara("data"));
        List<Map> list = new ArrayList<Map>();
        String name = null;
        for (int i = 0; i < aar.size(); i++) {
            try {
                JSONObject obj = aar.getJSONObject(i);

                String s = obj.getString("s");
                String e = obj.getString("e");
                name = obj.getString("name");
                String key = obj.getString("key");
                String word = obj.getString("word");
                name = getybid(name);

                Map m = new HashMap();
                m.put("youtube_id", name);
                m.put("key_word", key);
                m.put("word", word);
                m.put("s", s);
                m.put("e", e);
                list.add(m);
            } catch (Exception e) {
                System.out.println(name);
                e.printStackTrace();
            }

        }

        VideoCut_Stream.cut(list);
    }
}
