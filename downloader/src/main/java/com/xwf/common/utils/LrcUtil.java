package com.xwf.common.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weifengxu on 2018/8/12.
 */
public class LrcUtil {


    public static List<String> parse(List<String> time_content) {
        List<String[]> sb = new ArrayList<String[]>();
        List<String> result = new ArrayList<String>();
        int n = 0;
        for (int i = 0; i < time_content.size(); i++) {
            String str = time_content.get(i);
            //校验格式错误 直接返回
            if (str != null && str.split("]").length > 2) {

                return null;
            }

            String[] time = getTime(str);


            if (time != null) {
                sb.add(time);
            }

        }
        int index = 0;

        for (int j = 0; j < sb.size() - 1; j++) {
            String[] arr = sb.get(j);
            if (arr[1] != null) {
                String[] arr_next = sb.get(j + 1);

                result.add(String.valueOf(++index));
                result.add(arr[0] + " --> " + arr_next[0]);
                result.add(arr[1] + "\n");
            }

        }
        return result;

    }


    public static String[] getTime(String str) {
        if (str == null || !str.startsWith("["))
            return null;

        str = str.substring(1, str.length());
        String[] result = new String[2];


        String[] pare = str.split("]");
        if (pare.length == 2) {
//           时间、歌词
            String time = pare[0];
            if (CommonUtils.isValidDate(time, "mm:ss.SSS")) {

                time = validSS(time);
                result[0] = "00:" + time.replace(".", ",");
                result[1] = pare[1];
                return result;
            }
        } else if (pare.length == 1) {
            String time = pare[0];
            if (CommonUtils.isValidDate(time, "mm:ss.SSS")) {
                time = validSS(time);
                result[0] = "00:" + time.replace(".", ",");
                return result;
            }


        }

        return null;
    }

    public static String validSS(String time) {
        String temp = time.substring(time.indexOf("."));
        if (temp.length() == 1) {
            time += "0";
        } else if (temp.length() == 2) {
            time += "00";
        } else if (temp.length() == 3) {
            time += "0";
        }

        return time;
    }


}
