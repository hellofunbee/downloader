package com.xwf.common.dao;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by weifengxu on 2018/8/9.
 */
public class ClipsDao {
    static String table_name = "clips";

    /**
     * @return
     */
    public static List<Record> listAll() {
        List<Record> records = Db.find("select * from clips");
        return records;
    }

    public static List<Record> findByTv(String tv_id) {
        String sql = "select * from clips c " +
                "inner join video v " +
                "on c.video_id = v.video_id and v.tv_id = '" + tv_id + "'";
        System.out.println(sql);
        List<Record> records = Db.find(sql);
        return records;
    }


    public static void deletByVideo_id(String video_id) {
        Db.update("delete from clips where video_id = ?", video_id);


    }

    public static void batchSave(List<Record> clips) {
        Db.batchSave(table_name, clips, clips.size());

    }

    public static void save(Record re) {
        List<String> sqls = new ArrayList<String>();
        sqls.add(checkSave(re));
        Db.batch(sqls, sqls.size());

    }


    /**
     * 如果存在则放弃插入 否则插入
     * 数据表为空的情况下永远也插入不了
     *
     * @param re
     */

    private static String checkSave(Record re) {

        String sql = "insert into clips (clips_id,clips_name,video_id,order_num,clips_addr,clips_cover,lang_type,en,cn)" +
                "select '" +
                UUID.randomUUID().toString().replace("-", "") + "','" +
                re.getStr("clips_name") + "','" +
                re.getStr("video_id") + "','" +
                re.get("order_num") + "','" +
                re.getStr("clips_addr") + "','" +
                re.getStr("clips_cover") + "','" +
                re.get("lang_type") + "','" +
                re.getStr("en") + "','" +
                re.getStr("cn") +
                "' from clips where NOT EXISTS (SELECT clips_id  FROM clips WHERE clips_name = '" + re.getStr("clips_name") + "' and video_id = '" + re.getStr("video_id") + "' ) limit 1";

        return sql;

    }


    public static List<Record> selectByTv(String tv_id) {

        if (tv_id == null) {
            return listAll();
        } else {
            return findByTv(tv_id);

        }

    }
}
