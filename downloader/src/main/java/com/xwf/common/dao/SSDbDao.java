package com.xwf.common.dao;

import org.junit.Test;
import org.nutz.ssdb4j.SSDBs;
import org.nutz.ssdb4j.spi.Response;
import org.nutz.ssdb4j.spi.SSDB;

/**
 * Created by weifengxu on 2018/8/30.
 */
public class SSDbDao {
    //ssdb = new SSDB("127.0.0.1", 8888);
    SSDB ssdb = null;
    Response resp;


    public SSDbDao() throws Exception {
        ssdb = SSDBs.simple();


    }

    @Test
    public void test() throws Exception {
        SSDbDao dao = new SSDbDao();
        SSDB ssdb = dao.ssdb;
        resp = ssdb.hincr("n","u",100);
        System.out.println(resp.asString());

        for (String s:resp.listString()) {
            System.out.println(s);
        }

    }

    @Test
    public void info() throws Exception {
        SSDbDao dao = new SSDbDao();
        SSDB ssdb = dao.ssdb;
        resp = ssdb.info();
        for (int i = 1; i < resp.listString().size(); i++) {
            String s = new String(resp.listString().get(i));
            System.out.println(s);
        }
    }


}
