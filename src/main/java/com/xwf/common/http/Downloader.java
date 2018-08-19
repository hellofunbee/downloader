package com.xwf.common.http;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xwf.common.utils.CommonUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


/**
 * Created by weifengxu on 2018/4/10.
 */
public class Downloader {


    static String dir = "/Users/weifengxu/Desktop/temp/douyin/yinyue/";
    static boolean isAll=false;
    static boolean isAll_cat=false;//4006065500
    static long count = 200;//音乐个数
    static long count_percat = 30;//每个音乐下的视频个数
    static long cursor = 0;
    static long user_count_ = 100000;
    static String url_music = "https://aweme.snssdk.com/aweme/v1/music/rank/?" +
            "cursor=#&" +
            "count="+count+"&" +
            "ts=1523337166&" +
            "app_type=normal&" +
            "os_api=22&" +
            "device_type=OPPO%20R9tm&" +
            "device_platform=android&" +
            "ssmix=a&" +
            "iid=30096805396&" +
            "manifest_version_code=179&" +
            "dpi=480&uuid=861404036058990&" +
            "version_code=179&" +
            "app_name=aweme&" +
            "version_name=1.7.9&" +
            "openudid=c9e9be27d1766ea0&" +
            "device_id=17107095715&" +
            "resolution=1080*1920&" +
            "os_version=5.1&" +
            "language=zh&" +
            "device_brand=OPPO&" +
            "ac=wifi&" +
            "update_version_code=1792&" +
            "aid=1128&channel=oppo&" +
            "_rticket=1523337166107&" +
            "as=a1b5a43ccefc0ae71c9548&" +
            "cp=4acfa65ce5c6c974e1boyw&" +
            "mas=0088bbadadfdd820dba6ddc3bf32b76ea21c2cac9cc68ceca6860c";


    static String get_videos ="https://aweme.snssdk.com/aweme/v1/music/aweme/?" +
            "music_id=#&" +
            "cursor=0&" +
            "count="+count_percat+"&" +
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

        JSONObject jo=null ;
        Boolean has_more = true;
        String logid = "";
        long now = 0;
        JSONArray os = null;
        int j =0;

        while (has_more){
            jo = HttpUtils.doGet(url_music.replace("#",cursor+""));
            cursor = cursor+count;
            has_more = jo.getBoolean("has_more");

            JSONObject extra = jo.getJSONObject("extra");
            logid = extra.getString("logid");
            now = extra.getLong("now");
            os = jo.getJSONArray("music_list");
            sort(os);
            JSONObject jobject = null;

            String id = "";
            String title = "";
            long user_count = 0;
            if(os != null && os.size() > 0){
                for (Object obj:os) {
                    jobject = (JSONObject) obj;
                    id = jobject.getString("id");
                    title = jobject.getString("title");
                    user_count = jobject.getLong("user_count");
                   // System.out.println(title+"-------"+id+"------"+user_count);
                    j++;
                    getVideos(id,title,j);
                }

            }

            if(!isAll){
                break;
            }

        }

    }

    /**
     * 获取音乐下的全部video
     * @param music_id
     */
    static void getVideos(String music_id,String title,int num) throws InterruptedException, IOException {
        Boolean has_more = true;
        int i = 0;
        while (has_more ){
            JSONObject  jo = HttpUtils.doGet(get_videos.replace("#",music_id));
            if(jo == null) {
                System.out.println("数据为空：--"+music_id + "--" + title);
                break;
            }
            has_more = jo.getBoolean("has_more");
            JSONArray jsonArray = jo.getJSONArray("aweme_list");
            if(jsonArray != null && jsonArray.size() > 0){
                System.out.println(jsonArray.size()+"************");
                String desc = "";
                JSONObject jobject = null;
                JSONObject author = null;
                String fm = null;

                for (Object obj:jsonArray) {
                    i++;
//                    Thread.sleep(10);
                    jobject = (JSONObject) obj;
                    author =((JSONObject) obj).getJSONObject("author");
                    desc = jobject.getString("desc");
                    desc = i+"--"+ author.getString("short_id")+"--"+author.getString("uid")+desc;
                    JSONArray videos = jobject.getJSONObject("video").getJSONObject("download_addr").getJSONArray("url_list");

                    //目录已经存在，则跳过
                    if(!CommonUtils.mkDirectory(dir+title)){
                       //continue;
                    }
                    fm=dir+title+"/"+desc+".mp4";
                    int result = CommonUtils.mkFile(fm);
                    if(result == -1){//失败则换个名字
                        fm = dir+title+"/"+author.getString("short_id")+"--"+author.getString("uid")+"--"+i+".mp4";
                        result = CommonUtils.mkFile(fm);
                        if(result == 0){
                            continue;
                        }

                       // desc.replaceAll("[^0-9a-zA-Z\u4e00-\u9fa5.，,。？“”]+","");
                    }else if(result == 0){
                        continue;
                    }

                    System.out.println("["+num+"--"+i+"]--"+title+"--"+desc);
                    HttpUtils.downLoad(videos.getString(2).replace("watermark=1","watermark=0"),fm);
                }
            }
            if(!isAll_cat){
                System.out.println("==============="+num+"end==============");
                break;
            }

        }
    }


    /**
     *  对分类按照跟随数量进行排序,去掉使用少于十万的
     */
    static void sort(JSONArray os){
        //转成list
        List<JSONObject> list = new ArrayList<JSONObject>();
        JSONObject jsonObj = null;
        long user_count = 0;

        for (int i = 0; i < os.size(); i++) {
            jsonObj = (JSONObject)os.get(i);
            list.add(jsonObj);
        }

         // 过滤掉小于10w的
        for (int i =0;i<list.size();i++) {
            jsonObj = list.get(i);
            user_count = jsonObj.getLong("user_count");
            if(user_count < user_count_){
                list.remove(jsonObj);
                i--;
            }
        }
        //jsonArr排序
//        Collections.sort(list,new MyComparator());
        os.clear();
        StringBuffer sb = new StringBuffer();
        System.out.println("本次共计"+list.size()+"条分类");
        for (int i = 0; i < list.size(); i++) {
            jsonObj = list.get(i);
            os.add(jsonObj);

            sb.append("序号："+i+"--");
            sb.append("id:");
            sb.append(list.get(i).getString("id")+"--");
            sb.append("title:");
            sb.append(list.get(i).getString("title")+"--");
            sb.append("user_count:");
            sb.append(list.get(i).getString("user_count")+"\n");

        }

        System.out.println(sb.toString());



    }


    public static class MyComparator implements Comparator<JSONObject> {
        public int compare(JSONObject o1, JSONObject o2) {
            int key1 = o1.getInteger("user_count");
            int key2 = o2.getInteger("user_count");
            return (key2-key1);
        }
    }

}
