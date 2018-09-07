package com.xwf.common.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.Controller;
import com.xwf.common.utils.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by weifengxu on 17/7/8.
 */
public class MusicController extends Controller {

    final static int MAX_QUEUE = 200;
    final static int MAX_RESULT_BATCH = 30;
    final static int MAX_RESULT = 100;

    public void index() throws Exception {
        render("sercher/searchAudio.html");
    }

    public void localSerch() throws Exception {
        render("sercher/searchAudio1.html");
    }

    public void localSerch1() throws Exception {
        render("sercher/searchAudio1_batch.html");
    }

    /**
     * 批量下载
     *
     * @throws Exception
     */
    public void batchDownload() throws Exception {
        JSONArray aar = (JSONArray) JSONArray.parse(getPara("data"));
        List<Map> list = new ArrayList<Map>();
        for (int i = 0; i < aar.size(); i++) {
            JSONObject obj = aar.getJSONObject(i);

            String s = obj.getString("s");
            String e = obj.getString("e");
            String name = obj.getString("name");
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

        }

        VideoCut_Stream.cut(list);
    }

    public void getWords1() throws IOException {
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
            List<java.util.Map> videos = Searcher.search_subs(".srt", word, false, movie_path, MAX_QUEUE);

            CommonUtils.sort(videos, "type", 1);
            JSONArray ps = new JSONArray();

            int i = 0;
            for (java.util.Map m : videos) {
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
     */
    public void ybstreamCut() throws IOException, InterruptedException {


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
     * 截取youtube视频片段
     * 多个关键词直接下载
     */
    public void batchKeys() throws Exception {

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

            List<List<Map>> vvss = Searcher.search_subs_batch(".srt", words, false, movie_path, MAX_QUEUE);

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

    public void getWords() {
        List<Map> data = new ArrayList<Map>();
        JSONObject jo = new JSONObject();

        String word = getPara("word");//要查询的话
        String movie_path = getPara("path");//要查询的话
        if (movie_path == null) {
            movie_path = CommonUtils.getPathByKey("base_path") + "music/";
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
                List<java.util.Map> videos = Searcher.search(".mp3", w, false, movie_path);
                CommonUtils.sort(videos, "type", 1);
                JSONArray ps = new JSONArray();
                for (java.util.Map m : videos) {
                    File file = ((File) m.get("file"));

                    String path = file.getAbsolutePath();
                    path = CommonUtils.toWebUrl(path);

                    String mp3_name = file.getParentFile().getName();

                    JSONObject o = new JSONObject();
                    o.put("name", mp3_name.substring(0, mp3_name.indexOf("--")));
                    o.put("path", path);
                    o.put("type", (300 - (Integer) m.get("type")) / 2);
                    ps.add(o);

                }

                Map m = new HashMap();
                m.put("word", w);
                m.put("videos", ps.toString());
                data.add(m);
            }

        }

        jo.put("returnCode", "00");
        jo.put("data", data);

        renderJson(jo);
    }

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

    public void getDirs() {
        JSONObject jo = new JSONObject();

        String base_path = CommonUtils.getPathByKey("base_path") + "music/";
        List<Map> result = new ArrayList<Map>();
        File[] files = new File(base_path).listFiles();

        for (File f : files) {

            if (f.isDirectory()) {
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
