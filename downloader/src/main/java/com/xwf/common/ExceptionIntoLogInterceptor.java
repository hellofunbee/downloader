package com.xwf.common;


import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.JFinal;
import com.jfinal.log.Log;


public class ExceptionIntoLogInterceptor implements Interceptor {

    private static final Log log = Log.getLog(ExceptionIntoLogInterceptor.class);

    public void intercept(Invocation invocation) {
        try {
            invocation.invoke(); //一定要注意，把处理放在invoke之后，因为放在之前的话，是会空指针
        } catch (Exception e) {
            logWrite(invocation, e);
        } finally {
            try {

            } catch (Exception ee) {

            }
        }


    }


    private void logWrite(Invocation inv, Exception e) {
        //开发模式
        if (JFinal.me().getConstants().getDevMode()) {
            e.printStackTrace();
        }
        StringBuilder sb = new StringBuilder("\n---Exception Log Begin---\n");
        sb.append("Controller:").append(inv.getController().getClass().getName()).append("\n");
        sb.append("Method:").append(inv.getMethodName()).append("\n");
        sb.append("Exception Type:").append(e.getClass().getName()).append("\n");
        sb.append("Exception Details:");
        log.error(sb.toString(), e);

    }
}
