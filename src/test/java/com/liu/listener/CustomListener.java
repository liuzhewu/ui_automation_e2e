package com.liu.listener;


import com.liu.test.After;
import com.liu.test.Base;
import com.liu.util.Tool;
import com.liu.core.Browser;
import com.liu.core.Environment;
import com.liu.test.Before;
import io.qameta.allure.Allure;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Stack;

/**
 * 自定义监听类，可方便管理测试用例执行前、执行后
 */
public class CustomListener extends TestListenerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(CustomListener.class);


    @Override
    public void onTestFailure(ITestResult result) {
        try {
            Base base = (Base) result.getInstance();
            //截图需要driver，Before存在未初始化driver的情况导致报错；After关闭driver，报错
            if (!(base instanceof Before || base instanceof After)) {
                screenshotOnFailure();
            }
            closeOldWindow();
        } catch (Throwable e) {
            logger.warn("CustomListener监听截图失败", e);
        }
        super.onTestFailure(result);

    }

    @Override
    public void onTestSuccess(ITestResult result) {
        super.onTestSuccess(result);
    }

    /**
     * 截图之后，执行关闭新窗口逻辑
     */
    private void closeOldWindow() {
        //除了浏览器启动的时候打开的第一个窗口以外的窗口
        int newWindowSize = Base.windows.size() - 1;
        Browser b1 = Base.b1;
        Stack<String> windows = Base.windows;
        for (int i = 0; i < newWindowSize; i++) {
            //只需要关闭新窗口就ok
            Tool.step(" 关闭窗口" + windows.size() + ",窗口信息:" + windows.peek());
            b1.closeTab(windows.pop());
        }
        if (newWindowSize > 0) {
            Tool.step(" 切换到旧窗口" + windows.size() + ",窗口信息:" + windows.peek());
            b1.switchToTab(windows.peek(), false);
        }
    }


    /**
     * 失败截图并显示在allure报告中
     */
    private void screenshotOnFailure() {
        File file = ((TakesScreenshot) Base.getDriver()).getScreenshotAs(OutputType.FILE);
        String page = Base.lastPage;
        String lastKeyword = Base.lastKeyword;
        String lastReplaceValue = Base.lastReplaceValue;
        if (lastKeyword.contains("/")) {
            logger.info("contains /,lastPage:" + page + "lastKeyword:" + lastKeyword + ",lastReplaceValue:" + lastReplaceValue);
            lastKeyword = lastKeyword.replace("/", "-");
        }
        String imageName = page + "-" + lastKeyword;

        if (StringUtils.isNotEmpty(lastReplaceValue)) {
            imageName = imageName + "-" + lastReplaceValue;
        }
        imageName = imageName + "_" + Tool.getCurrentTimestamp() + ".png";
        try {
            //截图放入allure报告中
            Allure.addAttachment(imageName, new FileInputStream(file));
            //拷贝图片到Before中report_dir设置的值的目录下，方便本地无allure的情况下查看错误截图
            Path path = Paths.get(Environment.getInstance().getReportDir().toString(), imageName);
            Files.copy(file.toPath(), path, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
