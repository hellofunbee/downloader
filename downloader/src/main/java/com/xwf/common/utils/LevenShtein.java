package com.xwf.common.utils;

/**
 * Created by weifengxu on 2018/8/5.
 */

public class LevenShtein {
    private static int compare(String str, String target) {
        int d[][];              // 矩阵
        int n = str.length();
        int m = target.length();
        int i;                  // 遍历str的
        int j;                  // 遍历target的
        char ch1;               // str的
        char ch2;               // target的
        int temp;               // 记录相同字符,在某个矩阵位置值的增量,不是0就是1
        if (n == 0) {
            return m;
        }
        if (m == 0) {
            return n;
        }
        d = new int[n + 1][m + 1];
        for (i = 0; i <= n; i++) {                       // 初始化第一列
            d[i][0] = i;
        }

        for (j = 0; j <= m; j++) {                       // 初始化第一行
            d[0][j] = j;
        }

        for (i = 1; i <= n; i++) {                       // 遍历str
            ch1 = str.charAt(i - 1);
            // 去匹配target
            for (j = 1; j <= m; j++) {
                ch2 = target.charAt(j - 1);
                if (ch1 == ch2 || ch1 == ch2 + 32 || ch1 + 32 == ch2) {
                    temp = 0;
                } else {
                    temp = 1;
                }
                // 左边+1,上边+1, 左上角+temp取最小
                d[i][j] = min(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1] + temp);
            }
        }
        return d[n][m];
    }

    private static int min(int one, int two, int three) {
        return (one = one < two ? one : two) < three ? one : three;
    }

    /**
     * 获取两字符串的相似度
     */

    public static int getSimilarityRatio(String str, String target, boolean reduceMore) {

        if (reduceMore)
            target = reduceMore(str, target);
        float f = (1 - (float) compare(str, target) / Math.max(str.length(), target.length()));
        f = f * 100;
        return Float.valueOf(f).intValue();
    }

    /**
     * 减去B中多余的
     *
     * @return
     */
    private static String reduceMore(String str, String target) {

        for (int i = 0; i < target.length(); i++) {
            String w = target.substring(i, i + 1);
            if (str.indexOf(w) == -1) {
                target = target.replace(w, "");
            }
        }

        return target;

    }

    /**
     * 如果A与B比较，则先要把B比A多的去掉
     */
    public static boolean isIn(String a, String b) {
        boolean isIn = true;
        for (int i = 0; i < a.length(); i++) {
            if (b.indexOf(a.substring(i, i + 1)) == -1) {
                isIn = false;
                break;
            }
        }
        return isIn;

    }

    public static void main(String[] args) {
        LevenShtein lt = new LevenShtein();
        String str = "减去中多余的呼和氨基酸超级女声长时间你抽时间";
        String target = "减去中多余的呼和氨基酸超级女声长时间你抽时间好";


        System.out.println("similarityRatio=" + lt.getSimilarityRatio(str, target,false) + "\nISIN:" + isIn(str, target));
    }
}