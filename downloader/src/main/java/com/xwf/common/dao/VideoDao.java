package com.xwf.common.dao;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import java.util.List;
import java.util.UUID;

/**
 * Created by weifengxu on 2018/8/9.
 */
public class VideoDao {
    /**
     * @return
     */
    public static List<Record> listAll() {
        List<Record> records = Db.find("select * from video");
        return records;
    }



    public static void save(Record re){

        re.set("video_id", UUID.randomUUID().toString().replace("-",""));
        Db.save("video",re);
    }


}
