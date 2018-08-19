package com.xwf.common.controller;

import com.jfinal.core.Controller;
import com.xwf.common.http.NetEaseCloudMusic;
import com.xwf.common.utils.VideoCutMain;

/**
 * Created by weifengxu on 2018/8/9.
 */
public class TaskController extends Controller {
    //默认加载index方法
    public void index() {
        renderText("急急急");
    }


    public void refresh() {
//        VideoRefresh.refresh();
        render("ok");

    }

    public void musicDownload() throws Exception {
        NetEaseCloudMusic.main(null);
    }

    public void videocut() throws Exception {
        VideoCutMain.main(null);
    }
}