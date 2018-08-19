package com.xwf.common.utils;

import com.xwf.common.video.MFFMPEGLocator;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.MultimediaInfo;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by weifengxu on 2018/8/6.
 */
public class CommonUtils {
    public static Properties pathProp = null;

    /**
     * 毫秒转换成分秒
     *
     * @param mseconds
     */
    public static String ms2mmss(long mseconds) {

        long minute = mseconds / (60000);
        long second = (mseconds % 60000) / 1000;

        String sm, ss;

        if (minute < 10) {
            sm = "0" + String.valueOf(minute);
        } else {
            sm = String.valueOf(minute);
        }
        if (second < 10) {
            ss = "0" + String.valueOf(second);
        } else {
            ss = String.valueOf(second);
        }
        return sm + "*" + ss;

    }

    public static String ms2hhmmss(long mseconds) {

        long hour = mseconds / (60000 * 60);
        long minute = mseconds / 60000;
        long second = (mseconds % 60000) / 1000;
        long ms = mseconds % 1000;

        String hh, sm, ss, mm;
        if (hour < 10) {
            hh = "0" + String.valueOf(hour);
        } else {
            hh = String.valueOf(hour);
        }

        if (minute < 10) {
            sm = "0" + String.valueOf(minute);
        } else {
            sm = String.valueOf(minute);
        }
        if (second < 10) {
            ss = "0" + String.valueOf(second);
        } else {
            ss = String.valueOf(second);
        }

        if (ms < 10) {
            mm = "00" + String.valueOf(ms);
        } else if (ms < 100) {
            mm = "0" + String.valueOf(ms);
        } else {
            mm = String.valueOf(ms);
        }

        return hh + ":" + sm + ":" + ss + "." + mm;

    }


    public static boolean mkDirectory(String path) {
        File file = null;
        try {
            file = new File(path);
            if (!file.exists()) {
                return file.mkdirs();
            } else {
                return false;
            }
        } catch (Exception e) {
        } finally {
            file = null;
        }
        return false;
    }

    /**
     * -1:出现异常
     * 0：应景存在
     * 1：创建成功
     *
     * @param path
     * @return
     */
    public static Integer mkFile(String path) {
        File file = null;
        try {
            file = new File(path);
            if (!file.exists()) {
                file.createNewFile();
                return 1;
            } else {

                return 0;
            }
        } catch (Exception e) {
        } finally {
            file = null;
        }
        return -1;
    }

    public static String getPathByKey(String key) {

        String path = CommonUtils.class.getClassLoader().getResource("path.property").getPath();
        pathProp = new Properties();

        try {
            BufferedReader e = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
            pathProp.load(e);
            e.close();
            String value = pathProp.getProperty(key);
            return value;
        } catch (Exception var5) {
            var5.printStackTrace();
            return null;
        }

    }

    public static String toWebUrl(String path) {

        return path.replace("/Users/weifengxu/Desktop/clips/", "/usr/");
    }

    public static String tolocalUrl(String path) {

        return path.replace("/usr/", "/Users/weifengxu/Desktop/clips/");
    }

    /**
     * 获取视频信息
     *
     * @param source
     * @return
     */
    public static MultimediaInfo readVideoTime(File source) {
        Encoder encoder = new Encoder(new MFFMPEGLocator());

        try {
            MultimediaInfo m = encoder.getInfo(source);
            return m;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(source.getAbsolutePath());
        }
        return null;
    }

    /**
     * 获取所有的MP4文件
     *
     * @param strPath
     * @param fileList
     * @return
     */
    public static List<File> getMp4FileList(String strPath, List<File> fileList,String end) {
        File dir = new File(strPath);
        File[] files = dir.listFiles(); // 该文件目录下文件全部放入数组
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                String fileName = files[i].getName();
                if (files[i].isDirectory()) {
                    getMp4FileList(files[i].getAbsolutePath(), fileList,end); // 获取文件绝对路径
                } else if (fileName.endsWith(end)) {
                    fileList.add(files[i]);
                } else {
                    continue;
                }
            }

        }
        return fileList;
    }

    /**
     * 写出文本文件
     *
     * @param content
     * @param outPath
     * @throws IOException
     */
    public static void writeString(String content, String outPath) throws IOException {

        Writer out = new FileWriter(new File(outPath));
        out.write(content);
        out.close();
    }

    /**
     * 验证是不是为时间格式的字符串
     *
     * @param str
     * @return
     */

    public static boolean isValidDate(String str, String patt) {
        boolean convertSuccess = true;
        // 指定日期格式为四位年/两位月份/两位日期，注意yyyy/MM/dd区分大小写；
        SimpleDateFormat format = new SimpleDateFormat(patt);
        try {
            format.setLenient(false);
            format.parse(str);
        } catch (ParseException e) {
            convertSuccess = false;
        }
        return convertSuccess;
    }

    public static void writeAsLine(String path, List<String> subs) {

        try {
            File file = new File(path);     //文件路径（路径+文件名）
            if (!file.exists()) {   //文件不存在则创建文件，先创建目录
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            }
            BufferedWriter output = new BufferedWriter(new FileWriter(path));
            for (String srt : subs) {
                output.write(srt);
                output.newLine();
                output.flush();
            }
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String filterStr(String str) throws Exception {

        String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？_]";

        /**
         * Pattern p = Pattern.compile("a*b");
         * Matcher m = p.matcher("aaaaab");
         * boolean b = m.matches();
         */
        Pattern pat = Pattern.compile(regEx);
        Matcher mat = pat.matcher(str);
        return mat.replaceAll("").trim();
    }

    /**
     * @param list 要排序的数组
     * @param key  排序的字段
     * @param type 1正序 -1 倒叙
     */

    public static void sort(List<Map> list, final String key, final int type) {

        Collections.sort(list, new Comparator<Map>() {
            public int compare(Map o1, Map o2) {
                Integer num1 = (Integer) o1.get(key);//name1是从你list里面拿出来的一个
                Integer num2 = (Integer) o2.get(key); //name1是从你list里面拿出来的第二个name

                if (type == 1) {
                    return num1.compareTo(num2);
                }
                return num2.compareTo(num1);

            }
        });
    }

}


