package com.xwf.common.utils;

import com.jfinal.plugin.activerecord.Record;
import com.xwf.common.dao.ClipsDao;

import java.io.*;
import java.util.*;

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

    public static List<Map> search2(String content, boolean isperfect, Record tv) {
        content = content.replaceAll("[^\\u4e00-\\u9fa5a-zA-Z^0-9]", " ").replaceAll(" +", " ");
        int str_type = CommonUtils.strType(content);
        int lang_type = 0;
        if (tv != null)
            lang_type = tv.getInt("lang_type");

        List<Map> result = new ArrayList<Map>();

        List<Record> clips = ClipsDao.selectByTv(tv == null ? null : tv.getStr("tv_id"), content);

        for (Record clip : clips) {
            or(lang_type, result, clip, content, str_type, isperfect);

            /*//匹配算法
            Map m = null;
            if (lang_type == 0)
                m = match(content, word, isperfect);
            else if (lang_type == 1)
                m = match2(content, word, isperfect);
            if (m != null) {
                m.put("file", clip.get("clips_addr"));
                result.add(m);

            }*/
        }
        return result;
    }

    /**
     * @param lang_type 电视剧的语言
     * @param maps      匹配到的结果数组
     * @param clip      剪辑
     * @param content   关键词
     * @param c         关键词的中英文
     * @param isperfect 是否完全匹配
     */

    private static void or(int lang_type, List<Map> maps, Record clip, String content, int c, boolean isperfect) {
        String word = null;
        if(clip.getInt("lang_type") == 1){
            word = clip.getStr("en");
        }else {
            word = clip.getStr("cn");
        }

        if (word == null || word.length() == 0)
            return;


        int w = CommonUtils.strType(word);
        Map m = null;

        //英文中招汉语 pass掉
        if (lang_type == 1 && c == 0) {
            return;
        }

        //汉字
        if (c == 0 && w == 0) {
            doAdd(maps, match(content, word, isperfect), clip);
            //英文
        } else if (c == 1 && w == 1) {
            doAdd(maps, match2(content, word, isperfect), clip);
        } else {
            doAdd(maps, match(content, word, isperfect), clip);
            doAdd(maps, match2(content, word, isperfect), clip);

        }

    }

    private static void doAdd(List<Map> maps, Map m, Record clip) {
        if (m != null) {
            m.put("file", clip.get("clips_addr"));
            maps.add(m);
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

    /**
     * @param content   要查询的内容
     * @param isperfect 是否完全匹配
     * @param word      数据源
     *                  \u4e00-\u9fa5a #汉字的编码
     *                  a-zA-Z #英文字母
     *                  [^] #表示“非”
     * @return
     */

    public static Map match2(String content, String word, boolean isperfect) {


        content = content.toLowerCase();
        word = word.toLowerCase();

        List<String> lw = new ArrayList<String>();
        List<String> lc = new ArrayList<String>();

        String[] words = word.replaceAll("[^\\u4e00-\\u9fa5a-zA-Z^0-9]", " ").replaceAll(" +", " ").split(" ");
        String contents[] = content.split(" ");

        word = "";
        content = "";
        for (int i = 0; i < words.length; i++) {
            if (words[i].length() > 0) {
                lw.add(words[i]);
                word += words[i];
            }
        }
        for (int i = 0; i < contents.length; i++) {
            if (contents[i].length() > 0) {
                lc.add(contents[i]);
                content += contents[i];
            }
        }

        Map m = new HashMap();
        //完全匹配
        if (word.equals(content)) {
            m.put("type", 100);
            //完全包含
        } else if (!isperfect && word.indexOf(content) != -1) {
            int l = LevenShtein.getSimilarityRatio_En(lc, lw, false);
            m.put("type", 150 - l / 2);//75 - 100
            //跳跃包含
        } else if (!isperfect && LevenShtein.isIn_En(lc, lw)) {
            int l = LevenShtein.getSimilarityRatio_En(lc, lw, false);
            m.put("type", 180 - l * 10 / 33);//60-75
            //相似度
        } else {
            int l = LevenShtein.getSimilarityRatio_En(lc, lw, true);

            if (l >= 60)
                m.put("type", 230 - l / 2); //35 -60
            else
                return null;
            //相似度

        }


        return m;
    }


}
