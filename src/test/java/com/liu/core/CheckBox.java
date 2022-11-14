package com.liu.core;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

public class CheckBox extends ElementBase {
    public CheckBox(Page page, By by, String displayName) {
        super(page, by, displayName);
    }

    public CheckBox(ElementBase parent, By by, String displayName) {
        super(parent, by, displayName);
    }

    public boolean isChecked() {
        WebElement element = this.getRawObject();
        return element.isSelected();
    }

    public void check() {
        WebElement element = this.getRawObject();
        boolean changed = false;

        try {
            if (!element.isSelected()) {
                element.click();
                changed = true;
            }
        } catch (WebDriverException var4) {
            this.reporter.failed(String.format("Check '%s'", this.displayName), String.format("Failed to check checkbox '%s' on page '%s'. %s", this.displayName, this.page.getDisplayName(), var4.getMessage()), this.by.toString(), this.browserManager.getCurrentBrowser().getCurrentTab());
            throw var4;
        }

        if (changed) {
            this.reporter.info(String.format("Check '%s'", this.displayName), String.format("Checked checkbox '%s' on page '%s'.", this.displayName, this.page.getDisplayName()), this.by.toString(), this.browserManager.getCurrentBrowser().getCurrentTab());
        } else {
            this.reporter.info(String.format("No action taken: checkbox '%s' already checked", this.displayName), String.format("Checkbox '%s' on page '%s' was already checked. No action taken.", this.displayName, this.page.getDisplayName()), this.by.toString(), this.browserManager.getCurrentBrowser().getCurrentTab());
        }

    }

    public void uncheck() {
        WebElement element = this.getRawObject();
        boolean changed = false;

        try {
            if (element.isSelected()) {
                element.click();
                changed = true;
            }
        } catch (WebDriverException var4) {
            this.reporter.failed(String.format("Uncheck '%s'", this.displayName), String.format("Failed to uncheck checkbox '%s' on page '%s'. %s", this.displayName, this.page.getDisplayName(), var4.getMessage()), this.by.toString(), this.browserManager.getCurrentBrowser().getCurrentTab());
            throw var4;
        }

        if (changed) {
            this.reporter.info(String.format("Uncheck '%s'", this.displayName), String.format("Unchecked checkbox '%s' on page '%s'.", this.displayName, this.page.getDisplayName()), this.by.toString(), this.browserManager.getCurrentBrowser().getCurrentTab());
        } else {
            this.reporter.info(String.format("No action taken: checkbox '%s' already unchecked", this.displayName), String.format("Checkbox '%s' on page '%s' was already unchecked. No action taken.", this.displayName, this.page.getDisplayName()), this.by.toString(), this.browserManager.getCurrentBrowser().getCurrentTab());
        }

    }

    public void toggle() {
        WebElement element = this.getRawObject();
        boolean checked = false;

        try {
            if (!element.isSelected()) {
                checked = true;
            }

            element.click();
        } catch (WebDriverException var4) {
            if (checked) {
                this.reporter.failed(String.format("Check (toggle on) '%s'", this.displayName), String.format("Failed to check (toggle on) checkbox '%s' on page '%s'. %s", this.displayName, this.page.getDisplayName(), var4.getMessage()), this.by.toString(), this.browserManager.getCurrentBrowser().getCurrentTab());
            } else {
                this.reporter.failed(String.format("Uncheck (toggle off) '%s'", this.displayName), String.format("Failed to uncheck (toggle off) checkbox '%s' on page '%s'. %s", this.displayName, this.page.getDisplayName(), var4.getMessage()), this.by.toString(), this.browserManager.getCurrentBrowser().getCurrentTab());
            }

            throw var4;
        }

        if (checked) {
            this.reporter.info(String.format("Check (toggle on) '%s'", this.displayName), String.format("Checked (toggle on) checkbox '%s' on page '%s'.", this.displayName, this.page.getDisplayName()), this.by.toString(), this.browserManager.getCurrentBrowser().getCurrentTab());
        } else {
            this.reporter.info(String.format("Uncheck (toggle off) '%s'", this.displayName), String.format("Unchecked (toggle off) checkbox '%s' on page '%s'.", this.displayName, this.page.getDisplayName()), this.by.toString(), this.browserManager.getCurrentBrowser().getCurrentTab());
        }

    }
}
