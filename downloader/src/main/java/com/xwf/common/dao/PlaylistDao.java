package com.xwf.common.dao;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

/**
 * Created by weifengxu on 2018/8/9.
 * 播单 关系表
 */
public class PlaylistDao {
    static final String table_name = "playlist";

    public static void save(Record video) {

        Record back = Db.findFirst("select * from playlist where playlist_id = '" + video.get("playlist_id") + "' ");

        if (back != null && back.get("playlist_id") != null) {
            return;
        }

        Db.save(table_name, video);


    }
}
