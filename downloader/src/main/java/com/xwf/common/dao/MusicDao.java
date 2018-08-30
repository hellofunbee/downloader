package com.xwf.common.dao;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import java.util.ArrayList;
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

    public static List<String> isIn(List<String> ids) {
        List result = new ArrayList();
        StringBuffer sb = new StringBuffer();
        for (String id : ids) {
            sb.append("'");
            sb.append(id);
            sb.append("'");
            sb.append(",");

        }

        String sql = "select music_id from  music where music_id in (" + sb.substring(0, sb.length() - 1) + ")";

        List<Record> b = Db.find(sql);
        if (result != null && result.size() == 0)
            for (Record r : b)
                result.add(r.getStr("music_id"));


        return result;
    }

    public static List<Record> isexist(List<Record> ids) {
        StringBuffer sb = new StringBuffer();
        for (Record id : ids) {
            sb.append("'");
            sb.append(id.get("music_id"));
            sb.append("'");
            sb.append(",");

        }
        String sql = "select music_id,srt_path from  music where music_id in (" + sb.substring(0, sb.length() - 1) + ")";

        return Db.find(sql);
    }

    public static void deletByid(String music_id) {
        Db.update("delete from music where music_id = ?", music_id);
    }

    public static List<Record> page(){
        Db.paginate(1,1,"","","");
        return null;
    }
}
