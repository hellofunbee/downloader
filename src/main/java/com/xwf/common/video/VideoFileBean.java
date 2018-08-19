package com.xwf.common.video;

/**
 * Created by weifengxu on 2018/4/13.
 */


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 文件夹bean
 */
public  class VideoFileBean {
    File fold = null;
    List<File> files = new ArrayList<File>();
    List<File> folds = new ArrayList<File>();

    List<Map> maps = new ArrayList<Map>();

    @Override
    public String toString() {
        return "VideoFileBean{" +
                "fold=" + fold +
                ", files=" + files +
                ", folds=" + folds +
                '}';
    }

}
