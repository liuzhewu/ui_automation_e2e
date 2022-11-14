package com.liu.core;


import org.openqa.selenium.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

public class Tab implements IScreenshot {
    private final Browser browser;
    private final String tabHandle;
    private final WebDriver driver;
    private final Reporter reporter;

    public Tab(Browser browser, String tabHandle) {
        this(browser, tabHandle, Reporter.getInstance());
    }

    public Tab(Browser browser, String tabHandle, Reporter reporter) {
        this.browser = Objects.requireNonNull(browser, "'browser' must not be null");
        this.tabHandle = Objects.requireNonNull(tabHandle, "'tabHandle' must not be null");
        this.reporter = Objects.requireNonNull(reporter, "'reporter' must not be null");
        this.driver = this.browser.getDriver();
    }

    public String getTabHandle() {
        return this.tabHandle;
    }

    public WebDriver getDriver() {
        return this.driver;
    }

    public void switchToThisTab() {
        this.browser.switchToTab(this.tabHandle);
    }

    public void close() {
        this.browser.closeTab(this.tabHandle);
    }

    public WebElement findElement(By by) {
        this.switchToThisTab();
        return this.driver.findElement(by);
    }

    public void navigate(String url) {
        this.switchToThisTab();
        this.driver.get(url);
        this.reporter.info(String.format("Navigate to '%s'", url), String.format("Navigate browser '%s' tab '%s' to '%s'.", this.browser.getBrowserName(), this.tabHandle, url), null, this);
    }

    public String getCurrentUrl() {
        this.switchToThisTab();
        return this.driver.getCurrentUrl();
    }

    public String getLocale() {
        this.switchToThisTab();
        JavascriptExecutor js = (JavascriptExecutor) this.driver;
        return (String) js.executeScript("return window.navigator.userLanguage || window.navigator.language", new Object[0]);
    }

    public boolean takeScreenshot(Path path) {
        this.switchToThisTab();

        try {
            TakesScreenshot shot = (TakesScreenshot) this.driver;
            File file = shot.getScreenshotAs(OutputType.FILE);
            Files.copy(file.toPath(), path, StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException var4) {
            this.reporter.error(String.format("Error saving screenshot to '%s'", path), var4.toString(), null, null);
            return false;
        }
    }
}

