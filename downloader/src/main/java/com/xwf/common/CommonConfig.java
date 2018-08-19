package com.xwf.common;

import com.jfinal.config.*;
import com.jfinal.render.ViewType;
import com.jfinal.template.Engine;
import com.xwf.common.controller.HomeController;
import com.xwf.common.controller.MusicController;
import com.xwf.common.controller.TaskController;

/**
 * Created by weifengxu on 17/7/8.
 */
public class CommonConfig extends JFinalConfig {
    public void configConstant(Constants constants) {
        constants.setDevMode(true);
        loadPropertyFile("config.property");
        constants.setViewType(ViewType.FREE_MARKER);

    }


    @Override
    public void configRoute(Routes routes) {
        routes.add("/", HomeController.class);
        routes.add("task", TaskController.class, "/");
        routes.add("music", MusicController.class, "/");

    }

    public void configEngine(Engine engine) {

    }

    /**
     * 配置数据库连接参数以及控制台打印sql输入配置
     *
     * @param arg0
     */
    @Override
    public void configPlugin(Plugins arg0) {

//        C3p0Plugin c3p0 = new C3p0Plugin(getProperty("jdbcUrl"), getProperty("user"), getProperty("password").trim(), getProperty("jdbcDriver"));
//        arg0.add(c3p0);
//
//        ActiveRecordPlugin activeRecord = new ActiveRecordPlugin(c3p0);
//        activeRecord.addMapping("user", User.class).setShowSql(Boolean.parseBoolean(getProperty("showSql")));
//        arg0.add(activeRecord);
    }

    @Override
    public void configInterceptor(Interceptors interceptors) {
        interceptors.add(new ExceptionIntoLogInterceptor());
    }


    @Override
    public void configHandler(Handlers handlers) {

    }
}
