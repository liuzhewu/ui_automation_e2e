package com.liu.test;

import com.liu.core.BrowserManager;
import com.liu.core.BrowserType;
import com.liu.util.ConfigManager;
import com.liu.util.Constant;
import com.liu.util.TestDataManager;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.io.File;

/**
 * 测试的初始化工作
 */
@Feature("测试准备")
public class Before extends Base {

    @Parameters(value = {"module"})
    @Test
    @Description("测试初始化工作：打开浏览器")
    public void setUp(String module0) {
        //设置报告位置，必须有，否则报错
        System.setProperty("report_dir", "." + File.separator + "target" + File.separator + "critic-results");
        //true表示无头模式，linux服务器上必须是true
        System.setProperty("critic.headless", "True");
        System.setProperty("script_path", "");
        //针对无头模式才有效
        System.setProperty("critic.browser.size", Constant.WINDOW_WIDTH + "x" + Constant.WINDOW_HEIGHT);

        initEnv(module0);
        browserManager = BrowserManager.getInstance();
        BrowserType browserType = getBrowserType(getCommonProperty("BROWSER_TYPE"));
        b1 = browserManager.openNewBrowser(browserType, module);
        b1.setImplicitWaitTimeMs(Constant.WAIT_TIME * Constant.SECOND);
        b1.setPageLoadTimeoutMs(Constant.WAIT_TIME * Constant.SECOND);
        //访问网站登录页面
        to(systemUrl + common.getProperty("LOGIN_URL"));
        //将打开的窗口句柄加入窗口栈
        switchToNewWindow();
    }

    /**
     * 初始化环境变量，区分ali,wmmp的
     *
     * @param module0 模块名，用于日志文件、浏览器命名
     */
    private void initEnv(String module0) {
        //启动命令大概为mvn test -Dsurefire.suiteXmlFiles=src/test/resources/testng/cp/testng.xml -Denv=ali,不设置env就是默认ali
        String envParam = System.getProperty("env");
        if (StringUtils.isEmpty(envParam)) {
            envParam = getCommonProperty("DEFAULT_ENV");
        }
        ConfigManager.loadEnvConfig("src" + File.separator + "test" + File.separator + "resources" + File.separator + "env" + File.separator + envParam + ".properties");
        module = module0;
        systemUrl = getEnvProperty("SYSTEM_URL");
        commonHomeUrl = systemUrl + getCommonProperty("COMMON_HOME_URL");
        coreHomeUrl = systemUrl + getCommonProperty("CORE_HOME_URL");
        cpHomeUrl = systemUrl + getCommonProperty("CP_HOME_URL");
        TestDataManager.loadDataCsv(module);

    }


    private BrowserType getBrowserType(String type) {
        BrowserType browserType = BrowserType.CHROME;
        switch (type) {
            case "CHROME":
                browserType = BrowserType.CHROME;
                break;
            case "EDGE":
                browserType = BrowserType.EDGE;
                break;
            case "FIREFOX":
                browserType = BrowserType.FIREFOX;
                break;
            case "INTERNET_EXPLORER":
                browserType = BrowserType.INTERNET_EXPLORER;
                break;
            case "SAFARI":
                browserType = BrowserType.SAFARI;
                break;
        }
        return browserType;
    }


}