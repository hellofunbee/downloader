package com.xwf.common.utils;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * Created by weifengxu on 2018/8/8.
 */

public class ZipCompressor {


    /**
     * 功能:压缩多个文件成一个zip文件
     *
     * @param srcfile：源文件列表
     * @param zipfile：压缩后的文件
     */
    public static void zipFiles(File[] srcfile, File zipfile) {
        byte[] buf = new byte[1024];
        try {
            //ZipOutputStream类：完成文件或文件夹的压缩
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipfile));
            //去重 并重命名
            File [] deDubble = checkIsRepeat(srcfile);

            for (int i = 0; i < srcfile.length; i++) {
                FileInputStream in = new FileInputStream(srcfile[i]);
                out.putNextEntry(new ZipEntry(deDubble[i].getName()));
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.closeEntry();
                in.close();
            }
            out.close();
            System.out.println("压缩完成.");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 功能:解压缩
     *
     * @param zipfile：需要解压缩的文件
     * @param descDir：解压后的目标目录
     */
    public static void unZipFiles(File zipfile, String descDir) {
        try {
            ZipFile zf = new ZipFile(zipfile);
            for (Enumeration entries = zf.entries(); entries.hasMoreElements(); ) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                String zipEntryName = entry.getName();
                InputStream in = zf.getInputStream(entry);
                OutputStream out = new FileOutputStream(descDir + zipEntryName);
                byte[] buf1 = new byte[1024];
                int len;
                while ((len = in.read(buf1)) > 0) {
                    out.write(buf1, 0, len);
                }
                in.close();
                out.close();
                System.out.println("解压缩完成.");
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 功能:
     *
     * @param args
     */
    public static void main(String[] args) {
        //2个源文件
        File f1 = new File("D:\\workspace\\flexTest\\src\\com\\biao\\test\\abc.txt");
        File f2 = new File("D:\\workspace\\flexTest\\src\\com\\biao\\test\\test.zip");
        File[] srcfile = {f1, f2};
        //压缩后的文件
        File zipfile = new File("D:\\workspace\\flexTest\\src\\com\\biao\\test\\biao.zip");
        //TestZIP.zipFiles(srcfile, zipfile);
        //需要解压缩的文件
        File file = new File("D:\\workspace\\flexTest\\src\\com\\biao\\test\\biao.zip");
        //解压后的目标目录
        String dir = "D:\\workspace\\flexTest\\src\\com\\biao\\test\\";
        ZipCompressor.unZipFiles(file, dir);
    }


    /**
     * 判断整型数组中是否含有重复的元素
     *
     * @param arr
     */
    private static File[] checkIsRepeat(File[] arr) {

        boolean flag = true;   //假设不重复
        File[] after = arr.clone();
        for (int i = 0; i < arr.length - 1; i++) { //循环开始元素
            int n = 2;
            for (int j = i + 1; j < arr.length; j++) { //循环后续所有元素
                //如果相等，则重复
                if (arr[i].getAbsolutePath().equals(arr[j].getAbsolutePath())) {
                    File f = new File(arr[j].getAbsolutePath().replace(".mp4", "(" + n + ").mp4"));
                    after[j] = f;
                    System.out.println("重复:" + arr[i]);
                    n++;
                }
            }
        }
        return after;

    }

}