package com.xwf.common.controller;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.xwf.common.http.NetEaseCloudMusic;
import com.xwf.common.utils.CommonUtils;
import com.xwf.common.utils.MainExe;
import com.xwf.common.utils.VideoCutMain;
import com.xwf.common.video.VideoRefresh;

import java.io.File;
import java.sql.SQLException;

/**
 * Created by weifengxu on 2018/8/9.
 */
public class TaskController extends Controller {
    //默认加载index方法
    public void index() {
        String index = "/refresh:同步剪辑数据库\n"
                + "/musicDownload:下载音乐\n"
                + "/videoCut:截取视频\n"
                + "/coverTake:截取cover\n";

        renderText(index);
    }


    public void refresh() {

        String tv_name = getAttrForStr("tv_name");

        File[] files = new File(CommonUtils.getPathByKey("base_path")).listFiles();

        long count_1 = Db.queryLong("select count(*) from clips");
        System.out.println("执行前共计:" + count_1 + "条数据");

        for (final File file : files) {

            if (!file.isDirectory())
                continue;
            if ("ziptemp,music".indexOf(file.getName()) != -1)
                continue;
            //要插入的电视剧
            if (tv_name != null && tv_name != "")
                if (tv_name.indexOf(file.getName()) == -1) {
                    continue;
                }

            Db.tx(new IAtom() {
                public boolean run() throws SQLException {
                    int lang_type = 0;
                    if ("异形,阿甘正传".indexOf(file.getName()) != -1)
                        lang_type = 1;
                    //demo
                    VideoRefresh.refresh(file.getPath(), lang_type);
                    return true;
                }
            });
        }

        long count_2 = Db.queryLong("select count(*) from clips");

        System.out.println("执行之后共计:" + count_2 + "条数据");

        System.out.println("本次共计插入：" + (count_2 - count_1));


        render("ok");

    }

    public void musicDownload() throws Exception {
        NetEaseCloudMusic.main(null);
    }

    public void videoCut() throws Exception {
        VideoCutMain.main(null);
    }

    public void coverTake() throws Exception {
        MainExe.takeCover();
    }
}