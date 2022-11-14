package com.liu.enums;

/**
 * 控件类型
 */
public enum ControlType {
    //基本类型，非文本、按钮、单选框
    BASE("按钮"),
    CHECKBOX("选择框"),
    SINGLELIST("下拉框"),
    TEXTBOX("文本框"),
    PASSWORD("密码框");

    private final String desc;

    ControlType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
