package com.xwf.common.dao;


import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.druid.DruidPlugin;
import com.xwf.common.utils.CommonUtils;
import com.xwf.common.video.Refresh;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DbRefresh {
    static int m = 0;//此变量是为了切换数据源

    public static void main(String[] args) {


//        insert("", false);


        insert_music("", false);

//        test(false);


    }

    static void test(boolean isWeb) {

        ActiveRecordPlugin arp = null;
        arp = getActiveRecordPlugin(isWeb, arp);

        List<String> records = MusicDao.isIn(Arrays.asList(new String[]{"348804151", "10769156"}));

        System.out.println(records.size());


        if (!isWeb) {
            arp.stop();
        }


    }

    /**
     * clips
     * 若tv_name为空 则全部插入
     *
     * @param tv_name
     */


    public static void insert(String tv_name, boolean isWeb) {


        ActiveRecordPlugin arp = null;
        arp = getActiveRecordPlugin(isWeb, arp);


        File[] files = new File(CommonUtils.getPathByKey("base_path")).listFiles();

        long count_1 = Db.queryLong("select count(*) from clips");


        System.out.println("执行前共计:" + count_1 + "条数据");

        for (final File file : files) {

            if (!file.isDirectory())
                continue;
            if ("ziptemp,music".indexOf(file.getName()) != -1)
                continue;
            //要插入的电视剧
            if (tv_name != null && tv_name != "")
                if (tv_name.indexOf(file.getName()) == -1) {
                    continue;
                }


            Db.tx(new IAtom() {
                public boolean run() throws SQLException {
                    int lang_type = 0;
                    if ("异形,阿甘正传".indexOf(file.getName()) != -1)
                        lang_type = 1;


                    //demo
                    Refresh.refresh(file.getPath(), lang_type);
                    return true;
                }
            });
        }
        long count_2 = Db.queryLong("select count(*) from clips");

        System.out.println("执行之后共计:" + count_2 + "条数据");


        System.out.println("本次共计插入：" + (count_2 - count_1));


        if (!isWeb) {
            arp.stop();
        }

    }


    /**
     * music
     * 若tv_name为空 则全部插入
     */

    public static void insert_music(String playlist_id, boolean isWeb) {
        final List<Exception> es = new ArrayList<Exception>();

        ActiveRecordPlugin arp = null;
        arp = getActiveRecordPlugin(isWeb, arp);


        File[] files = new File(CommonUtils.getPathByKey("audioPath")).listFiles();

        long count_1 = Db.queryLong("select count(*) from music");
        System.out.println("执行前共计:" + count_1 + "条数据");
        for (final File file : files) {

            if (!file.isDirectory())
                continue;
            //要插入的音乐
            if (playlist_id != null && playlist_id != "")
                if (file.getName().indexOf(playlist_id) == -1) {
                    continue;
                }


            Db.tx(new IAtom() {
                public boolean run() throws SQLException {
                    //demo

                    try {


                        Refresh.refresh_music(file.getPath());
                    } catch (Exception e) {
                        e.printStackTrace();
                        es.add(e);
                        return false;
                    }
                    return true;

                }
            });
        }


        long count_2 = Db.queryLong("select count(*) from music");

        System.out.println("执行之后共计:" + count_2 + "条数据");


        System.out.println("本次共计插入：" + (count_2 - count_1));


        if (arp != null) {
            arp.stop();
        }


        System.out.println("异常：" + es.size() + "个");
        for (Exception e : es) {

            System.out.println(e.getMessage());
        }

    }

    private static ActiveRecordPlugin getActiveRecordPlugin(boolean isWeb, ActiveRecordPlugin arp) {
        if (!isWeb) {
            String jdbcUrl = "jdbc:mysql://localhost:3306/talk?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull";
            DruidPlugin druidPlugin = new DruidPlugin(jdbcUrl, "talk", "talk");
            druidPlugin.start();
            arp = new ActiveRecordPlugin(druidPlugin);
            arp.start();
        }
        return arp;
    }
}
