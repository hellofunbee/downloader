package com.xwf.common.http;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


/**
 * Created by weifengxu on 2018/4/10.
 */
public class DownMylike {
    static String dir = "/Users/weifengxu/Desktop/temp/douyin/xihuan/";
    static int cc = 0;

    public static void main(String args[]) throws InterruptedException, IOException {
        cc = 0;
        JSONObject jo = null;
        String text = null;
        JSONArray ja = JSON.parseArray(FileUtils.readFileToString(new File(new DownMylike().getPath())));
        for (int n = 0; n < ja.size(); n++) {
            try {
                JSONObject obj = ja.getJSONObject(n);
                text = obj.getJSONObject("response").getJSONObject("body").getString("text");

                jo = JSON.parseObject(text);
                downloader(jo);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(text);
            }
        }
        webp2png();
    }

    private static void webp2png() {

        System.out.println(System.getProperty("java.library.path"));

        File f = new File(dir);
        File[] files = f.listFiles();
        if (files != null && files.length > 0) {
            for (File pic : files) {
                String name = pic.getName();
                if (name != null && name.endsWith(".webp")) {
                    System.out.println(name);
                    try {
                        BufferedImage im = ImageIO.read(pic);
                        ImageIO.write(im, "png", new File(pic.getAbsolutePath().replace(".webp", ".png")));

                        pic.delete();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    public static void downloader(JSONObject jo) throws Exception {
        JSONArray jsonArray = null;
        jsonArray = jo.getJSONArray("aweme_list");

        int i = 0;

        if (jsonArray != null && jsonArray.size() > 0) {
            String desc = "";
            JSONObject jobject = null;
            JSONObject author = null;
            String fm = null;
            String cfm = null;
            for (Object obj : jsonArray) {
                cc++;
                jobject = (JSONObject) obj;
                desc = jobject.getString("desc");
                author = jobject.getJSONObject("author");
                desc = author.getString("uid") + "--" + author.getString("short_id") + "--" + desc;
                JSONArray videos = jobject.getJSONObject("video").getJSONObject("play_addr").getJSONArray("url_list");
                JSONArray covers = jobject.getJSONObject("video").getJSONObject("origin_cover").getJSONArray("url_list");

                //目录已经存在，则跳过
                mkDirectory(dir);

                cfm = dir + desc + ".webp";
                fm = dir + desc + ".mp4";

                mkFile(fm);
                mkFile(cfm);

                HttpUtils.downLoad(covers.getString(0), cfm);
                HttpUtils.downLoad(videos.getString(0), fm);

                System.out.println(cc + "--" + desc);
            }
        }
    }


    public static boolean mkDirectory(String path) {
        File file = null;
        try {
            file = new File(path);
            if (!file.exists()) {
                return file.mkdirs();
            } else {
                return false;
            }
        } catch (Exception e) {
        } finally {
            file = null;
        }
        return false;
    }

    /**
     * -1:出现异常
     * 0：应景存在
     * 1：创建成功
     *
     * @param path
     * @return
     */
    public static Integer mkFile(String path) {
        File file = null;
        try {
            file = new File(path);
            if (!file.exists()) {
                file.createNewFile();
                return 1;
            } else {

                return 0;
            }
        } catch (Exception e) {
        } finally {
            file = null;
        }
        return -1;
    }

    private String getPath() {
        return "/Users/weifengxu/Desktop/temp/xihuanjson/Untitled_3.chlsj";
    }

}
