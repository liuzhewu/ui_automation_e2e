package com.liu.util;

/**
 * 存放常量，包括用户不需要改变且不需要了解的
 */
public class Constant {

    public static final int SECOND = 1000;

    /**
     * 等待时间
     **/
    public static final long SHORT_WAIT_TIME = 5L;
    public static final long WAIT_TIME = 10L;
    //针对部分需要时间长的，提高等待时间
    public static final long LONG_WAIT_TIME = 60L;

    //休眠时间
    public static final long SLEEP_TIME = 2L;

    //ui.xml中的替换字符
    public static final String REPLACE_STR = "%PARAM0";

    //针对linux无头模式下设置的窗口大小信息，不设置的话，默认值太小，影响执行结果
    public static final String WINDOW_WIDTH = "1920";
    public static final String WINDOW_HEIGHT = "1080";

    //重试次数，部分情况下第二次执行结果受前一次影响也失败，且导致不好定位问题，比如，cms物模型的修改，固设置为1
    public static final int MAX_RETRY_COUNT = 1;

    //    core独有begin

    //    core独有end


    //    cp独有begin

    //    cp独有end


}
