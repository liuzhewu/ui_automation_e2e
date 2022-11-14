package com.liu.util;

import io.qameta.allure.Allure;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Tool {

    public static String getCurrentTimestamp() {
        return "" + new Date().getTime();
    }

    public static String getCurrentTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss SSS");
        return simpleDateFormat.format(new Date());
    }

    /**
     * 根据截图的时间戳计算出截图当前时间
     *
     * @param timestamp 时间戳
     */
    public static void printTime(long timestamp) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
        System.out.println(simpleDateFormat.format(new Date(timestamp)));
    }

    public static void step(String msg) {
        Allure.step(getCurrentTime() + msg);
    }

    public static void main(String[] args) {
        System.out.println(getCurrentTime());
        printTime(333);
    }

}
