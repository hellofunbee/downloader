package com.xwf.common.utils;

import java.io.File;

/**
 * Created by weifengxu on 2018/8/6.
 */
public class VideoCutMain {

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        video();



//        audio();


    }

    private static void video() throws Exception {
        System.out.println(CommonUtils.getPathByKey("videoPath"));
        System.out.println(CommonUtils.getPathByKey("outPath"));
        System.out.println(CommonUtils.getPathByKey("srtPath"));

        VideoCut videoCut = new VideoCut();
        videoCut.videoCut(".mp4", ".mp4", CommonUtils.getPathByKey("videoPath"), CommonUtils.getPathByKey("outPath"), CommonUtils.getPathByKey("srtPath"));
    }

    private static void audio() {
        String audiopath = CommonUtils.getPathByKey("audioPath");
        String a_outPath = CommonUtils.getPathByKey("a_outPath");

        File[] afiles = new File(audiopath).listFiles();

        if (afiles != null) {
            for (File file : afiles) {
                if (file.isDirectory()) {
                    String out_path = a_outPath + file.getName() + "/";

                    String srt_path = file.getPath() + "/srt/";

                    VideoCut vc = new VideoCut();
                    try {
                        vc.videoCut(".mp3", ".mp3", file.getAbsolutePath() + "/", out_path, srt_path);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            }

        }
    }


}
