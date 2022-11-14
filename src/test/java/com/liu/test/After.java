package com.liu.test;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import org.testng.annotations.Test;

/**
 * 测试执行的收尾工作，比如：关闭浏览器
 */
@Feature("测试结束")
public class After extends Base {
    @Test
    @Description("测试收尾工作：关闭浏览器")
    public void tearDown() {
        // At the end of the test run, you should clean up the open browsers just in case afterEach did not do so.
        if (browserManager != null) {
            browserManager.closeAllBrowsers();
        }
    }
}
