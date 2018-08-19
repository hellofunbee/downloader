package com.xwf.common.utils;

import subtitleFile.Caption;
import subtitleFile.FormatSRT;
import subtitleFile.TimedTextObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by weifengxu on 2018/8/5.
 */
public class VideoCut {
    private static String ffmpegPath = CommonUtils.getPathByKey("ffmpegPath");
    private static String videoPath = "";
    private static String outPath = "";

    private static String srtPath = "";
    private static String src_video_type = ".mp4";
    private static String out_type = ".mp4";//有可能是MP3


    public static void exe(String path, String outPath, String startTime, String lastTime) throws IOException, InterruptedException {
        String[] cmd;
        if (out_type.endsWith(".mp3")) {
            cmd = new String[]{
                    ffmpegPath,
                    "-ss", String.valueOf(startTime),
                    String.valueOf("-t"), String.valueOf(lastTime),
                    "-accurate_seek",
                    "-i", path,
                    "-acodec", "copy",
                    "-threads", "1",
                    outPath

            };
        }else {
            cmd = new String[]{
                    ffmpegPath,
                    "-ss", String.valueOf(startTime),
                    String.valueOf("-t"), String.valueOf(lastTime),
                    "-accurate_seek",
                    "-i", path,
                    "-vcodec", "h264", "-acodec", "aac",
                    "-threads", "1",
                    outPath

            };
        }




//        commandId.add("-avoid_negative_ts");//
//        commandId.add("1");//
        Process process = new ProcessBuilder(Arrays.asList(cmd)).redirectErrorStream(true).start();
        new PrintStream(process.getInputStream(),false).start();
        process.waitFor();
    }


    /**
     * @param videoPath_ 视频存放的路径
     * @param outPath_   视频输出的路径
     * @param srtPath_   字幕存放的路径
     * @throws IOException
     * @throws InterruptedException
     */
    public void videoCut(String out_type_, String src_video_type_, String videoPath_, String outPath_, String srtPath_) throws Exception {
        TimedTextObject ttff = null;
        videoPath = videoPath_;
        outPath = outPath_;
        srtPath = srtPath_;
        src_video_type = src_video_type_;
        out_type = out_type_;


//         ttff = readSrt(srtPath);
//        preCut(ttff);
//
//        if(1==1)return;

//存取所有不相同的字
//        StringBuffer sb = new StringBuffer();
//        List<String> words = new ArrayList<String>();

        File[] files = new File(srtPath).listFiles();
        System.out.println(files);
        if (files != null && files.length > 0) {
            for (File file : files) {
                if (file.getName().endsWith(".srt")) {
                    ttff = readSrt(file.getAbsolutePath());
                    String o = outPath + file.getName().replace(".srt", "") + "/";
                    String v = videoPath + file.getName().replace(".srt", "") + src_video_type;
                    if (!new File(v).exists()) {
                        System.out.println("文件(" + v + "不存在)");
                        continue;
                    }
//                    addKeyFrams(v);
                    preCut(ttff, o, v);


                    /*if (ttff != null && ttff.captions != null) {
                        StringBuffer temp = new StringBuffer();

                        Set<Map.Entry<Integer, Caption>> set = ttff.captions.entrySet();
                        Iterator iterator = set.iterator();


                        while (iterator.hasNext()) {
                            Map.Entry<Integer, Caption> enty = (Map.Entry<Integer, Caption>) iterator.next();
                            Caption cp = enty.getValue();
                            String content = strFormat(cp.content);
                            int start = cp.start.mseconds;
                            int end = cp.end.mseconds;

                            temp.append(content);
                            words.add(content);
                        }

                        sb.append(DeDubble.deDubble(temp.toString()));

                    }*/

                }


            }

//            pastLeep5(words);
        }

//        System.out.println(DeDubble.deDubble(sb.toString()));


    }

    /**
     * 加载字母时间 进行切割
     *
     * @param ttff
     * @throws IOException
     * @throws InterruptedException
     */
    public static void preCut(TimedTextObject ttff, String o, String v) throws IOException, InterruptedException {

        CommonUtils.mkDirectory(o);
        if (ttff == null || ttff.captions == null || ttff.captions.size() == 0) {
            System.out.println(ttff.fileName + "***************:some thing goes wrong!");
        } else {
            Set<Map.Entry<Integer, Caption>> set = ttff.captions.entrySet();
            Iterator iterator = set.iterator();


            while (iterator.hasNext()) {

                Map.Entry<Integer, Caption> enty = (Map.Entry<Integer, Caption>) iterator.next();
                Caption cp = enty.getValue();

                String content = strFormat(cp.content);

                String st = CommonUtils.ms2mmss(cp.start.mseconds);
                String et = CommonUtils.ms2mmss(cp.end.mseconds);

                String clips = o + st + "&" + et + "--" + content + out_type;
                File outFile = new File(clips);
                if (!outFile.exists()) {
                    exe(v, clips, CommonUtils.ms2hhmmss(cp.start.mseconds - 500), CommonUtils.ms2hhmmss(cp.end.mseconds - cp.start.mseconds + 1000));
//                    Thread.sleep(100);
                    System.out.println("切出:" + clips);
                } else {
//                    System.out.println("已存在:" +clips);
                }


            }


        }

    }


    /**
     * 读取字幕文件srt
     *
     * @param srtPath
     * @throws IOException
     */
    public static TimedTextObject readSrt(String srtPath) throws IOException {
        FormatSRT ttff = new FormatSRT();
        File file = new File(srtPath);
        InputStream is = new FileInputStream(file);
        return ttff.parseFile(file.getName(), is);

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

    //去重并按自然顺序排序
    public static List<String> pastLeep5(List<String> list) {
        System.out.println("list = [" + list.size() + "]");
        List<String> listNew = new ArrayList<String>(new TreeSet<String>(list));
        System.out.println("listNew = [" + listNew.size() + "]");

        return listNew;
    }


}
