package com.xwf.common.dao;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import java.util.List;
import java.util.UUID;

/**
 * Created by weifengxu on 2018/8/9.
 * 电影电视 关系表
 */
public class TvDao {
    static final String table_name = "tv";

    public static String save(Record tv) {
        Record back = Db.findFirst("select * from tv where tv_name = '" + tv.get("tv_name") + "'");
        if (back != null && back.get("tv_id") != null) {
            return back.get("tv_id");
        }
        String tv_id = UUID.randomUUID().toString().replace("-", "");
        tv.set("tv_id", tv_id);
        Db.save(table_name, tv);
        return tv_id;

    }

    public static List<Record> findAll() {
        return Db.find("select * from tv");
    }

    public static Record findById(String tv_id) {
        List<Record> tvs = Db.find("select * from tv where tv_id = '"+tv_id+"'");
        if(tvs == null || tvs.size() == 0)
            return null;
        return tvs.get(0);
    }


    public static boolean isExist(Record tv) {
        Record back = Db.findFirst("select * from tv where tv_name = '" + tv.get("tv_name") + "'");

        if (back != null && back.get("tv_id") != null) {

            return true;
        } else
            return false;

    }
}
