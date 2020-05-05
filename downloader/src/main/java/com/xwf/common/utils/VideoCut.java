package com.xwf.common.utils;

import subtitleFile.Caption;
import subtitleFile.TimedTextObject;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by weifengxu on 2018/8/5.
 */
public class VideoCut {
    private static String ffmpegPath = CommonUtils.getFfmpeg();
    private static String videoPath = "";
    private static String outPath = "";

    private static String srtPath = "";
    private static String out_type = ".mp4";//有可能是MP3

    public static int ajust = 500;//ms
    public static String threads = "20";//ms
    public static ExecutorService cachedThreadPool = Executors.newFixedThreadPool(2);

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
                    "-threads", threads,
                    outPath

            };
        } else {
            cmd = new String[]{
                    ffmpegPath,
                    "-ss", String.valueOf(startTime),
                    String.valueOf("-t"), String.valueOf(lastTime),
                    "-accurate_seek",
                    "-i", path,
                    "-vcodec", "h264", "-acodec", "aac",
                    "-threads", threads,
                    outPath

            };
        }


//        commandId.add("-avoid_negative_ts");//
//        commandId.add("1");//
        Process process = new ProcessBuilder(Arrays.asList(cmd)).redirectErrorStream(true).start();
        new PrintStream(process.getInputStream(), false).start();
        process.waitFor();
    }


    /**
     * @param videoPath_ 视频存放的路径
     * @param outPath_   视频输出的路径
     * @param srtPath_   字幕存放的路径
     * @throws IOException
     * @throws InterruptedException
     */
    public void videoCut(String out_type_, String videoPath_, String outPath_, String srtPath_) throws Exception {
        TimedTextObject ttff = null;
        videoPath = videoPath_;
        outPath = outPath_;
        srtPath = srtPath_;
        out_type = out_type_;


//         ttff = readSrt(srtPath);
//        preCut(ttff);
//
//        if(1==1)return;

//存取所有不相同的字
//        StringBuffer sb = new StringBuffer();
//        List<String> words = new ArrayList<String>();

        File f = new File(srtPath);
        System.out.println(f.isDirectory());
        File[] files = new File(srtPath).listFiles();
        System.out.println(files);
        if (files != null && files.length > 0) {
            for (File file : files) {
                if (CommonUtils.isSrt(file)) {
                    ttff = CommonUtils.readSrt(file.getAbsolutePath());
                    String name = file.getName().substring(0, file.getName().lastIndexOf("."));


                    String o = outPath + name + "/";
                    String v = CommonUtils.getVideo(videoPath + name);
                    System.out.println("out path" + o);
                    if (v == null) {
                        System.out.println("文件(" + videoPath + name + "不存在)");
                        continue;
                    }
                    preCut(ttff, o, v);
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

                cachedThreadPool.execute(new CutRunnable(o, v, enty, ajust, out_type));
            }

        }

    }

    //去重并按自然顺序排序
    public static List<String> pastLeep5(List<String> list) {
        System.out.println("list = [" + list.size() + "]");
        List<String> listNew = new ArrayList<String>(new TreeSet<String>(list));
        System.out.println("listNew = [" + listNew.size() + "]");

        return listNew;
    }

    public static void main(String[] args) {
        File f = new File("/Volumes/自媒体/clips/");
        File[] files = f.listFiles();
        for (File file:files){
            System.out.println(file.getAbsoluteFile());
        }
    }

}
