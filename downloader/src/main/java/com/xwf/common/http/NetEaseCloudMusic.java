package com.xwf.common.http;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.xwf.common.dao.MusicDao;
import com.xwf.common.dao.PlaylistDao;
import com.xwf.common.utils.CommonUtils;
import com.xwf.common.utils.LrcUtil;
import com.xwf.common.utils.ThreadPoolUtils;
import com.xwf.common.utils.WebClientUtil;
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
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by weifengxu on 2018/8/11.
 */
public class NetEaseCloudMusic {

    static String get_play_lists = "https://music.163.com/discover/playlist/?order=hot&cat=%%&limit=35&offset=##";

    static String song_url = "http://music.163.com/song/media/outer/url?id=##.mp3";
    static String lrc_url = "http://music.163.com/api/song/lyric?os=pc&id=##&lv=-1&kv=-1&tv=-1";
    static String save_path = "/Volumes/自媒体/music/";
    static String play_list = "https://music.163.com/playlist?id=##";
    static String temp = save_path + "temp.txt";


     static WebClientUtil wb = new WebClientUtil();
    static ThreadPoolUtils poo = new ThreadPoolUtils(0, 10);


    private static Map getProxy() {
        return null;

//        try {
//            return CommonUtils.getProxy(3);
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.out.println("获取代理ip失败！");
//            return null;
//        }

    }


    public static void main(String arg[]) throws Exception {

        wb.setProxy(getProxy());


        String target = "";
        String path = NetEaseCloudMusic.class.getClassLoader().getResource("test.html").getPath();
        String html = org.apache.commons.io.FileUtils.readFileToString(new File(path));
        Document doc = Jsoup.parse(html);
        Elements elements = doc.select("a.s-fc1");


        Integer index = -1;
        for (Element e : elements) {
            try {
                String word = e.attr("href");
                if ((index = word.indexOf("=")) != -1) {
                    word = word.substring(index + 1);
                    target = get_play_lists.replace("%%", word).replace("##", "0");

                    //家在网页
                    Document page = Jsoup.parse(wb.getHtml(target));

                    Elements p = page.select("div.u-page a.zpgi");
                    int max_page = 0;
                    if (p != null && p.size() > 0) {
                        max_page = Integer.parseInt(p.get(p.size() - 1).text());

                    }

                    if (max_page > 0)
                        getPlay_lists(max_page, word);

                }
            } catch (Exception ee) {
                ee.printStackTrace();
            }

        }


    }

    //获取播放列表列表
    public static void getPlay_lists(int page,  String keyword) throws Exception {

        for (int i = 0; i < page; i++) {
             String url = get_play_lists.replace("##", String.valueOf(i * 35)).replace("%%", keyword);


            Document doc = Jsoup.parse(wb.getHtml(url));

            Elements elements = doc.select("li div.u-cover a.msk");

            for ( Element e : elements) {


                String name = e.attr("title");
                name = CommonUtils.filterStr(name);
                name = CommonUtils.v(name);


                String href = e.attr("href");
                String id = href.substring(href.indexOf("id=") + 3);

                String visitCount = e.parent().select("div.bottom span.nb").text();
                System.out.println("【" + URLDecoder.decode(keyword, "utf-8") + "】【第" + i + "页共" + page + "页】" + id + "--" + visitCount + "--" + name);


                if (CommonUtils.strType(id) != 1) {
                    System.out.println("playlist_id 错误：" + name + "--" + id);
                    return;
                }

                Record re = new Record();
                re.set("playlist_id", id);
                re.set("playlist_name", name);
                re.set("key_word", keyword);


                String url_ = play_list.replace("##", id);

                String html = wb.getHtml(url_);
                if (html != null)
                    jsoup(html, id, url, re);


        }
    }


}


    /*
    * 使用jsoup解析网页信息
    */
    private static void jsoup(String html, String play_list_id, String url, Record playlist) throws Exception {
        Document doc = Jsoup.parse(html);
        Element title = doc.select("div.tit h2.f-ff2").first();
        String title_ = title.text();
        title_ = CommonUtils.v(title_);//=====
        if (title_ == null || title_ == "") {
            System.out.println("未能获取标题：" + url);
            return;
        }

        List<Record> musics = new ArrayList<Record>();

        String dir_new = save_path + title_.replaceAll("--", "") + "--" + play_list_id + "/";


        CommonUtils.mkDirectory(dir_new);
        CommonUtils.mkDirectory(dir_new + "lrc");
        CommonUtils.mkDirectory(dir_new + "srt");


        Elements elements = doc.select("ul.f-hide li a");

        for (int i = 0; i < elements.size(); i++) {
            Element e = elements.get(i);
            String href = e.attr("href");
            String name = e.text();


            name = CommonUtils.filterStr(name);
            name = name.replaceAll("--", " ");//--自定义关键字段
            name = CommonUtils.v(name);


            String id = href.substring(href.indexOf("id=") + 3);

            String baseName = name + "--" + id;

            if (CommonUtils.strType(id) != 1) {
                System.out.println("music id错误：" + id);
                continue;
            }
            //            如果已存在则继续查找
            if (MusicDao.isExit(id)) {
//                System.out.println("exist-lrc：" + baseName);
                continue;
            }


            String outPath = dir_new + baseName + ".mp3";
            String l_outPath = dir_new + "lrc/" + baseName + ".lrc";
            String srt_outPath = dir_new + "srt/" + baseName + ".srt";


            //歌词
            JSONObject jo = HttpUtils.doGet(lrc_url.replace("##", id));

            JSONObject lrc = jo.getJSONObject("lrc");

            Record music = new Record();
            music.set("music_id", id);
            music.set("music_name", baseName + ".srt");
            music.set("playlist_id", play_list_id);


            if (lrc != null && lrc.getString("lyric") != null && lrc.getString("lyric").length() > 0) {
                String lyric = lrc.getString("lyric");
                CommonUtils.writeString(lyric, l_outPath);
                if (writeSrt(lyric, baseName, srt_outPath)) {
                    //下载音乐
                    System.out.println("downloading..." + baseName);
//                    HttpUtils.downLoad(song_url.replace("##", id), outPath);

                    music.set("has_srt", 1);
                    music.set("srt_path", srt_outPath);


                } else {
                    music.set("has_srt", 0);
                    System.out.println("歌词格式错误：" + baseName);
                }


            } else {
                music.set("has_srt", 0);
                System.out.println("no liric...." + baseName);
            }

            musics.add(music);
        }

        if (musics.size() > 0) {
            save(playlist, musics);

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

    private static void save(final Record playlist, final List<Record> musics) {

        Db.tx(new IAtom() {
            public boolean run() throws SQLException {
                PlaylistDao.save(playlist);
                MusicDao.batchSave(musics);
                return true;
            }
        });
    }


}
