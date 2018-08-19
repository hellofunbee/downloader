package com.xwf.common.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by weifengxu on 2018/8/8.
 */
public class Searcher {

    public static void main(String[] args) {
//
//        List<Map> result = search("你的娇滴滴的娘子呢", false);
//        for (Map sb : result) {
//            System.out.println(sb.get("file"));
//        }


    }

    /**
     * 关键词查找 完全匹配 包含 关系
     *
     * @param content
     * @param isperfect 完全匹配
     * @return
     */

    public static List<Map> search(String type,String content, boolean isperfect, String path) {
        content = content.replace(" ", "");

        List<Map> result = new ArrayList<Map>();
        List<File> fileList = CommonUtils.getMp4FileList(path, new ArrayList<File>(),type);


        for (File file : fileList) {
            String name = file.getName();
            String word = "";
            if (name != null && name.indexOf("--") != -1) {
                word = name.substring(name.indexOf("--") + 2);
                word = word.replace(type, "");
                word = word.replace(" ", "");
                //匹配算法

                //完全匹配
                if (word.equals(content)) {
                    Map m = new HashMap();
                    m.put("file", file);
                    m.put("type", 100);
                    result.add(m);
                    //完全包含
                } else if (!isperfect && word.indexOf(content) != -1) {
                    int l = LevenShtein.getSimilarityRatio(content, word,false);
                    Map m = new HashMap();
                    m.put("file", file);
                    m.put("type", 150 - l/2);//75 - 100
                    result.add(m);
                    //跳跃包含
                } else if (!isperfect && LevenShtein.isIn(content, word)) {
                    int l = LevenShtein.getSimilarityRatio(content, word,false);

                    Map m = new HashMap();
                    m.put("file", file);
                    m.put("type", 180 - l*10/33);//60-75
                    result.add(m);
                    //相似度
                } else {

                    int l = LevenShtein.getSimilarityRatio(content, word,true);
                    if (l > 50) {
                        Map m = new HashMap();
                        m.put("file", file);
                        m.put("type", 230 - l/2); //35 -60
                        result.add(m);
                        //相似度
                    }
                }

            }
        }
        return result;
    }


}
