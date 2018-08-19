package com.xwf.common.http;


import com.xwf.common.utils.CommonUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by weifengxu on 2018/4/10.
 */
public class MIDIShowDownloader {


    static String dir = "/Users/weifengxu/Desktop/temp/midi/";
    static String url = "http://www.midishow.com/midi/file/#.mid";
    static List<ProxyInfo> ipresult;
    static int ipIndex = 0;
    static int i = 56231;

    public static void main(String args[]) throws InterruptedException, IOException {
        CommonUtils.mkDirectory(dir);

//        if (getProxy()) {
//            download(ipIndex);
//        }

        download(ipIndex);

    }

    private static void download(int index) {

        String file = "";
        Map proxy = setProxy(index);
        if (proxy == null) {
            return;
        }
        try {

            while (i > 0) {

                file = dir + i + ".mid";

                if (new File(file).exists()) {

                    System.out.println("已存在：" + file);
                    i--;
                    continue;
                }

                System.out.println("【下载】--" + i);
                CommonUtils.mkFile(file);
                HttpUtils.downLoad(url.replace("#", "" + i), proxy, file);
                i--;

            }
        } catch (Exception e) {
            e.printStackTrace();
            ipIndex++;
            System.out.println("ipIndex:" + ipIndex);
            if (file != null)
                new File(file).delete();

            download(ipIndex);

        }

    }

    private static Map<String, String> setProxy(int i) {
        if (i > 400) {
            return null;
        }
//        String ip = HttpUtils.sendGet("http://127.0.0.1:5010/get/", new HashMap<String, String>());
        String ip = HttpUtils.sendGet("http://123.207.35.36:5010/get?key=thankyou", new HashMap<String, String>());
        if (ip != null) {
            String ip_port[] = ip.split(":");
            Map<String, String> m = new HashMap();

            if (ip_port.length > 1) {
                m.put("ip", ip_port[0]);
                m.put("port", ip_port[1]);
                return m;
            } else {
                return setProxy(ipIndex++);
            }


        } else {
            return setProxy(ipIndex++);

        }


       /* ProxyInfo pi = ipresult.get(i);
        System.out.println("代理IP------>" + pi.getIp()+":"+pi.getPort());

        Map<String, String> m = new HashMap();
        m.put("ip", pi.getIp());
        m.put("port", pi.getPort());
        return m;*/
    }

    private static boolean getProxy() {


        try

        {
            //获取代理ip
            ProxyCralwerUnusedVPN proxyCrawler = new ProxyCralwerUnusedVPN();
            //想要获取的代理IP个数，由需求方自行指定。（如果个数太多，将导致返回变慢）
            ipresult = proxyCrawler.startCrawler(10);
            if (ipresult.size() > 0) {
                return true;
            } else {
                return false;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
