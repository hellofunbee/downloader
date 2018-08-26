package com.xwf.common.dao;


import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.druid.DruidPlugin;
import com.xwf.common.utils.CommonUtils;
import com.xwf.common.video.VideoRefresh;

import java.io.File;
import java.sql.SQLException;

public class ClipsRefresh {
    static int m = 0;//此变量是为了切换数据源

    public static void main(String[] args) {


        insert("");


    }

    /**
     * 若tv_name为空 则全部插入
     * @param tv_name
     */

    private static void insert(String tv_name) {
        String jdbcUrl = "jdbc:mysql://localhost:3306/talk?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull";
        DruidPlugin druidPlugin = new DruidPlugin(jdbcUrl, "talk", "talk");
        druidPlugin.start();
        ActiveRecordPlugin arp = new ActiveRecordPlugin(druidPlugin);
        arp.start();


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
                    VideoRefresh.refresh(file.getPath(), lang_type);
                    return true;
                }
            });
        }
        long count_2 = Db.queryLong("select count(*) from clips");

        System.out.println("执行之后共计:" + count_2 + "条数据");


        System.out.println("本次共计插入：" + (count_2 - count_1));


        arp.stop();
    }
}
