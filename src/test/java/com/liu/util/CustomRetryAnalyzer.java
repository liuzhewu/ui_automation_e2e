package com.liu.util;

import com.liu.test.After;
import com.liu.test.Base;
import com.liu.core.Environment;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class CustomRetryAnalyzer implements IRetryAnalyzer {

    private static final Logger logger = LoggerFactory.getLogger(CustomRetryAnalyzer.class);
    private int retryCount = 1;

    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < Constant.MAX_RETRY_COUNT) {
            logger.warn("第" + retryCount + "次失败,待重试，失败类信息:" + result.getInstance().toString(), result.getThrowable());
            retryCount++;
            screenshotOnFailure((Base) result.getInstance());
            return true;
        }
        return false;
    }


    /**
     * 第一次失败截图，不显示在allure报告中
     *
     * @param base 测试类
     */
    private void screenshotOnFailure(Base base) {
        if (base instanceof After) {
            //after类已经关闭driver,不可截图
            return;
        }
        File file = ((TakesScreenshot) Base.getDriver()).getScreenshotAs(OutputType.FILE);
        String lastKeyword = Base.lastKeyword;
        if (lastKeyword.contains("/")) {
            logger.info("contains /,lastPage:" + Base.lastPage + "lastKeyword:" + lastKeyword);
            lastKeyword = lastKeyword.replace("/", "-");
        }
        String imageName = Base.lastPage + "-" + lastKeyword + Tool.getCurrentTimestamp() + ".png";
        try {
            Path path = Paths.get(Environment.getInstance().getReportDir().toString(), imageName);
            Files.copy(file.toPath(), path, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
