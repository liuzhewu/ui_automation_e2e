package com.liu.pojo;

/**
 * 映射页面的控件，对应ui.xml，keyWord是元素描述关键字，by定位方式，value定位值
 */
public class UiElement {
    //元素在excel的定位信息
    private final String keyWord;
    private final String by;
    private final String value;

    public UiElement(String keyWord, String by, String value) {
        this.keyWord = keyWord;
        this.by = by;
        this.value = value;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public String getBy() {
        return by;
    }

    public String getValue() {
        return value;
    }


    @Override
    public String toString() {
        return "UiElement{" +
                "keyWord='" + keyWord + '\'' +
                ", by='" + by + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
