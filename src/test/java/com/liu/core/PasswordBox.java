package com.liu.core;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

public class PasswordBox extends TextBox {
    public PasswordBox(Page page, By by, String displayName) {
        super(page, by, displayName);
    }

    public PasswordBox(ElementBase parent, By by, String displayName) {
        super(parent, by, displayName);
    }

    public void setValue(String text) {
        this.setValue(text, 50);
    }

    public void setValue(String text, int delayMs) {
        WebElement element = this.getRawObject();

        try {
            this.rawClear(element);
            this.rawSendKeys(element, text, delayMs);
        } catch (WebDriverException var5) {
            this.reporter.failed(String.format("Set '%s' to '***' (masked)", this.displayName), String.format("Failed to set object '%s' on page '%s' to '***' (masked). %s", this.displayName, this.page.getDisplayName(), var5.getMessage()), this.by.toString(), this.browserManager.getCurrentBrowser().getCurrentTab());
            throw var5;
        }

        this.reporter.info(String.format("Set '%s' to '***' (masked)", this.displayName), String.format("Set object '%s' to '***' (masked) on page '%s'.", this.displayName, this.page.getDisplayName()), this.by.toString(), this.browserManager.getCurrentBrowser().getCurrentTab());
    }
}

