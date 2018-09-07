package com.xwf.common.utils;

import com.xwf.common.utils.ssh.SshUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * Created by weifengxu on 2018/8/5.
 */
public class VideoCut_Stream {
    private static String out_type = ".mp4";//有可能是MP3
    public static int ajust = 0;//ms
    public static String threads = "2";//ms
    private static String ffmpegPath = CommonUtils.getPathByKey("ffmpegPath");
    private static String youtube_dl = CommonUtils.getPathByKey("youtube-dl");
    private static String stream_out = CommonUtils.getPathByKey("stream_out");
    static SshUtil sshUtil;


    public static void exe(String path, String outPath, String startTime, String lastTime) throws IOException, InterruptedException {
        String[] cmd;

        String stream = execCMD(youtube_dl + " -f 22 --get-url " + path);

        System.out.println(stream);

        if (out_type.endsWith(".mp3")) {
            cmd = new String[]{
                    ffmpegPath,
                    "-ss", String.valueOf(startTime),
                    String.valueOf("-t"), String.valueOf(lastTime),
                    "-accurate_seek",
                    "-i",
                    stream,
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
                    "-i",
                    stream,
                    "-vcodec", "h264", "-acodec", "aac",
                    "-threads", threads,
                    outPath

            };
        }

        Process process = new ProcessBuilder(Arrays.asList(cmd)).redirectErrorStream(true).start();
        new PrintStream(process.getInputStream(), true).start();
        process.waitFor();
    }

    public static void exe2(String path, String outPath, String startTime, String lastTime) throws Exception {

        System.out.println(outPath);
        sshUtil = SshUtil.getInstance();

        String cmd = ffmpegPath + " -ss " + startTime + " -to " + lastTime + " -accurate_seek " + "-i "
                + "$(" + youtube_dl + " -f 22 --get-url " + path + ") " + "-vcodec h264 -acodec aac -threads " + threads + " " + outPath;
        System.out.println(cmd);

         String dir = new File(outPath).getParent();
        sshUtil.exe(cmd);

//      sshUtil.close();

    }


    public static void cut(String youtube_id, String s, String e, String key_word, String word) throws Exception {
        String url = "https://www.youtube.com/watch?v=" + youtube_id;


        String out_path = "\'" + stream_out + key_word + "--" + word + "-" + youtube_id + ".mp4\'";
        exe2(url, out_path, s, e);

    }


    //执行cmd命令，获取返回结果
    public static String execCMD(String command) {
        StringBuilder sb = new StringBuilder();
        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (Exception e) {
            return e.toString();
        }
        return sb.toString();
    }


}
