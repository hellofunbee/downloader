package com.xwf.common.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by weifengxu on 2018/8/1.
 * 文本去重
 */
public class DeDubble {

    public static String path = "/Volumes/自媒体/面筋歌词";

    public static void main(String args[]) throws InterruptedException, IOException {

//        String str = FileUtils.readFileToString(new File(path));
//
//        System.out.println(deDubble(str));

        dedubble_file();


    }

    public static String deDubble(String str) {
        if (str == null || str.trim() == "") {
            return null;
        }
        String result = new String();
        List<String> arr = new ArrayList<String>();
        for (int i = 0; i < str.length(); i++) {
            int s = result.indexOf(str.substring(i, i + 1));
            if (s == -1) {
                result += str.substring(i, i + 1);
            }
        }
        return result;

    }


    //文件去重 mp3

    public static void dedubble_file() {
        List<File> files = CommonUtils.getMp4FileList(CommonUtils.getPathByKey("audioPath"), new ArrayList<File>(), ".mp3");
        List<File> more = new ArrayList<File>();
        StringBuffer sb = new StringBuffer();

        for (File f : files) {
            if (sb.indexOf(f.getName()) == -1) {
                sb.append(f.getName() + ",");
            } else {
                more.add(f);
            }

        }

        sb = null;

        for (File f : more) {
            String name = f.getName();
            String srt_path = f.getAbsolutePath().replace(name, "srt/" + name.replace(".mp3", ".srt"));
            String lrc_path = f.getAbsolutePath().replace(name, "lrc/" + name.replace(".mp3", ".lrc"));
            String clips_path = CommonUtils.getPathByKey("a_outPath") + f.getParent() + "/" + name.replace(".mp3", "");

//            System.out.println(clips_path);
            f.delete();
            new File(srt_path).delete();
            new File(lrc_path).delete();
        }

    }

}
