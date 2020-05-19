package com.xwf.common.utils;

import com.jfinal.plugin.activerecord.Record;
import com.xwf.common.dao.ClipsDao;
import com.xwf.common.utils.Beans.Clip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import subtitleFile.Caption;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by weifengxu on 2018/8/8.
 */
public class Searcher {
    static Logger log = LoggerFactory.getLogger(Searcher.class);
    static List<File> fileList = null;

    static List<Clip> result = new ArrayList<Clip>();


    /**
     * 关键词查找 完全匹配 包含 关系 [文件]
     *
     * @param isperfect 完全匹配
     * @return
     */

    public static List<Clip> search2(String searchWord, boolean isperfect, Record tv) {
        searchWord = searchWord.replaceAll("[^\\u4e00-\\u9fa5a-zA-Z^0-9]", " ").replaceAll(" +", " ");
        int str_type = CommonUtils.strType(searchWord);
        int lang_type = 0;
        if (tv != null)
            lang_type = tv.getInt("lang_type");


        List<Record> clips = ClipsDao.selectByTv(tv == null ? null : tv.getStr("tv_id"), searchWord);

        result.clear();
        for (Record clip : clips) {
            doFilterNeeded(lang_type, clip, searchWord, str_type, isperfect);
        }
        return result;
    }

    /**
     * @param lang_type 电视剧的语言
     * @param clip      剪辑
     * @param content   关键词
     * @param wordType  关键词的中英文
     * @param isperfect 是否完全匹配
     */

    private static void doFilterNeeded(int lang_type, Record clip, String content, int wordType, boolean isperfect) {
        String word = null;
        if (clip.getInt("lang_type") == 1) {
            word = clip.getStr("en");
        } else {
            word = clip.getStr("cn");
        }

        if (word == null || word.length() == 0)
            return;


        int w = CommonUtils.strType(word);

        //英文中招汉语 pass掉
        if (lang_type == 1 && wordType == 0) {
            return;
        }
        Clip clips = new Clip();
        clips.setFile("" + clip.get("clips_addr"));
        int index = 0;
        //汉字
        if (wordType == 0 && w == 0) {
            index = match(content, word, isperfect);
            //英文
        } else if (wordType == 1 && w == 1) {
            index = match2(content, word, isperfect);
        } else {
            int index1 = match(content, word, isperfect);
            int index2 = match2(content, word, isperfect);

            if(index1 > index2){
                index = index1;
            }else {
                index = index2;

            }

        }
        if (index > 0) {
            clips.setType(index);

            result.add(clips);
        }

    }

    /**
     * @param content   要查询的内容
     * @param isperfect 是否完全匹配
     * @param word      数据源
     * @return
     */

    public static int match(String content, String word, boolean isperfect) {
        //完全匹配
        if (word.equals(content)) {
            return 100;
            //完全包含
        } else if (!isperfect && word.indexOf(content) != -1) {
            int l = LevenShtein.getSimilarityRatio(content, word, false);
            return 150 - l / 2;//75 - 100
            //跳跃包含
        } else if (!isperfect && LevenShtein.isIn(content, word)) {
            int l = LevenShtein.getSimilarityRatio(content, word, false);
            return 180 - l * 10 / 33;//60-75
            //相似度
        } else {
            int l = LevenShtein.getSimilarityRatio(content, word, true);

            if (l >= 60) {
                return 230 - l / 2; //35 -60
            }
            //相似度
        }
        return 0;
    }

    /**
     * english
     *
     * @param content   要查询的内容
     * @param isperfect 是否完全匹配
     * @param word      数据源
     *                  \u4e00-\u9fa5a #汉字的编码
     *                  a-zA-Z #英文字母
     *                  [^] #表示“非”
     * @return
     */

    public static int match2(String content, String word, boolean isperfect) {


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

        //完全匹配
        if (word.equals(content)) {
            return 100;
            //完全包含
        } else if (!isperfect && word.indexOf(content) != -1) {
            int l = LevenShtein.getSimilarityRatio_En(lc, lw, false);
            return 150 - l / 2;//75 - 100
            //跳跃包含
        } else if (!isperfect && LevenShtein.isIn_En(lc, lw)) {
            int l = LevenShtein.getSimilarityRatio_En(lc, lw, false);
            return 180 - l * 10 / 33;//60-75
            //相似度
        } else {
            int l = LevenShtein.getSimilarityRatio_En(lc, lw, true);

            if (l >= 60) {
                return 230 - l / 2; //35 -60
            }
        }

        return 0;
    }

    private static boolean pass(Caption cp) {
        if (cp.end.mseconds - cp.start.mseconds < 50)
            return true;
        return false;
    }


}
