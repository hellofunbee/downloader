package com.xwf.common.controller;


import com.jfinal.core.Controller;
import com.xwf.common.utils.CommonUtils;
import com.xwf.common.utils.ZipCompressor;

import java.io.File;
import java.util.UUID;

/**
 * Created by weifengxu on 17/7/8.
 */
public class CommonController extends Controller {

    public void index() throws Exception {
        render("sercher/searchMain.html");
//        render("downloader/pic.html");
    }
    public void download_zip() {
        String para = getPara("fileUrls");
        if (para == null)
            render("空空如也！");
        else {
            String zip_path = CommonUtils.getPathByKey("zip_path") + UUID.randomUUID() + ".zip";
            String p = CommonUtils.tolocalUrl(para);

            File[] fs;
            if (p.indexOf(",") == -1) {
                fs = new File[]{new File(p)};
            } else {
                String[] ps = p.split(",");
                fs = new File[ps.length];
                for (int i = 0; i < ps.length; i++) {
                    fs[i] = new File(ps[i]);
                }

            }
            ZipCompressor.zipFiles(fs, new File(zip_path));
            renderFile(new File(zip_path));

        }
    }
}
