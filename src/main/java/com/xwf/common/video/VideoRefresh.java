package com.xwf.common.video;

import com.jfinal.plugin.activerecord.Record;
import com.xwf.common.dao.MovieDao;
import com.xwf.common.dao.VideoDao;

import java.io.File;

/**
 * Created by weifengxu on 2018/8/9.
 */
public class VideoRefresh {
    static String path = "/Users/weifengxu/Desktop/clips/xyj-86/";


    public static void refresh() {

        File[] fileList = new File(path).listFiles();

        int i = 0;
        for (File move : fileList) {
            if (!move.isDirectory())
                continue;
            Record movie = new Record();
            movie.set("movie_pid", "1");
            movie.set("movie_name", "西游记（84）" + move.getName() + "集");
            movie.set("order", i);
            String movie_id = MovieDao.save(movie);
            i++;

            System.out.println(move);
            saveVideos(move, movie_id);
        }


    }

    public static void saveVideos(File movie, String movie_id) {
        File[] fileList = movie.listFiles();

        int i = 0;
        for (File f : fileList) {
            if (f.getAbsolutePath().indexOf(".mp4") == -1)
                continue;
            System.out.println(f);
            String name = f.getName();

            Record video = new Record();
            video.set("video_pid", movie_id);
            video.set("video_name", name.substring(name.indexOf("--") + 2));
            video.set("order", i);
            video.set("video_path", f.getAbsolutePath());
            VideoDao.save(video);
            i++;


        }

    }
}
