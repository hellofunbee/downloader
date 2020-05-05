package com.xwf.common.utils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by weifengxu on 2018/8/15.
 */
public class Youtube_dl {
    static String path = "/Volumes/自媒体/tool/youtube-dl";
    static String o_path = "/Volumes/自媒体/bz/test";

    public static void main(String arg[]) throws Exception {
        if(1==1)return;
        String urls = FileUtils.readFileToString(new File("/Volumes/自媒体/bilibili.sh"));
        List<String> vs = Arrays.asList(urls.split("\n"));

        for (String s : vs) {

            if (s != null && s.startsWith("http")) {
                System.out.println(s);
                batch_download(s);
//                batch_download("https://www.bilibili.com/video/av29422902?from=search&seid=17865400827662811015");
            }


        }


    }

    public static void batch_download(String url) throws IOException, InterruptedException {


        String[] cmd = new String[]{path, url};
        Process process = new ProcessBuilder(Arrays.asList(cmd)).redirectErrorStream(true).start();
        new PrintStream(process.getInputStream(), true).start();
        process.waitFor();
    }




}
