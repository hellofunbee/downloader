package com.xwf.common.http;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xwf.common.utils.CommonUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;


/**
 * Created by weifengxu on 2018/4/10.
 */
public class DownMylike_ks {
    static String dir = "/Volumes/自媒体/temp/douyin/xihuan/ks/";
    static int cc = 0;
    public static void main(String args[]) throws InterruptedException, IOException {
        cc = 0;
        final Base64 base64 = new Base64();
        JSONObject jo = null;
        JSONArray ja =   JSON.parseArray(FileUtils.readFileToString(new File(new DownMylike_ks().getPath())));
        for(int n = 0;n < ja.size();n++){
            JSONObject obj = ja.getJSONObject(n);
            String text = obj.getJSONObject("response").getJSONObject("body").getString("text");
//            text=  CommonUtils.decode(text, "utf-8");
            jo = JSON.parseObject(text);
            downloader(jo);
        }

    }

    public static void downloader(JSONObject jo) throws IOException {
        JSONArray jsonArray = null;
        jsonArray = jo.getJSONArray("feeds");

        int i = 0;

        if (jsonArray != null && jsonArray.size() > 0) {
            String desc = "";
            JSONObject jobject = null;
            String fm = null;
            String kwaiId = null;
            String user_id = null;
            for (Object obj : jsonArray) {
                cc++;
                jobject = (JSONObject) obj;
                desc = jobject.getString("caption");
                kwaiId = jobject.getString("kwaiId");
                user_id  = jobject.getString("user_id");

                desc =user_id + "--" + (kwaiId == null ? user_id : kwaiId) + "--" + desc;
                JSONArray videos = jobject.getJSONArray("main_mv_urls");

                //目录已经存在，则跳过
                CommonUtils.mkDirectory(dir);
                fm = dir + desc + ".mp4";
                int result = CommonUtils.mkFile(fm);
                if (result == -1) {//失败则换个名字
                    fm = dir + jobject.getString("user_id") + ".mp4";
                    result = CommonUtils.mkFile(fm);
                    if (result == 0) {
                        continue;
                    }

                } else if (result == 0) {
                    continue;
                }

                System.out.println(cc+"--"+desc);
                HttpUtils.downLoad(videos.getJSONObject(0).getString("url"), fm);
            }
        }
    }




    private String getPath() {
        return  "/Volumes/自媒体/temp/xihuanjson/ks-Untitled.chlsj";
    }

}
