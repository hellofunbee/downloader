package com.xwf.common.http;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xwf.common.utils.CommonUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;


/**
 * Created by weifengxu on 2018/4/10.
 */
public class DownMylike_hs {
    static String dir = "/Users/weifengxu/Desktop/temp/douyin/xihuan/";
    static int cc = 0;
    public static void main(String args[]) throws InterruptedException, IOException {
        cc = 0;
        JSONObject jo = null;
        JSONArray ja =   JSON.parseArray(FileUtils.readFileToString(new File(new DownMylike_hs().getPath())));
        for(int n = 0;n < ja.size();n++){
            JSONObject obj = ja.getJSONObject(n);
            String text = obj.getJSONObject("response").getJSONObject("body").getString("text");
            jo = JSON.parseObject(text);
            downloader(jo);
        }

    }

    public static void downloader(JSONObject jo) throws IOException {
        JSONArray jsonArray = null;
        jsonArray = jo.getJSONArray("data");

        int i = 0;

        if (jsonArray != null && jsonArray.size() > 0) {
            String desc = "";
            JSONObject jobject = null;
            JSONObject author = null;
            String fm = null;
            for (Object obj : jsonArray) {
                cc++;
                jobject = (JSONObject) obj;
                jobject = jobject.getJSONObject("data");

                desc = jobject.getString("title");
                author = jobject.getJSONObject("author");
                desc = author.getString("id_str") + "--" + author.getString("short_id") + "--" + desc;
                JSONArray videos = jobject.getJSONObject("video").getJSONArray("download_url");

                //目录已经存在，则跳过
                CommonUtils.mkDirectory(dir);
                fm = dir + desc + ".mp4";
                int result = CommonUtils.mkFile(fm);
                if (result == -1) {//失败则换个名字
                    fm = dir + author.getString("short_id") + ".mp4";
                    result = CommonUtils.mkFile(fm);
                    if (result == 0) {
                        continue;
                    }

                } else if (result == 0) {
                    continue;
                }

                System.out.println(cc+"--"+desc);
                HttpUtils.downLoad(videos.getString(1).replace("watermark=1", "watermark=0"), fm);
            }
        }
    }



    private String getPath() {
        return  "/Users/weifengxu/Desktop/temp/xihuanjson/huoshan.chlsj";
    }

}
