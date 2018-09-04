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
                + "/musicDownload?order=&s=&e=:下载音乐\n"
                +"order{0:1倒叙：正序}\n"
                +"s{开始}\n"
                +"e{结束}\n"
                + "/videoCut?delay=:截取视频 前进ms\n"
                + "/coverTake:截取cover\n";

        renderText(index);
    }


    public void refresh() {

        String tv_name = getPara("tv_name");

        DbRefresh.insert(tv_name, true);
        render("ok");

    }

    public void musicDownload() throws Exception {
//        NetEaseCloudMusic.main(null);
        MusicDownload.main(new String[]{
                getPara("order")==null?"1":getPara("order"),
                getPara("s")==null?"0":getPara("s"),
                getPara("e")==null?null:getPara("e")
        });
    }

    public void videoCut() throws Exception {
        VideoCutMain.main(new String[]{
                getPara("delay"),
                getPara("thread"),
                getPara("tv_name"),
        });
    }

    public void coverTake() throws Exception {
        MainExe.takeCover();
    }


}