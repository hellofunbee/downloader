package com.xwf.common.http;

import com.alibaba.fastjson.JSONObject;
import com.xwf.common.utils.CommonUtils;
import com.xwf.common.utils.LrcUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import subtitleFile.FormatSRT;
import subtitleFile.IOClass;
import subtitleFile.TimedTextObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by weifengxu on 2018/8/11.
 */
public class NetEaseCloudMusic {
    
    static String get_play_lists = "https://music.163.com/discover/playlist/?order=hot&cat=%E5%85%A8%E9%83%A8&limit=35&offset=##";

    static String song_url = "http://music.163.com/song/media/outer/url?id=##.mp3";
    static String lrc_url = "http://music.163.com/api/song/lyric?os=pc&id=##&lv=-1&kv=-1&tv=-1";
    static String save_path = "/Users/weifengxu/Downloads/music/";
    static String play_list = "https://music.163.com/playlist?id=##";
    static String temp = save_path + "temp.txt";

    static Map allMp3 = null;


    public static void main(String arg[]) throws Exception {
        //312734124
//        getAllFromList("312734124");
        getPlay_lists(120);


    }

    public static void getPlay_lists(int count) throws Exception {

        for (int i = 0; i < count; i++) {
            String url = get_play_lists.replace("##", String.valueOf(i * 35));
            String html = HttpUtils.sendGet(url, getHeader());
            Document doc = Jsoup.parse(html);

            Elements elements = doc.select("li div.u-cover a.msk");
            for (Element e : elements) {
                String name = e.attr("title");
                name = CommonUtils.filterStr(name);

                String href = e.attr("href");
                String id = href.substring(href.indexOf("id=") + 3);

                String visitCount = e.parent().select("div.bottom span.nb").text();
                System.out.println("***********************************");
                System.out.println(id + "--" + visitCount + "--" + name);
                System.out.println("***********************************");

                getAllFromList(id);
            }
        }


    }

    private static Map<String, String> getHeader() {
        Map hearder = new HashMap<String, String>();
        hearder.put("Host", "music.163.com");
        hearder.put("Referer", "https://music.163.com/");
        hearder.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");

        return hearder;
    }

    /**
     * 下载一个歌单歌曲
     *
     * @param play_list_id
     * @throws Exception
     */
    public static void getAllFromList(String play_list_id) throws Exception {
        String url = play_list.replace("##", play_list_id);

        String html = HttpUtils.sendGet(url, getHeader());
        jsoup(html, url);
    }

    /*
    * 使用jsoup解析网页信息
    */
    private static void jsoup(String html, String url) throws Exception {
        Document doc = Jsoup.parse(html);
        Element title = doc.select("div.tit h2.f-ff2").first();
        String title_ = title.text();
        if (title_ == null || title_ == "") {
            System.out.println("未能获取标题：" + url);
            return;
        }
        String dir = save_path + title_ + "/";

        CommonUtils.mkDirectory(dir);
        CommonUtils.mkDirectory(dir + "lrc");
        CommonUtils.mkDirectory(dir + "srt");

        Elements elements = doc.select("ul.f-hide li a");

        for (int i = 0; i < elements.size(); i++) {
            Element e = elements.get(i);
            String href = e.attr("href");
            String name = e.text();
            name = CommonUtils.filterStr(name);


            String id = href.substring(href.indexOf("id=") + 3);

            String baseName = name + "--" + id;



            String outPath = dir + baseName + ".mp3";
            String l_outPath = dir + "lrc/" + baseName + ".lrc";
            String srt_outPath = dir + "srt/" + baseName + ".srt";


//            如果已存在则继续查找
            if (new File(outPath).exists() || isExist(id)) {
                System.out.println("exist..." + outPath);
                continue;
            }
//


            //歌词
            JSONObject jo = HttpUtils.doGet(lrc_url.replace("##", id));

            JSONObject lrc = jo.getJSONObject("lrc");
            if (lrc != null && lrc.getString("lyric") != null && lrc.getString("lyric").length() > 0) {
                String lyric = lrc.getString("lyric");
                CommonUtils.writeString(lyric, l_outPath);
                if (writeSrt(lyric, baseName, srt_outPath)) {
                    //下载音乐
                    System.out.println("downloading..." + baseName);
                    HttpUtils.downLoad(song_url.replace("##", id), outPath);
                } else {
                    System.out.println("歌词格式错误：" + lyric);
                }


                //文件已经存在
                if (new File(outPath).exists())
                    allMp3.put(id, outPath);


            } else {
                System.out.println("no liric...." + baseName);
            }

        }

    }

    public static boolean writeSrt(String lyric, String baseName, String srt_outPath) throws IOException {

        List<String> time_content = new ArrayList<String>();
        for (String s : lyric.split("\n")) {
            time_content.add(s);
        }
        List<String> sb = LrcUtil.parse(time_content);
//        System.out.println(sb + "****" + sb.size() + "*****");
        if (sb != null && sb.size() > 0) {

            CommonUtils.writeAsLine(temp, sb);

            //To test the correct implementation of the SRT parser and writer.
            FormatSRT ttff = new FormatSRT();
            InputStream is = new FileInputStream(temp);
            TimedTextObject tto = ttff.parseFile(baseName + ".srt", is);
            IOClass.writeFileTxt(srt_outPath, tto.toSRT());
            return true;
        } else {
            return false;
        }

    }


    private static boolean isExist(String id) {
        if (allMp3 == null) {
            allMp3 = new HashMap();
            List<File> files = CommonUtils.getMp4FileList(CommonUtils.getPathByKey("audioPath"), new ArrayList<File>(), ".mp3");

            for (File file : files) {
                String name = file.getName();
                name = name.substring(name.indexOf("--") + 2, name.length() - 4);

                allMp3.put(name, file.getAbsolutePath());

            }

        }
        if (allMp3.get(id) != null) {

            return true;
        } else {
            return false;
        }


    }


}
