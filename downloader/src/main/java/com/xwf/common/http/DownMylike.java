package com.xwf.common.http;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;

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
        JSONArray ja =   JSON.parseArray(FileUtils.readFileToString(new File(new DownMylike().getPath())));
        for(int n = 0;n < ja.size();n++){
            JSONObject obj = ja.getJSONObject(n);
            String text = obj.getJSONObject("response").getJSONObject("body").getString("text");
            jo = JSON.parseObject(text);
            downloader(jo);
        }

    }

    public static void downloader(JSONObject jo) throws IOException {
        JSONArray jsonArray = null;
        jsonArray = jo.getJSONArray("aweme_list");

        int i = 0;

        if (jsonArray != null && jsonArray.size() > 0) {
            String desc = "";
            JSONObject jobject = null;
            JSONObject author = null;
            String fm = null;
            for (Object obj : jsonArray) {
                cc++;
                jobject = (JSONObject) obj;
                desc = jobject.getString("desc");
                author = jobject.getJSONObject("author");
                desc = author.getString("uid") + "--" + author.getString("short_id") + "--" + desc;
                JSONArray videos = jobject.getJSONObject("video").getJSONObject("download_addr").getJSONArray("url_list");

                //目录已经存在，则跳过
                mkDirectory(dir);
                fm = dir + desc + ".mp4";
                int result = mkFile(fm);
                if (result == -1) {//失败则换个名字
                    fm = dir + author.getString("short_id") + ".mp4";
                    result = mkFile(fm);
                    if (result == 0) {
                        continue;
                    }

                } else if (result == 0) {
                    continue;
                }

                System.out.println(cc+"--"+desc);
                HttpUtils.downLoad(videos.getString(2).replace("watermark=1", "watermark=0"), fm);
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
        return  "/Users/weifengxu/Desktop/temp/xihuanjson/Untitled.chlsj";
    }

}
