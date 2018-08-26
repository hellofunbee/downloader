package com.xwf.common.utils;

import it.sauronsoftware.jave.MultimediaInfo;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by weifengxu on 2018/8/10.
 */
public class MainExe {
    static String path = "/Users/weifengxu/Downloads/电视/水浒传1996/srt";
    static String path_video = "/Users/weifengxu/Downloads/电视/水浒传1996/";
    static String path_music = "/Users/weifengxu/Downloads/music/";

    public static void main(String args[]) throws Exception {

//        reName(path, path_video);
//        copyAndZipTopBottom();
//        takeCover();
//        search();
        musicCheck();


    }

    public static void reName(String path, String path2) {

        File[] files_srt = new File(path).listFiles();
        File[] files_video = new File(path2).listFiles();

        for (int i = 0; i < files_srt.length; i++) {
            if (!files_srt[i].getName().endsWith(".srt"))
                continue;

            String name_srt = files_srt[i].getName().replace(".srt", "");
            String name_ = files_video[i].getAbsolutePath().replace(files_video[i].getName(), "");

            File file = new File(name_ + name_srt + ".avi");

//            files_video[i].renameTo(file);
            System.out.println(name_srt + "-->" + file);

            System.out.println(files_srt[i].getName());


        }

    }


    /**
     * copy
     */
    public static void copyAndZipTopBottom() {

        String movie = "/Users/weifengxu/Desktop/clips/三国演义1994/";
        String zip_path = CommonUtils.getPathByKey("zip_path") + UUID.randomUUID() + ".zip";
        List<File> list = new ArrayList<File>();
        File[] video_paths = new File(movie).listFiles();
        for (File file : video_paths) {
            if (!file.isDirectory())
                continue;
            File[] clips = file.listFiles();
            if (clips.length > 0) {
                list.add(clips[1]);
                list.add(clips[clips.length - 1]);
            }

        }
        File[] cs = new File[list.size()];
        list.toArray(cs);

        ZipCompressor.zipFiles(cs, new File(zip_path));
    }


    /**
     * 截屏缩略图
     *
     * @throws Exception
     */
    public static void takeCover() throws Exception {
        List<File> fileList = CommonUtils.getMp4FileList(CommonUtils.getPathByKey("base_path"), new ArrayList<File>(), ".mp4");

        for (File file : fileList) {

            if (file == null || !file.getName().endsWith(".mp4")) {
                continue;
            }

            String i = file.getAbsolutePath();
            String o = file.getAbsolutePath().replace(".mp4", ".jpg");
            if (new File(o).exists()) {
                System.out.println("已存在：" + o);
                continue;
            }
            MultimediaInfo mi = CommonUtils.readVideoTime(file);
            String ss = CommonUtils.ms2hhmmss(mi.getDuration() / 2);

            String[] cmd = {"ffmpeg", "-ss", ss, "-i", i, "-f", "image2", /*"-y",*/ o};
            Process process = new ProcessBuilder(Arrays.asList(cmd)).redirectErrorStream(true).start();
            new PrintStream(process.getInputStream(), false).start();
            process.waitFor();
            System.out.println("cover：" + o);
        }
    }


    /**
     * 查询
     */
    public static void seacher() {

        List<File> fileList = CommonUtils.getMp4FileList(CommonUtils.getPathByKey("base_path"), new ArrayList<File>(), ".mp4");

        for (File file : fileList) {
            String name = file.getName();
            String word = "";
            if (name != null && name.indexOf("--") != -1) {
                word = name.substring(name.indexOf("--") + 2);
                word = word.replace(".mp4", "");
                //匹配算法

                if (word.length() == 7) {
                    System.out.println(word);
                }

            }
        }

    }

    /**
     * 查询
     */
    public static void musicCheck() throws IOException {

        List<File> fileList = CommonUtils.getMp4FileList(CommonUtils.getPathByKey("base_path"), new ArrayList<File>(), ".mp4");
//        List<File> fileList =  Arrays.asList(new File(path_music).listFiles());


        System.out.println("共计【" + fileList.size() + "】srt歌词");
        for (File file : fileList) {

            if (file.exists()) {
//                String name = CommonUtils.v(file.getName());
//                file.renameTo(new File(file.getParent() + "/" + name));

                System.out.println(CommonUtils.backMD5(file.getName()));
//                System.out.println(CommonUtils.v(file.getName()));
            }

//            File[] fs= file.listFiles();
//            if(fs!= null &&fs.length == 0){
//                System.out.println(file);
//                file.delete();
//            }

            /*String strLine = null;

            //删除坏的字幕文件
            InputStreamReader isr = new InputStreamReader(new FileInputStream(
                    file));
            BufferedReader br = new BufferedReader(isr);
            while (null != (strLine = br.readLine())) {
                if (strLine.split("]").length > 2) {
                    System.out.println(file);

                    file.delete();
                    new File((file.getAbsolutePath().replace("/lrc/", "/srt/")).replace(".lrc", ".srt")).delete();
                    break;
                }

            }

            isr.close();*/

           /* if (file.isFile()) {
                String name = file.getName();

                FormatSRT ttff = new FormatSRT();
                InputStream is = new FileInputStream(file);
                TimedTextObject tto = ttff.parseFile(file.getName(), is);

                if (tto.captions.size() ==10) {
                    file.delete();
                    System.out.println(file);
                }

//                System.out.println(name);

            }

            /*if (file.isFile()) {
                String liyric = org.apache.commons.io.FileUtils.readFileToString(file);
                String outPath = (file.getAbsolutePath().replace("/lrc/", "/srt/")).replace(".lrc", ".srt");
                NetEaseCloudMusic.writeSrt(liyric, file.getName().replace(".lrc", ""), outPath);

            }*/


        }

    }



}
