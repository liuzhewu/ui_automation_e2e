package com.liu.test.e2e;

import com.liu.test.Base;
import com.liu.test.core.Dashboard;
import com.liu.test.core.HomePage;
import com.liu.test.cp.ProjectConfiguration;
import com.liu.pojo.TestData;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;


@Feature("展示多个模块的总页")
public class Demo extends Base {

    @Test(dataProvider = "demo")
    @Description("进入总页")
    public void demo(TestData testData) {
        toCommonHomePage();
        new HomePage().join();
        new Dashboard().view();
        toCommonHomePage();
        new com.liu.test.cp.HomePage().join();
        new ProjectConfiguration().demo(testData);

    }

    @DataProvider(name = "demo")
    public Object[] getDemoData() {
        return getTestData("demo", "demo");
    }

}
