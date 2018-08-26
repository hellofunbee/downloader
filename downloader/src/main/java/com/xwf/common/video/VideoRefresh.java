package com.xwf.common.video;

import com.jfinal.plugin.activerecord.Record;
import com.xwf.common.dao.ClipsDao;
import com.xwf.common.dao.TvDao;
import com.xwf.common.dao.VideoDao;
import com.xwf.common.utils.CommonUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by weifengxu on 2018/8/9.
 */
public class VideoRefresh {
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
            if (f.getAbsolutePath().indexOf(".mp4") == -1)
                continue;


            String name = f.getName();

            Record clips = new Record();
            clips.set("clips_id", UUID.randomUUID().toString().replace("-", ""));
            clips.set("clips_md5", CommonUtils.backMD5(video_id + name));
            clips.set("video_id", video_id);
            clips.set("clips_name", name);
            clips.set("order_num", i);
            clips.set("clips_addr", f.getAbsolutePath());
            clips.set("clips_cover", f.getAbsolutePath().replace(".mp4", ".jpg"));

            clips.set("lang_type", lang_type);

            String word = name.substring(name.indexOf("--") + 2, name.lastIndexOf("."));
            if (lang_type == 1)
                clips.set("en", word);
            if (lang_type == 0)
                clips.set("cn", word);


//            ClipsDao.save(clips);

            i++;

            res.add(clips);

        }

        ClipsDao.batchSave(res);


    }


}
