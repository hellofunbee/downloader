package com.xwf.common.controller;

import com.jfinal.core.Controller;
import com.xwf.common.dao.DbRefresh;
import com.xwf.common.http.MusicDownload;
import com.xwf.common.utils.MainExe;
import com.xwf.common.utils.VideoCutMain;

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

        DbRefresh.insert(tv_name, true);
        render("ok");

    }

    public void musicDownload() throws Exception {
//        NetEaseCloudMusic.main(null);
        MusicDownload.main(null);
    }

    public void videoCut() throws Exception {
        VideoCutMain.main(new String[]{getPara("delay")});
    }

    public void coverTake() throws Exception {
        MainExe.takeCover();
    }


}