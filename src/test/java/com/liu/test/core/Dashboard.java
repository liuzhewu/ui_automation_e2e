package com.liu.test.core;

import com.liu.asserts.Assertion;
import com.liu.pojo.TestData;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@Feature("Dashboard")
public class Dashboard extends CoreBase {

    @Test
    @Description("查看Dashboard")
    public void view() {
        //暂时没想到执行什么用例
        toCoreHomePage();
        Assertion.assertExist(findElementNeedReplace("常用定位", "span文本值定位", "云主机"));

    }

    @Test(dataProvider = "demo")
    @Description("demo,用于展示csv中的数据怎么使用,使用dataProvider注解")
    public void demo(TestData testData) {
        logger.info(testData.toString());
    }


    @DataProvider(name = "demo")
    public Object[] getDemoData() {
        return getTestData("Dashboard", "view");
    }

}
