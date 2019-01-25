package com.xwf.common.utils;

import com.xwf.common.utils.ssh.SshUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by weifengxu on 2018/8/5.
 */
public class VideoCut_Stream {
    private static String out_type = ".mp4";//有可能是MP3
    public static int ajust = 0;//ms
    public static String threads = "2";//ms
    private static String ffmpegPath = CommonUtils.getFfmpeg();
    private static String youtube_dl = CommonUtils.getPathByKey("youtube-dl");
    private static String stream_out = CommonUtils.getPathByKey("stream_out");
    static SshUtil sshUtil;
    static String yb_url = "https://www.youtube.com/watch?v=";
    static int cmds = 20;


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

    public static void exe2(String cmd) throws Exception {
        sshUtil = SshUtil.getInstance();
        sshUtil.exe(cmd);
//      sshUtil.close();
    }


    public static void cut(String youtube_id, String s, String e, String key_word, String word) throws Exception {
        String url = "https://www.youtube.com/watch?v=" + youtube_id;
        String out_path = "\'" + stream_out + key_word + "--" + word + "-" + youtube_id + ".mp4\'";

        String cmd = ffmpegPath + " -n -ss " + s + " -to " + e + " -accurate_seek " + "-i "
                + "$(" + youtube_dl + " -f 22 --get-url " + url + ") " + "-vcodec h264 -acodec aac -threads " + threads + " " + out_path;
        exe2(cmd);

    }

    /**
     * 批量执行
     *
     * @param list
     * @throws Exception
     */
    public static void cut(List<Map> list) throws Exception {
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < list.size(); i++) {
            Map m = list.get(i);

            String youtube_id = (String) m.get("youtube_id");
            String key_word = (String) m.get("key_word");
            String word = (String) m.get("word");
            String s = (String) m.get("s");
            String e = (String) m.get("e");
            String url = yb_url + youtube_id;

            String out_path = "\'" + stream_out + key_word + "--" + word + "-" + youtube_id + ".mp4\'";

            String cmd = ffmpegPath + " -n -ss " + s + " -to " + e + " -accurate_seek " + "-i "
                    + "$(" + youtube_dl + " -f 22 --get-url " + url + ") " + "-vcodec h264 -acodec aac -threads " + threads + " " + out_path + ";";
            sb.append(cmd);

            if (i > 0 && i % cmds == 0) {
                exe2(sb.toString());
                sb = new StringBuffer();
            }

        }
        exe2(sb.toString());

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
