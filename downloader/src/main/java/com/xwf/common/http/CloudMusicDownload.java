package com.xwf.common.http;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.xwf.common.dao.MusicDao;
import com.xwf.common.dao.PlaylistDao;
import com.xwf.common.utils.CommonUtils;
import com.xwf.common.utils.LrcUtil;
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
public class CloudMusicDownload implements Runnable {

    static String get_play_lists = "https://music.163.com/discover/playlist/?order=hot&cat=%%&limit=35&offset=##";

    static String song_url = "http://music.163.com/song/media/outer/url?id=##.mp3";
    static String lrc_url = "http://music.163.com/api/song/lyric?os=pc&id=##&lv=-1&kv=-1&tv=-1";
    static String save_path = "/Users/weifengxu/Downloads/music/";
    static String play_list = "https://music.163.com/playlist?id=##";
    static String temp = save_path + "temp.txt";


    WebClientUtil wb = null;
    Element e = null;

    public CloudMusicDownload(WebClientUtil wb, Element e, Map proxy) {
        this.wb = wb;
        this.e = e;

        if (proxy != null)
            wb.setProxy(proxy);

    }

    public void run() {

        String target = "";
        Integer index = -1;

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

        PooledClientFactory.getInstance().returnClient(wb.webClient);


    }

    //获取播放列表列表
    public void getPlay_lists(int page, String keyword) throws Exception {

        for (int i = 0; i < page; i++) {
            String url = get_play_lists.replace("##", String.valueOf(i * 35)).replace("%%", keyword);


            Document doc = Jsoup.parse(wb.getHtml(url));

            Elements elements = doc.select("li div.u-cover a.msk");

            for (Element e : elements) {


                String name = e.attr("title");
                name = CommonUtils.filterStr(name);
                name = CommonUtils.v(name);


                String href = e.attr("href");
                String id = href.substring(href.indexOf("id=") + 3);

                String visitCount = e.parent().select("div.bottom span.nb").text();
                System.out.println("【" + URLDecoder.decode(keyword, "utf-8") + "】【第" + i + "页共" + page + "页】" + id + "--" + visitCount + "--" + name);


                if (CommonUtils.strType(id) != 1) {
                    log("playlist_id 错误：" + name + "--" + id);
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
    private void jsoup(String html, String play_list_id, String url, Record playlist) throws Exception {
        Document doc = Jsoup.parse(html);
        Element title = doc.select("div.tit h2.f-ff2").first();
        String title_ = title.text();
        title_ = CommonUtils.v(title_);//=====
        if (title_ == null || title_ == "") {
            log("未能获取标题：" + url);
            return;
        }

        List<Record> musics = new ArrayList<Record>();

        String dir_new = save_path + title_.replaceAll("--", "") + "--" + play_list_id + "/";


        CommonUtils.mkDirectory(dir_new);
        CommonUtils.mkDirectory(dir_new + "lrc");
        CommonUtils.mkDirectory(dir_new + "srt");


        Elements elements = doc.select("ul.f-hide li a");
        List<String> ids = new ArrayList<String>();

        for (int i = 0; i < elements.size(); i++) {

            Element e = elements.get(i);
            String href = e.attr("href");
            String id = href.substring(href.indexOf("id=") + 3);
            if (CommonUtils.strType(id) != 1) {
                log("music id错误：" + id);
                continue;
            }
            ids.add(id);
        }

        if (ids.size() == 0)
            return;

        List<String> exist_ids = MusicDao.isIn(ids);


        for (int i = 0; i < elements.size(); i++) {
            Element e = elements.get(i);
            String href = e.attr("href");
            String id = href.substring(href.indexOf("id=") + 3);
            if (CommonUtils.strType(id) != 1) {
                log("music id错误：" + id);
                continue;
            }

            if (exist_ids.indexOf(id) != -1) {
                continue;
            }


            String name = e.text();
            name = CommonUtils.filterStr(name);
            name = name.replaceAll("--", " ");//--自定义关键字段
            name = CommonUtils.v(name);


            String baseName = name + "--" + id;


            String outPath = dir_new + baseName + ".mp3";
            String l_outPath = dir_new + "lrc/" + baseName + ".lrc";
            String srt_outPath = dir_new + "srt/" + baseName + ".srt";


            //歌词
            JSONObject lrc = null;
            JSONObject jo = HttpUtils.doGet(lrc_url.replace("##", id));


            if (jo != null)
                lrc = jo.getJSONObject("lrc");

            Record music = new Record();
            music.set("music_id", id);
            music.set("music_name", baseName + ".srt");
            music.set("playlist_id", play_list_id);


            if (lrc != null && lrc.getString("lyric") != null && lrc.getString("lyric").length() > 0) {
                String lyric = lrc.getString("lyric");
                CommonUtils.writeString(lyric, l_outPath);
                if (writeSrt(lyric, baseName, srt_outPath)) {
                    //下载音乐
                    log("downloading..." + baseName);
//                    HttpUtils.downLoad(song_url.replace("##", id), outPath);

                    music.set("has_srt", 1);
                    music.set("srt_path", srt_outPath);


                } else {
                    music.set("has_srt", 0);
                    log("歌词格式错误：" + baseName);
                }


            } else {
                music.set("has_srt", 0);
                log("no liric...." + baseName);
            }

            musics.add(music);
        }

        if (musics.size() > 0) {
            save(playlist, musics);

        }

    }


    public boolean writeSrt(String lyric, String baseName, String srt_outPath) throws IOException {

        List<String> time_content = new ArrayList<String>();
        for (String s : lyric.split("\n")) {
            time_content.add(s);
        }
        List<String> sb = LrcUtil.parse(time_content);
//        log(sb + "****" + sb.size() + "*****");
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

    private synchronized void save(final Record playlist, final List<Record> musics) {


        Db.tx(new IAtom() {
            public boolean run() throws SQLException {
                //再次去重
                List<Record> ids = MusicDao.isexist(musics);
                String path = null;
                for (Record id : ids) {
                    for (int i = 0; i < musics.size(); i++) {
                        Record music = musics.get(i);
                        if (music.get("music_id").equals(id.get("music_id"))) {

                            //新加入的存在文件
                            if ((path = music.get("srt_path")) != null && new File(path).exists()) {
                                //数据库中不存在文件
                                if (id.get("srt_path") == null || !new File(id.getStr("srt_path")).exists()) {
                                    MusicDao.deletByid(id.getStr("music_id"));
                                } else {
                                    //路径不相同则删除文件
                                    if (!path.equals(id.get("srt_path"))) {
                                        new File(path).delete();
                                        new File(path.replace(".srt", ".lrc").replace("/srt/", "/lrc/")).delete();
                                    }

                                    musics.remove(music);
                                }
                            } else
                                musics.remove(music);
                            i--;
                        }
                    }
                }


                PlaylistDao.save(playlist);
                MusicDao.batchSave(musics);
                return true;
            }
        });


    }

    static void log(String info){
        System.out.println(info);
    }


}
