package com.xwf.common.video;

import com.jfinal.plugin.activerecord.Record;
import com.xwf.common.dao.*;
import com.xwf.common.utils.CommonUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by weifengxu on 2018/8/9.
 */
public class Refresh {
    /**
     * @param path
     * @param lang_type 语言类型0：1 中：英
     */
    public static void refresh(String path, int lang_type) {

        Record tv = new Record();
        tv.set("tv_name", new File(path).getName());
        tv.set("type", 0);//电视电影 0：1
        tv.set("lang_type", lang_type);//中英文0：1
        if (TvDao.isExist(tv)) {
            System.out.println("电视剧已存在：" + new File(path).getName());
//            return;
        }

        String tv_id = TvDao.save(tv);


        File[] fileList = new File(path).listFiles();


        int i = 0;
        for (File video : fileList) {
            if (!video.isDirectory())
                continue;
            Record v = new Record();
            v.set("tv_id", tv_id);
            v.set("video_name", new File(path).getName() + video.getName());
            v.set("srt", null);
            v.set("eps", i + 1);//集数
            String video_id = VideoDao.save(v);
            i++;

            System.out.println(video);
            saveVideos(video, video_id, lang_type);
        }


    }

    public static void saveVideos(File video, String video_id, int lang_type) {
        File[] fileList = video.listFiles();
        List<Record> res = new ArrayList<Record>();

        ClipsDao.deletByVideo_id(video_id);

        int i = 0;
        for (File f : fileList) {
            String videoPath = f.getAbsolutePath();
            try {
                if (!f.getName().endsWith(".mp4"))
                    continue;

                String name = f.getName();

                Record clips = new Record();
                clips.set("clips_id", UUID.randomUUID().toString().replace("-", ""));
                clips.set("clips_md5", CommonUtils.backMD5(video_id + name));
                clips.set("video_id", video_id);
                clips.set("clips_name", name);
                clips.set("order_num", i);
                clips.set("clips_addr", videoPath);
                clips.set("clips_cover", CommonUtils.mp42Jpg(videoPath));

                clips.set("lang_type", lang_type);

                String word = name.substring(name.indexOf("--") + 2, name.lastIndexOf("."));
                if (lang_type == 1)
                    clips.set("en", word);
                if (lang_type == 0)
                    clips.set("cn", word);


//            ClipsDao.save(clips);

                i++;

                res.add(clips);
            }catch (Exception r){
                r.printStackTrace();
                System.out.println("insert clips failed ： " + videoPath);
            }

        }

        ClipsDao.batchSave(res);


    }

    static int i = 0;

    /**
     * @param path //播放列表
     */
    public static void refresh_music(String path) {


        if (path.lastIndexOf("--") == -1) {
            System.out.println("播单id为空!!!!");
            CommonUtils.deleteFile(new File(path));
            return;
        }


        String playlist_id = path.substring(path.lastIndexOf("--") + 2);
        if (CommonUtils.strType(playlist_id) != 1) {

            System.out.println(playlist_id + "错误");
            CommonUtils.deleteFile(new File(path));
            return;
        }


        if (i++ % 100 == 0)
            System.out.println("播单:" + playlist_id);


        Record play_list = new Record();
        play_list.set("playlist_name", new File(path).getName());
        play_list.set("playlist_id", playlist_id);

        List<Record> musics = new ArrayList<Record>();


        PlaylistDao.save(play_list);


        MusicDao.deletBypid(playlist_id);

        List<File> fileList = CommonUtils.getMp4FileList(path, new ArrayList<File>(), ".srt");

        for (File music : fileList) {
            String name = music.getName();
            String music_id = name.substring(name.lastIndexOf("--") + 2, name.lastIndexOf("."));


            if (CommonUtils.strType(music_id) != 1) {

                System.out.println(playlist_id + "错误");
                CommonUtils.deleteFile(music);
                return;
            }


            Record m = new Record();
            m.set("playlist_id", playlist_id);
            m.set("music_id", music_id);
            m.set("music_name", name);
            m.set("srt_path", music.getAbsolutePath());
            m.set("has_srt", 1);//srt

            musics.add(m);


        }

        MusicDao.batchSave(musics);


    }


}
