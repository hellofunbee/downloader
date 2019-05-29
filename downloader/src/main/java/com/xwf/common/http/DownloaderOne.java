package com.xwf.common.http;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xwf.common.utils.CommonUtils;

import java.io.IOException;


/**
 * Created by weifengxu on 2018/4/10.
 */
public class DownloaderOne {


    static String dir = "/Users/weifengxu/Desktop/temp/douyin/yinyue/";
    static boolean isAll_cat = false;//4006065500
    static long count_percat = 100;//每个音乐下的视频个数

    static String get_videos = "https://aweme.snssdk.com/aweme/v1/music/aweme/?" +
            "music_id=#&" +
            "cursor=0&" +
            "count=" + count_percat + "&" +
            "type=6&" +
            "retry_type=no_retry&" +
            "iid=30096805396&" +
            "device_id=17107095715&" +
            "ac=wifi&" +
            "channel=oppo&" +
            "aid=1128&" +
            "app_name=aweme&" +
            "version_code=179&" +
            "version_name=1.7.9&" +
            "device_platform=android&" +
            "ssmix=a&" +
            "device_type=OPPO+R9tm&" +
            "device_brand=OPPO&" +
            "language=zh&" +
            "os_api=22&" +
            "os_version=5.1&" +
            "uuid=861404036058990&" +
            "openudid=c9e9be27d1766ea0&" +
            "manifest_version_code=179&" +
            "resolution=1080*1920&" +
            "dpi=480&" +
            "update_version_code=1792&" +
            "_rticket=1523342566679&" +
            "ts=1523342565&as=a115859cd54e3accac1044&" +
            "cp=5be9a95a5cccc8c0e1glcu&" +
            "mas=006a52896a220d0cc230863641c89c15882c2c0c8cc686c6c686cc";

    public static void main(String args[]) throws InterruptedException, IOException {

        String music_id = "6535710798528908040";
        String music_title = "奔跑吧向阳创作的原声";
        getVideos(music_id, music_title, 0);

    }

    /**
     * 获取音乐下的全部video
     *
     * @param music_id
     */
    static void getVideos(String music_id, String title, int num) throws IOException {
        Boolean has_more = true;
        int i = 0;
        while (has_more) {
            JSONObject jo = HttpUtils.doGet(get_videos.replace("#", music_id));
            if (jo == null) {
                System.out.println("数据为空：--" + music_id + "--" + title);
                break;
            }
            has_more = jo.getBoolean("has_more");
            JSONArray jsonArray = jo.getJSONArray("aweme_list");
            if (jsonArray != null && jsonArray.size() > 0) {
                System.out.println(jsonArray.size() + "************");
                String desc = "";
                JSONObject jobject = null;
                JSONObject author = null;
                String fm = null;

                for (Object obj : jsonArray) {
                    i++;
//                    Thread.sleep(10);
                    jobject = (JSONObject) obj;
                    author = ((JSONObject) obj).getJSONObject("author");
                    desc = jobject.getString("desc");
                    desc = i + "--" + author.getString("short_id") + "--" + author.getString("uid") + desc;
                    JSONArray videos = jobject.getJSONObject("video").getJSONObject("download_addr").getJSONArray("url_list");

                    //目录已经存在，则跳过
                    if (!CommonUtils.mkDirectory(dir + title)) {
                        //continue;
                    }
                    fm = dir + title + "/" + desc + ".mp4";
                    int result = CommonUtils.mkFile(fm);
                    if (result == -1) {//失败则换个名字
                        fm = dir + title + "/" + author.getString("short_id") + "--" + author.getString("uid") + "--" + i + ".mp4";
                        result = CommonUtils.mkFile(fm);
                        if (result == 0) {
                            continue;
                        }

                        // desc.replaceAll("[^0-9a-zA-Z\u4e00-\u9fa5.，,。？“”]+","");
                    } else if (result == 0) {
                        continue;
                    }

                    System.out.println("[" + num + "--" + i + "]--" + title + "--" + desc);
                    HttpUtils.downLoad(videos.getString(2).replace("watermark=1", "watermark=0"), fm);
                }
            }
            if (!isAll_cat) {
                System.out.println("===============" + num + "end==============");
                break;
            }

        }
    }

}
