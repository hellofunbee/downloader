package com.xwf.common.dao;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import java.util.UUID;

/**
 * Created by weifengxu on 2018/8/9.
 * 电影电视 关系表
 */
public class MovieDao {
    static final String table_name = "movie";

    public static String save(Record movie) {
        String movie_id = UUID.randomUUID().toString().replace("-","");
        movie.set("movie_id",movie_id);
        Db.save(table_name, movie);
        return movie_id;

    }
}
