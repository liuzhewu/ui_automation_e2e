package com.liu.test.cp;

import com.liu.asserts.Assertion;
import com.liu.pojo.TestData;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;


@Feature("项目配置")
public class ProjectConfiguration extends CpBase {

    @Test
    @Description("查看MDS项目")
    public void viewMdsProject() {
        //该方法这里一般是配置testrail链接，如Allure.tms("自定义创建直连设备模型并发布成功");
        //由于目前没有对应的测试用例，方法内容如下：
        // 1.进入cp模块的首页2.点击项目配置3.点击MDS项目选项卡4.点击第一个查看
        //5.断言环境列表元素存在
        toCpHomePage();
        clickNeedReplace("常用定位", "span文本值定位", "项目配置");
        clickNeedReplace("常用定位", "div文本值定位", "MDS项目");
        clickNeedReplace("常用定位", "a文本值定位", "查看");
        Assertion.assertExist(findElementNeedReplace("常用定位", "span文本值定位", "环境列表"));
    }

    @Test(dataProvider = "demo")
    @Description("demo,用于展示csv中的数据怎么使用,使用dataProvider注解")
    public void demo(TestData testData) {
        logger.info(testData.toString());
    }


    @DataProvider(name = "demo")
    public Object[] getDemoData() {
        return getTestData("项目配置", "view");
    }

}
