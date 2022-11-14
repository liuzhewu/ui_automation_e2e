package com.liu.pojo;

import java.util.Map;

/**
 * 测试数据，对应excel中的一行数据
 */
public class TestData {
    private final String menu;
    private final String operate;
    private final String title;
    private final Map<String, String> data;


    public TestData(String menu, String operate, String title, Map<String, String> data) {
        this.menu = menu;
        this.operate = operate;
        this.title = title;
        this.data = data;
    }

    public String getMenu() {
        return menu;
    }

    public String getOperate() {
        return operate;
    }

    public String getTitle() {
        return title;
    }

    public String getDataValueByKey(String key) {
        String value = data.get(key);
        if (null == value) {
            value = "";
        }
        return value;
    }

    public Map<String, String> getData() {
        return data;
    }

    @Override
    public String toString() {
        return "TestData{" +
                "title='" + title + '\'' +
                "menu='" + menu + '\'' +
                ", operate='" + operate + '\'' +
                ", data=" + data + '\'' +
                '}';
    }
}
