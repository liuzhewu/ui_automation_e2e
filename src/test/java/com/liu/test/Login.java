package com.liu.test;

import com.liu.asserts.Assertion;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import org.testng.annotations.Test;


/**
 * 登录操作
 */
@Feature("登录")
public class Login extends Base {

    /**
     * 登录操作
     */
    @Description("登录成功")
    @Test
    public void login() {
        click("公有页面_登录页面", "Standard登录方式");
        sendKey("公有页面_登录页面", "用户名", env.getProperty("USERNAME"));
        sendKeyPasswd("公有页面_登录页面", "密码", env.getProperty("PASSWD"));
        clickNeedReplace("常用定位", "span文本值定位元素父级", "登录");
        Assertion.assertUrlContains(commonHomeUrl, "登录成功，进入首页");
    }


}
