package com.liu.core;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

public class SingleListBox extends ElementBase {
    public SingleListBox(Page page, By by, String displayName) {
        super(page, by, displayName);
    }

    public SingleListBox(ElementBase parent, By by, String displayName) {
        super(parent, by, displayName);
    }

    public void selectByIndex(int index) {
        WebElement element = this.getRawObject();

        try {
            Select select = new Select(element);
            select.selectByIndex(index);
        } catch (WebDriverException var4) {
            this.reporter.failed(String.format("Select option at position '%d' in '%s'", index, this.displayName), String.format("Failed to select option at position '%d' (zero-based index) in '%s' on page '%s'. %s", index, this.displayName, this.page.getDisplayName(), var4.getMessage()), this.by.toString(), this.browserManager.getCurrentBrowser().getCurrentTab());
            throw var4;
        }

        this.reporter.info(String.format("Select option at position '%d' in '%s'", index, this.displayName), String.format("Selected option at position '%d' (zero-based index) in '%s' on page '%s.", index, this.displayName, this.page.getDisplayName()), this.by.toString(), this.browserManager.getCurrentBrowser().getCurrentTab());
    }

    public void selectByValue(String value) {
        WebElement element = this.getRawObject();

        try {
            Select select = new Select(element);
            select.selectByValue(value);
        } catch (WebDriverException var4) {
            this.reporter.failed(String.format("Select option with value '%s' in '%s'", value, this.displayName), String.format("Failed to select option with value '%s' in '%s' on page '%s'. %s", value, this.displayName, this.page.getDisplayName(), var4.getMessage()), this.by.toString(), this.browserManager.getCurrentBrowser().getCurrentTab());
            throw var4;
        }

        logger.info(String.format("Select option with value '%s' in '%s'", value, this.displayName), String.format("Selected option with value '%s' in '%s' on page '%s.", value, this.displayName, this.page.getDisplayName()), this.by.toString(), this.browserManager.getCurrentBrowser().getCurrentTab());
    }

    public void selectByVisibleText(String text) {
        WebElement element = this.getRawObject();

        try {
            Select select = new Select(element);
            select.selectByVisibleText(text);
        } catch (WebDriverException var4) {
            this.reporter.failed(String.format("Select option with text '%s' in '%s'", text, this.displayName), String.format("Failed to select option with text '%s' in '%s' on page '%s'. %s", text, this.displayName, this.page.getDisplayName(), var4.getMessage()), this.by.toString(), this.browserManager.getCurrentBrowser().getCurrentTab());
            throw var4;
        }

        this.reporter.info(String.format("Select option with text '%s' in '%s'", text, this.displayName), String.format("Selected option with text '%s' in '%s' on page '%s.", text, this.displayName, this.page.getDisplayName()), this.by.toString(), this.browserManager.getCurrentBrowser().getCurrentTab());
    }

    public int getSelectedIndex() {
        WebElement element = this.getRawObject();

        try {
            Select select = new Select(element);
            return select.getOptions().indexOf(select.getFirstSelectedOption());
        } catch (WebDriverException var3) {
            this.reporter.failed(String.format("Get selected option's index from '%s'", this.displayName), String.format("Failed to get selected option's index from '%s' on page '%s'. %s", this.displayName, this.page.getDisplayName(), var3.getMessage()), this.by.toString(), this.browserManager.getCurrentBrowser().getCurrentTab());
            throw var3;
        }
    }

    public String getSelectedValue() {
        WebElement element = this.getRawObject();

        try {
            Select select = new Select(element);
            return select.getFirstSelectedOption().getAttribute("value");
        } catch (WebDriverException var3) {
            this.reporter.failed(String.format("Get selected option's value from '%s'", this.displayName), String.format("Failed to get selected option's value from '%s' on page '%s'. %s", this.displayName, this.page.getDisplayName(), var3.getMessage()), this.by.toString(), this.browserManager.getCurrentBrowser().getCurrentTab());
            throw var3;
        }
    }

    public String getSelectedVisibleText() {
        WebElement element = this.getRawObject();

        try {
            Select select = new Select(element);
            return select.getFirstSelectedOption().getText();
        } catch (WebDriverException var3) {
            this.reporter.failed(String.format("Get selected option's text from '%s'", this.displayName), String.format("Failed to get selected option's text from '%s' on page '%s'. %s", this.displayName, this.page.getDisplayName(), var3.getMessage()), this.by.toString(), this.browserManager.getCurrentBrowser().getCurrentTab());
            throw var3;
        }
    }
}

