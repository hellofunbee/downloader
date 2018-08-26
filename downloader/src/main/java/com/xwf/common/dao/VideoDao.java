package com.xwf.common.dao;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import java.util.UUID;

/**
 * Created by weifengxu on 2018/8/9.
 * 电影电视 关系表
 */
public class VideoDao {
    static final String table_name = "video";

    public static String save(Record video) {

        Record back = Db.findFirst("select * from video where tv_id = '" + video.get("tv_id") + "' && video_name = '" + video.get("video_name")+"'");

        if (back != null && back.get("video_id") != null) {

            return back.get("video_id");
        }


        String video_id = UUID.randomUUID().toString().replace("-", "");
        video.set("video_id", video_id);
        Db.save(table_name, video);
        return video_id;

    }
}
