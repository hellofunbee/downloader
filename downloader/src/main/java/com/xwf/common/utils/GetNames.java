package com.xwf.common.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by weifengxu on 2018/8/1.
 * 文本去重
 */
public class GetNames {

    public static String path = "/Users/weifengxu/Downloads/音源/Mian Jin Ge/";
    public static String pathA = "/Users/weifengxu/Desktop/music/学猫叫/xmj.txt";

    public static void main(String args[]) throws Exception {
        String result = toA_B(listA(), listB());

        String the_more_word = getWordNotIn(result);

        System.out.println("需要补充的拼音:共计" + result.split(" ").length + "个---" + result);
        System.out.println("请替换以下汉字:共计" + the_more_word.length() + "个【" + the_more_word + "】");


        System.out.println("-----------------:请从下面文字中选择替换：----------\n" + pinyinToWords(listB()));
    }

    public static List<String> listA() throws Exception {
        List<String> bs;
        String as = FileUtils.readFileToString(new File(pathA));


        HanyuPinyinHelper hp = new HanyuPinyinHelper();
        String word = DeDubble.deDubble(as);
        bs = hp.toHanyuPinyin(word);


        System.out.println("原始文本去重之后汉字：【" + word + "】");
        System.out.println("原始文本去重之后拼音：【" + bs.toString() + "】");

        return bs;
    }

    public static List<String> listB() throws Exception {
        StringBuffer sb = new StringBuffer();
        List<String> bs = new ArrayList<String>();
        File file = null;

        file = new File(path);


        File[] files = file.listFiles();
        if (files != null && files.length > 0) {
            for (File f : files) {

                String name = f.getName();
                if ("wav".equals(name.substring(name.lastIndexOf(".") + 1))) {
                    sb.append(f.getName().replace(".wav", ""));
                    sb.append(" ");

                    bs.add(f.getName().replace(".wav", ""));
                }
            }

        }
        //System.out.println("音源包含拼音：" + bs.toString());

        return bs;
    }

    //数组a比b数组多余的元素
    public static String toA_B(List<String> a, List<String> b) {
        StringBuffer sb = new StringBuffer();
        boolean has = false;
        for (String aa : a) {
            has = false;
            for (String bb : b) {
                if (aa.equals(bb)) {
                    has = true;
                    break;
                }
            }

            if (!has) {
                sb.append(aa);
                sb.append(" ");
            }
        }

        return sb.toString();

    }

    /**
     * 根据多出来的拼音 获取原始歌词中对应的汉字
     *
     * @param pinyin_not_include
     * @return
     * @throws IOException
     */

    public static String getWordNotIn(String pinyin_not_include) throws IOException {
        HanyuPinyinHelper hp = new HanyuPinyinHelper();
        StringBuffer sb = new StringBuffer();
        String source = FileUtils.readFileToString(new File(pathA));
        String word = DeDubble.deDubble(source);

        List<String> not_includs = Arrays.asList(pinyin_not_include.split(" "));

        for (int i = 0; i < word.length(); i++) {
            String w = word.substring(i, i + 1);
            List<String> pinyins = hp.toHanyuPinyin(w);

            if (pinyins.size() == 1) {
                if (not_includs.indexOf(pinyins.get(0)) != -1) {
                    sb.append(w);
                }

            }


        }

        return sb.toString();


    }


    public static String pinyinToWords(List<String> pinyi) throws IOException {
        String path = GetNames.class.getClassLoader().getResource("char.json").getPath();
        JSONObject jo = JSONObject.parseObject(FileUtils.readFileToString(new File(path)));
        HanyuPinyinHelper hp = new HanyuPinyinHelper();


        String word = jo.getString("chars");
        StringBuffer sb = new StringBuffer();
        StringBuffer temp = new StringBuffer();
        boolean has = false;

        for (int j = 0; j < pinyi.size(); j++) {

            String p = pinyi.get(j);
            String start = p;
            while (start.length() < 8) {
                start += " ";
            }
            temp.append("【   " + start + "】--【");

            for (int i = 0; i < word.length(); i++) {
                String w = word.substring(i, i + 1);
                List<String> pinyins = hp.toHanyuPinyin(w);

                if (pinyins.size() == 1) {
                    if (p.equals(pinyins.get(0))) {
                        has = true;
                        temp.append(w + ",");
                    }
                }

            }

            temp.append("】\n");

            if (has) {
                sb.append(temp.toString());
            }

            temp = new StringBuffer();
            has = false;

        }

        return sb.toString();

    }
}
