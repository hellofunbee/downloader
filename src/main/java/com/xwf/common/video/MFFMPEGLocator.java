package com.xwf.common.video;

import com.xwf.common.utils.CommonUtils;
import it.sauronsoftware.jave.FFMPEGLocator;

/**
 * Created by weifengxu on 2018/4/14.
 */
public class MFFMPEGLocator extends FFMPEGLocator {
    protected String getFFMPEGExecutablePath() {
        return CommonUtils.getPathByKey("ffmpegPath");
    }
}
