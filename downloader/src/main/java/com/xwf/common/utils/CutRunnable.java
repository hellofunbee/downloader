package com.xwf.common.utils;

import subtitleFile.Caption;

import java.io.File;
import java.util.Map;

public class CutRunnable implements Runnable {
    public String o;
    public String v;
    public Map.Entry<Integer, Caption> enty;

    public int ajust;
    public String out_type;

    public CutRunnable(String o, String v, Map.Entry<Integer, Caption> enty, int ajust, String out_type) {
        this.o = o;
        this.v = v;
        this.enty = enty;
        this.ajust = ajust;
        this.out_type = out_type;
    }

    public void run() {
        try {
            Thread.sleep(new Double(Math.random()* 50).longValue());
            Caption cp = enty.getValue();

            String content = CommonUtils.v(strFormat(cp.content));
            content = content.replaceAll("--", " ");//-- 为我们命名的关键字段

            String st = CommonUtils.ms2mmss(cp.start.mseconds);
            String et = CommonUtils.ms2mmss(cp.end.mseconds);


            int s = cp.start.mseconds + ajust;
            int e = cp.end.mseconds + ajust;

            String clips = o + st + "&" + et + "--" + content + out_type;
            File outFile = new File(clips);
            if (!outFile.exists()) {
                VideoCut.exe(v, clips, s, e);
                System.out.println("切出:" + clips);
            } else {
                System.out.println("已存在:" + clips);
            }
        } catch (Exception r) {
            r.printStackTrace();
        }
    }


    /**
     * 去掉尖括号内内容
     *
     * @param str
     * @return
     */
    public static String strFormat(String str) {
        String pattern = "<([^<>]*)>";//括号内
        str = str.replaceAll(pattern, "");
        return str;

    }
}
