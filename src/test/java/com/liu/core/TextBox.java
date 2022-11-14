package com.liu.core;


import org.apache.commons.lang3.SystemUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

public class TextBox extends ElementBase {
    public TextBox(Page page, By by, String displayName) {
        super(page, by, displayName);
    }

    public TextBox(ElementBase parent, By by, String displayName) {
        super(parent, by, displayName);
    }

    protected void rawClear(WebElement element) {
        if (!SystemUtils.IS_OS_LINUX && !SystemUtils.IS_OS_WINDOWS) {
            if (!SystemUtils.IS_OS_MAC) {
                throw new AssertionError("Unsupported OS: " + System.getProperty("os.name"));
            }

            element.sendKeys(Keys.COMMAND, "a");
            element.sendKeys(Keys.BACK_SPACE);
        } else {
            element.sendKeys(Keys.CONTROL, "a");
            element.sendKeys(Keys.BACK_SPACE);
        }

    }

    public void clearValue() {
        WebElement element = this.getRawObject();

        try {
            this.rawClear(element);
        } catch (WebDriverException var3) {
            this.reporter.failed(String.format("Clear '%s'", this.displayName), String.format("Failed to clear object '%s' on page '%s'. %s", this.displayName, this.page.getDisplayName(), var3.getMessage()), this.by.toString(), this.browserManager.getCurrentBrowser().getCurrentTab());
            throw var3;
        }

        this.reporter.info(String.format("Clear '%s'", this.displayName), String.format("Cleared object '%s' on page '%s'.", this.displayName, this.page.getDisplayName()), this.by.toString(), this.browserManager.getCurrentBrowser().getCurrentTab());
    }

    public void setValue(String text, int delayMs) {
        WebElement element = this.getRawObject();

        try {
            this.rawClear(element);
            this.rawSendKeys(element, text, delayMs);
        } catch (WebDriverException var5) {
            this.reporter.failed(String.format("Set '%s' to '%s'", this.displayName, text), String.format("Failed to set object '%s' on page '%s' to '%s'. %s", this.displayName, this.page.getDisplayName(), text, var5.getMessage()), this.by.toString(), this.browserManager.getCurrentBrowser().getCurrentTab());
            throw var5;
        }

        this.reporter.info(String.format("Set '%s' to '%s'", this.displayName, text), String.format("Set object '%s' to '%s' on page '%s'.", this.displayName, text, this.page.getDisplayName()), this.by.toString(), this.browserManager.getCurrentBrowser().getCurrentTab());
    }

    public String getValue() {
        return this.getAttribute("value");
    }

    public void setValue(String text) {
        this.setValue(text, 50);
    }
}

