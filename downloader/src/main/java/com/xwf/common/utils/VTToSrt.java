package com.xwf.common.utils;

import org.junit.Test;
import subtitleFile.FormatSRT;
import subtitleFile.IOClass;
import subtitleFile.TimedTextObject;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by weifengxu on 2018/9/6.
 */
public class VTToSrt {
        static String path = "/Volumes/自媒体/srt/";
    ;
    static String ffmpeg = CommonUtils.getPathByKey("ffmpeg");
    static String temp = CommonUtils.getPathByKey("srt_path") + "temp.srt";

    public static void main(String[] args) throws Exception {

        ffmpeg = "/Users/weifengxu/Desktop/tool/jave_ffmpeg/ffmpeg";

        List<File> files = CommonUtils.getMp4FileList(path, new ArrayList<File>(), "vtt");

        vtt2Srt(files);
    }

    private static void vtt2Srt2(List<File> files) throws Exception {
        int i = 1;
        for (File file : files) {

            if (new File(file.getAbsolutePath().replace(".vtt", ".srt")).exists())
                continue;
            String[] cmd = new String[]{
                    ffmpeg,
                    "-i", file.getAbsolutePath(),
                    file.getAbsolutePath().replace(".vtt", ".srt")

            };
            System.out.println(i++ + "*****" + file.getName());
            Process process = new ProcessBuilder(Arrays.asList(cmd)).redirectErrorStream(true).start();
            new PrintStream(process.getInputStream(), false).start();
            process.waitFor();
        }


    }

    private static void exe(String command) {

        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedInputStream bis = new BufferedInputStream(
                    process.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(bis));
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }

            process.waitFor();
            if (process.exitValue() != 0) {
                System.out.println("error!");
            }

            bis.close();
            br.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    private static void vtt2Srt(List<File> files) {
        String line = null;
        int j = 0;
        for (File file : files) {
            // file = new File(file.getParent() + "/Obama Foundation Summit _ Morning Session - Business & Society-N9n7_xpxdpM.en.vtt");

            System.out.println(j++ + "*****" + file.getName());
            String o_srt = file.getAbsolutePath().replace(".vtt", ".srt");
            boolean start = false;
            try {
                BufferedReader br = new BufferedReader(new FileReader(file.getAbsolutePath()));

                List<String> result = new ArrayList<String>();
                int i = 1;
                while ((line = br.readLine()) != null) {
                    if (!start && !line.startsWith("0"))
                        continue;


                    if (line.startsWith("0") && line.indexOf("-->") != -1) {

                        line = line.substring(0, 29);

                        if (!start)
                            result.add(String.valueOf(i) + "\n" + line);
                        else
                            result.add("\n" + String.valueOf(i) + "\n" + line);
                        i++;

                        start = true;
                    } else {

                        line = line.replaceAll("<(.*?)>", " ").replaceAll("\n", " ").replaceAll(" +", " ");
                        if (line.length() <= 0 || line.equals(" ")) {
                            continue;
                        }
                        result.add(line);

                    }
                }

                CommonUtils.writeAsLine(temp, result);

                //To test the correct implementation of the SRT parser and writer.
                FormatSRT ttff = new FormatSRT();
                InputStream is = new FileInputStream(temp);
                TimedTextObject tto = ttff.parseFile(file.getName().replace(".vtt", ".srt"), is);
                IOClass.writeFileTxt(o_srt, tto.toSRT());

//                file.delete();

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(line);
            }


        }
    }




    @Test
    public void dele() {

//        path = "/Volumes/自媒体/srt";
        List<File> jpgs = CommonUtils.getMp4FileList(path, new ArrayList<File>(), "jpg");
        List<File> srts = CommonUtils.getMp4FileList(path, new ArrayList<File>(), "vtt");
        String srt_name = null;

        boolean isIn;
        for (File srt : srts) {
            isIn = false;
            srt_name = srt.getName().replace(".vtt", "");
            if (srt_name.indexOf(".") == -1)
                continue;

            srt_name = srt_name.substring(0, srt_name.lastIndexOf("."));

            for (File jpg : jpgs) {
                String jpg_name = jpg.getName().replace(".jpg", "");

                if (srt_name.equals(jpg_name)) {
                    isIn = true;
                    break;
                }
            }

            if (!isIn) {
                srt.delete();

                System.out.println(srt_name);
            }

        }

    }


}
