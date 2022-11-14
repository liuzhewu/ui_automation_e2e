package com.liu.test.core;

import com.liu.asserts.Assertion;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import org.testng.annotations.Test;


@Feature("主页")
public class HomePage extends CoreBase {

    @Test
    @Description("进入主页")
    public void join() {
        clickNeedReplace("常用定位", "p文本值定位", "监控中心");
        switchToNewWindow();
        Assertion.assertUrlContains(coreHomeUrl, "进入到mds-core首页");
    }

}
