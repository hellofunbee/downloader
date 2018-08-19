package com.xwf.common.video;


import com.spreada.utils.chinese.ZHConverter;
import com.xwf.common.utils.CommonUtils;
import subtitleFile.FormatSRT;
import subtitleFile.IOClass;
import subtitleFile.TimedTextObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by weifengxu on 2018/4/12.
 */
public class FileUtils {

    static String dir = "/Users/weifengxu/Desktop/temp/douyin/yinyue/";
    static String dir_one = "/Users/weifengxu/Desktop/temp/douyin/xihuan/";

    public static void main(String args[]) throws Exception {
//        getFolders(dir_one);
        getFolderOne(dir_one);
    }

    //根据目录结构来的
    static void getFolders(String path) throws Exception {
        List<VideoFileBean> videos = new ArrayList<VideoFileBean>();
        File file = new File(path);
        File[] folds = file.listFiles();
        int j = 0;
        for (File fold : folds) {
            //二级目录
            if (fold.isDirectory()) {
                VideoFileBean vb = new VideoFileBean();
                vb.fold = fold;
                File[] files = fold.listFiles();
                //文件
                for (File f : files) {
                    if (f.getName().startsWith(".")) continue;
                    if (f.getName().equals("title.txt")) continue;
                    if (f.getName().equals("title.srt")) continue;
                    if (f.isFile()) {
                        j++;
                        vb.files.add(f);
                    } else {
                        delFolder(f.getAbsolutePath());
                        vb.folds.add(f);
                    }
                }
                videos.add(vb);
            }
        }
        System.out.println("共计：" + j + "个视频");
        divid(videos);
    }

    //根据目录结构来的 只獲取一個
    static void getFolderOne(String path) throws Exception {
        List<VideoFileBean> videos = new ArrayList<VideoFileBean>();
        File fold = new File(path);
        int j = 0;
        if (fold.isDirectory()) {
            VideoFileBean vb = new VideoFileBean();
            vb.fold = fold;
            File[] files = fold.listFiles();
            //文件
            for (File f : files) {
                if (f.getName().startsWith(".")) continue;
                if (f.getName().equals("title.txt")) continue;
                if (f.getName().equals("title.srt")) continue;
                if (f.isFile()) {
                    j++;
                    vb.files.add(f);
                } else {
                    delFolder(f.getAbsolutePath());
                    vb.folds.add(f);
                }
            }
            videos.add(vb);
        }
        System.out.println("共计：" + j + "个视频");
        divid(videos);
    }

    static void divid(List<VideoFileBean> videos) throws Exception {
        List<String> paths = null;

        for (int i = 0; i < videos.size(); i++) {
            VideoFileBean vb = videos.get(i);
            Collections.sort(vb.files, new CompratorByLastModified());
            List<String> subs = getTimeName(vb.files);
            String title = vb.fold + "/title.txt";
            CommonUtils.writeAsLine(title, subs);


            //To test the correct implementation of the SRT parser and writer.
            FormatSRT ttff = new FormatSRT();
            File file = new File(title);
            InputStream is = new FileInputStream(file);
            TimedTextObject tto = ttff.parseFile(file.getName(), is);
            IOClass.writeFileTxt(vb.fold + "/title.srt", tto.toSRT());

            System.out.println(vb.fold.getName());
//            break;

        }

    }

    /**
     * 进行文件排序时间
     *
     * @author 谈情
     */
    private static class CompratorByLastModified implements Comparator<File> {
        public int compare(File f1, File f2) {
            long diff = f1.lastModified() - f2.lastModified();
            if (diff > 0)
                return 1;
            else if (diff == 0)
                return 0;
            else
                return -1;
        }

        public boolean equals(Object obj) {
            return true;
        }
    }

    //分组的名称
    private static List<String> getTimeName(List<File> fs) {
        StringBuffer sb = new StringBuffer();
        List<String> srt = new ArrayList<String>();
        long now = 0;
        int i = 1;
        for (File f : fs) {
            String time = "";
            srt.add(i + "");

            time += formatTime(now) + " --> ";
            now += CommonUtils.readVideoTime(f).getDuration();
            time += formatTime(now);

            srt.add(time);
            srt.add(getName(f));
            srt.add("\n");
            i++;
        }
        return srt;
    }

    /**
     * 获取文件名称
     *
     * @param file
     * @return
     */
    private static String getName(File file) {
        ZHConverter converter = ZHConverter.getInstance(ZHConverter.TRADITIONAL);
        String name = file.getName();
//        name = converter.convert(name);

        String id = getSubUtilSimple(name, "--(.*?)--");
        try {
            int index = name.lastIndexOf("--");
            name = name.substring(index + 2);
            name = name.replace(".mp4", "");
            if (name != null && !name.trim().equals("")) {
                return "id:" + id + "\n" + NN(name);
            } else {
                return "id:" + id + "\n" + "うぇえぉおかがきぎ";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "id:" + id + "\n" + "******";

    }

    static int nameLn = 10;

    static String NN(String name) {
        String name_after = null;

        List<String> strs = new ArrayList<String>();
        while (name.length() > nameLn) {
            strs.add(name.substring(0, nameLn));
            name = name.substring(nameLn, name.length());
        }
        strs.add(name);

        for (int i = 0; i < strs.size(); i++) {
            if (i == 0) {
                name_after = strs.get(0);
            } else {
                name_after = name_after + "\n" + strs.get(i);
            }
        }

//         if(name.length() > 30){
//             name = name.substring(0,30)+"\n"+name.substring(30,name.length());
//         }

        return name_after;
    }


    static String formatTime(long time) {
        long min = time / 60000;
        long sec = (time % 60000) / 1000;
        long mmsec = (time % 1000);
        String mn = "";
        if (mmsec < 10) {
            mn = "00" + mmsec;
        } else if (mmsec < 100) {
            mn = "0" + mmsec;
        } else mn = mmsec + "";
        return "00:" + (min < 10 ? ("0" + min) : (min + "")) + ":" + (sec < 10 ? "0" + sec : (sec + "")) + "," + mn;
    }



    /**
     * 删除文件夹
     *
     * @param folderPath 文件夹完整绝对路径 ,"Z:/xuyun/save"
     */
    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath); //删除完里面所有内容
            String filePath = folderPath;
            filePath = filePath.toString();
            java.io.File myFilePath = new java.io.File(filePath);
            myFilePath.delete(); //删除空文件夹
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除指定文件夹下所有文件
     *
     * @param path 文件夹完整绝对路径 ,"Z:/xuyun/save"
     */
    public static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);//先删除文件夹里面的文件
                delFolder(path + "/" + tempList[i]);//再删除空文件夹
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 返回单个字符串，若匹配到多个的话就返回第一个，方法与getSubUtil一样
     *
     * @param soap
     * @param rgex
     * @return
     */
    public static String getSubUtilSimple(String soap, String rgex) {
        Pattern pattern = Pattern.compile(rgex);// 匹配的模式
        Matcher m = pattern.matcher(soap);
        while (m.find()) {
            return m.group(1);
        }
        return "";

    }
}
