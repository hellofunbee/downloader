package com.xwf.common.http;

import com.alibaba.fastjson.JSONObject;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.ProxyConfig;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.xwf.common.utils.CommonUtils;
import com.xwf.common.utils.LrcUtil;
import org.apache.commons.logging.LogFactory;
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
import java.util.logging.Level;


/**
 * Created by weifengxu on 2018/8/11.
 */
public class NetEaseCloudMusic {

    static String get_play_lists = "https://music.163.com/discover/playlist/?order=hot&cat=%%&limit=35&offset=##";

    static String song_url = "http://music.163.com/song/media/outer/url?id=##.mp3";
    static String lrc_url = "http://music.163.com/api/song/lyric?os=pc&id=##&lv=-1&kv=-1&tv=-1";
    static String save_path = "/Users/weifengxu/Downloads/music/";
    static String play_list = "https://music.163.com/playlist?id=##";
    static String temp = save_path + "temp.txt";

    static Map allMp3 = null;
    static Map allLrc = null;
    static Map alldir = null;

    static int tryCount = 4;


    public static void main(String arg[]) throws Exception {
        HttpUtils.setProxy(CommonUtils.getProxy(3));
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
                    target = get_play_lists.replace("%%", word);


                    //构造一个webClient 模拟Chrome 浏览器
                    WebClient webClient = new WebClient(BrowserVersion.CHROME);
                    //屏蔽日志信息
                    LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log",
                            "org.apache.commons.logging.impl.NoOpLog");

                    java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit")
                            .setLevel(Level.OFF);

                    java.util.logging.Logger.getLogger("org.apache.commons.httpclient")
                            .setLevel(Level.OFF);
                    //支持JavaScript
                    webClient.getOptions().setJavaScriptEnabled(false);
                    webClient.getOptions().setCssEnabled(false);
                    webClient.getOptions().setActiveXNative(false);
                    webClient.getOptions().setCssEnabled(false);
                    webClient.getOptions().setThrowExceptionOnScriptError(false);
                    webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
                    webClient.getOptions().setTimeout(10000);
                    HtmlPage rootPage = webClient.getPage(target.replace("##", "0"));

                    if (HttpUtils.proxy_ip != null) {
                        String ip = (String) HttpUtils.proxy_ip.get("ip");
                        Integer port = Integer.parseInt((String) HttpUtils.proxy_ip.get("port"));
                        webClient.getOptions().setProxyConfig(new ProxyConfig(ip, port));

                    }
                    //设置一个运行JavaScript的时间
//                webClient.waitForBackgroundJavaScript(5000);
                    webClient.getOptions().setJavaScriptEnabled(true);
                    Document page = Jsoup.parse(rootPage.asXml());


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

    public static void getPlay_lists(int page, String keyword) throws Exception {

        for (int i = 0; i < page; i++) {
            String url = get_play_lists.replace("##", String.valueOf(i * 35)).replace("%%", keyword);

            String html = HttpUtils.sendGetProxy(url, getHeader());
            Document doc = Jsoup.parse(html);

            Elements elements = doc.select("li div.u-cover a.msk");
            for (Element e : elements) {
                String name = e.attr("title");
                name = CommonUtils.filterStr(name);

                String href = e.attr("href");
                String id = href.substring(href.indexOf("id=") + 3);

                String visitCount = e.parent().select("div.bottom span.nb").text();
                System.out.println("***********************************");
                System.out.println("【第" + i + "页共" + page + "页】" + id + "--" + visitCount + "--" + name);
                System.out.println("***********************************");


                if (isExist(id, "dir")) {
                    System.out.println("exist-播单..." + name + "--" + id);
                    continue;
                }

                try {
                    getAllFromList(id);
                } catch (Exception ecp) {
                    ecp.printStackTrace();
                }


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

        if (tryCount < 0) {
            return;
        }
        tryCount--;
        String url = play_list.replace("##", play_list_id);

        String html = null;

        try {
            html = HttpUtils.sendGetProxy(url, getHeader());
        } catch (Exception e) {

            HttpUtils.setProxy(CommonUtils.getProxy(3));

            getAllFromList(play_list_id);

        }


        if (html != null)
            jsoup(html, play_list_id, url);

        tryCount = 4;
    }

    /*
    * 使用jsoup解析网页信息
    */
    private static void jsoup(String html, String play_list_id, String url) throws Exception {
        Document doc = Jsoup.parse(html);
        Element title = doc.select("div.tit h2.f-ff2").first();
        String title_ = title.text();
        if (title_ == null || title_ == "") {
            System.out.println("未能获取标题：" + url);
            return;
        }

        String dir = save_path + title_ + "/";
        String dir_new = save_path + title_.replace("--", "") + "--" + play_list_id + "/";


        if (new File(dir).exists()) {

            new File(dir).renameTo(new File(dir_new));
            alldir.put(play_list_id, dir);//添加
            System.out.println("exist-播单..." + dir);
            return;
        }

        CommonUtils.mkDirectory(dir_new);
        alldir.put(play_list_id, dir_new);//添加
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
            if (new File(outPath).exists() || isExist(id, ".mp3")) {
                System.out.println("exist-mp3..." + outPath);
                continue;
            }

            //            如果已存在则继续查找
            if (new File(l_outPath).exists() || isExist(id, ".lrc")) {
                System.out.println("exist-lrc..." + outPath);
                continue;
            }


            //歌词
            JSONObject jo = HttpUtils.doGet(lrc_url.replace("##", id));

            JSONObject lrc = jo.getJSONObject("lrc");
            if (lrc != null && lrc.getString("lyric") != null && lrc.getString("lyric").length() > 0) {
                String lyric = lrc.getString("lyric");
                CommonUtils.writeString(lyric, l_outPath);
                if (writeSrt(lyric, baseName, srt_outPath)) {
                    //下载音乐
                    System.out.println("downloading..." + baseName);
//                    HttpUtils.downLoad(song_url.replace("##", id), outPath);
                } else {
                    System.out.println("歌词格式错误：" + lyric);
                }


                //文件已经存在
                if (new File(outPath).exists())
                    allMp3.put(id, outPath);
                //文件已经存在
                if (new File(l_outPath).exists())
                    allLrc.put(id, outPath);

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


    private static boolean isExist(String id, String match) {

        if (match.equals(".lrc")) {
            if (allLrc == null) {
                allLrc = new HashMap();
                List<File> files = CommonUtils.getMp4FileList(CommonUtils.getPathByKey("audioPath"), new ArrayList<File>(), match);
                for (File file : files) {
                    String name = file.getName();
                    name = name.substring(name.indexOf("--") + 2, name.length() - 4);
                    allLrc.put(name, file.getAbsolutePath());
                }
            }
            if (allLrc.get(id) != null) {

                return true;
            } else {
                return false;
            }
        } else if (match.equals(".mp3")) {
            if (allMp3 == null) {
                allMp3 = new HashMap();
                List<File> files = CommonUtils.getMp4FileList(CommonUtils.getPathByKey("audioPath"), new ArrayList<File>(), match);
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
        } else {
            if (alldir == null) {
                alldir = new HashMap();
                File[] files = new File(CommonUtils.getPathByKey("audioPath")).listFiles();
                for (File file : files) {
                    if (file.isDirectory() && file.getName().indexOf("--") != -1) {
                        String name = file.getName();
                        name = name.substring(name.indexOf("--") + 2, name.length());
                        alldir.put(name, file.getAbsolutePath());
                    }

                }
            }
            if (alldir.get(id) != null) {

                return true;
            } else {
                return false;
            }
        }


    }


}
