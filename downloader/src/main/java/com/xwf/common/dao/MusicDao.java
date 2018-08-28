package com.xwf.common.dao;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import java.util.List;

/**
 * Created by weifengxu on 2018/8/26.
 */
public class MusicDao {
    static String table_name = "music";

    public static void batchSave(List<Record> musics) {
        Db.batchSave(table_name, musics, musics.size());

    }

    public static void deletBypid(String playlist_id) {
        Db.update("delete from music where has_srt = 1 and playlist_id = ? ", playlist_id);

    }

    public static boolean isExit(String music_id) {
        Record back = Db.findFirst("select * from music where music_id = ?", music_id);

        if (back != null && back.get("playlist_id") != null) {
            return true;
        }

        return false;

    }


}
