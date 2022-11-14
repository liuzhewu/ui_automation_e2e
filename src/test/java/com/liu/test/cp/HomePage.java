package com.liu.test.cp;

import com.liu.asserts.Assertion;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import org.testng.annotations.Test;


@Feature("主页")
public class HomePage extends CpBase {

    @Test
    @Description("进入主页")
    public void join() {
        clickNeedReplace("常用定位", "p文本值定位", "配置中心");
        switchToNewWindow();
        Assertion.assertUrlContains(cpHomeUrl, "进入到配置平台首页");
    }

}
