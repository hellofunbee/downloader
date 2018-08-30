package com.xwf.common.utils;

import java.io.InputStream;

/**
 * Created by weifengxu on 2018/8/5.
 */
class PrintStream extends Thread {
    InputStream _is = null;
    boolean _is_print = false;

    public PrintStream(InputStream is, boolean is_print) {
        _is = is;
        _is_print = is_print;
    }

    @Override
    public void run() {
        try {
            while (this != null) {
                int _ch = _is.read();
                if (_ch != -1) {
                    if (_is_print)
                        System.out.print((char) _ch);
                } else
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
