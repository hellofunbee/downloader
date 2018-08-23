package com.xwf.common.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by weifengxu on 2018/8/8.
 */
public class Searcher {

    public static void main(String[] args) throws IOException {




        List<Map> result = searchLrc(".lrc", "你的娇滴滴的娘子呢", false, "/Users/weifengxu/Downloads/music");
        for (Map sb : result) {
            System.out.println(sb.get("str") + "--" + (300 - (Integer) sb.get("type")));
            System.out.println();
        }

    }

    /**
     * 关键词查找 完全匹配 包含 关系 [文件]
     *
     * @param content
     * @param isperfect 完全匹配
     * @return
     */

    public static List<Map> search(String type, String content, boolean isperfect, String path) {
        content = content.replace(" ", "");

        List<Map> result = new ArrayList<Map>();
        List<File> fileList = CommonUtils.getMp4FileList(path, new ArrayList<File>(), type);

        for (File file : fileList) {
            String name = file.getName();
            String word = "";
            if (name != null && name.indexOf("--") != -1) {
                word = name.substring(name.indexOf("--") + 2);
                word = word.replace(type, "");
                word = word.replace(" ", "");
                //匹配算法
                Map m = match(content, word, isperfect);
                if (m != null) {
                    m.put("file", file);
                    result.add(m);
                }
            }
        }
        return result;
    }

    /**
     * 关键词查找 完全匹配 包含 关系【歌词内容】
     *
     * @param content
     * @param isperfect 完全匹配
     * @return
     */

    public static List<Map> searchLrc(String type, String content, boolean isperfect, String path) throws IOException {
        content = content.replace(" ", "");

        List<Map> result = new ArrayList<Map>();
        List<File> fileList = CommonUtils.getMp4FileList(path, new ArrayList<File>(), type);

        InputStreamReader isr = null;
        for (File file : fileList) {

            try {


                String strLine = null;
                isr = new InputStreamReader(new FileInputStream(
                        file));
                BufferedReader br = new BufferedReader(isr);
                while (null != (strLine = br.readLine())) {
                    strLine = strLine.replaceAll("\\[[^\\]]+\\]", "").replace(" ", "");

                    //匹配算法
                    Map m = match(content, strLine, isperfect);
                    if (m != null) {
                        m.put("str", strLine);
                        result.add(m);
                    }
                }
                isr.close();

            } catch (Exception e) {
                e.printStackTrace();

            } finally {
                if (isr != null)
                    isr.close();
            }
        }
        return result;
    }

    /**
     * @param content   要查询的内容
     * @param isperfect 是否完全匹配
     * @param word      数据源
     * @return
     */

    public static Map match(String content, String word, boolean isperfect) {

        Map m = new HashMap();
        //完全匹配
        if (word.equals(content)) {
            m.put("type", 100);
            //完全包含
        } else if (!isperfect && word.indexOf(content) != -1) {
            int l = LevenShtein.getSimilarityRatio(content, word, false);
            m.put("type", 150 - l / 2);//75 - 100
            //跳跃包含
        } else if (!isperfect && LevenShtein.isIn(content, word)) {
            int l = LevenShtein.getSimilarityRatio(content, word, false);
            m.put("type", 180 - l * 10 / 33);//60-75
            //相似度
        } else {
            int l = LevenShtein.getSimilarityRatio(content, word, true);

            if (l >= 60)
                m.put("type", 230 - l / 2); //35 -60
            else
                return null;
            //相似度


        }


        return m;
    }


}
