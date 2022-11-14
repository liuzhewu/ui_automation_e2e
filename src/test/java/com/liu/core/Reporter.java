package com.liu.core;

/**
 * 主要是为了适配，不需要打印内容
 */
public class Reporter {
    private static Reporter instance = null;

    private Reporter() {
    }

    public static Reporter getInstance() {
        if (instance == null) {
            instance = new Reporter();
        }

        return instance;
    }

    public void error(Object... objs) {

    }

    public void warning(Object... objs) {

    }

    public void info(Object... objs) {

    }

    public void failed(Object... objs) {

    }

    public void passed(Object... objs) {

    }


}
