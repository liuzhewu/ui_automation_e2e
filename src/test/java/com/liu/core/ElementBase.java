package com.liu.core;


import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.regex.Pattern;

public class ElementBase {
    protected static final Logger logger = LoggerFactory.getLogger(ElementBase.class);
    protected final String displayName;
    protected final ElementBase parent;
    protected final Page page;
    protected final String typeName;
    protected final BrowserManager browserManager;
    protected final Reporter reporter;
    protected By by;

    public ElementBase(Page page, By by, String displayName) {
        this.parent = null;
        this.page = Objects.requireNonNull(page, "'page' must not be null");
        this.by = Objects.requireNonNull(by, "'by' must not be null");
        this.displayName = Objects.requireNonNull(displayName, "'displayName' must not be null ");
        if (displayName.isBlank()) {
            throw new RuntimeException("'displayName' must not be an empty or blank string");
        } else {
            this.typeName = this.getClass().getSimpleName();
            this.browserManager = BrowserManager.getInstance();
            this.reporter = Reporter.getInstance();
        }
    }

    public ElementBase(ElementBase parent, By by, String displayName) {
        this.parent = parent;
        this.page = parent.getPage();
        this.by = Objects.requireNonNull(by, "'by' must not be null");
        this.displayName = Objects.requireNonNull(displayName, "'displayName' must not be null ");
        if (displayName.isBlank()) {
            throw new RuntimeException("'displayName' must not be an empty or blank string");
        } else {
            this.typeName = String.format("%s_%s", parent.typeName, this.getClass().getSimpleName());
            this.browserManager = BrowserManager.getInstance();
            this.reporter = Reporter.getInstance();
        }
    }

    public String getTypeName() {
        return this.typeName;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public Page getPage() {
        return this.page;
    }

    public By getBy() {
        return this.by;
    }

    public WebElement getRawObject() {
        try {
            if (this.parent == null) {
                return this.browserManager.getCurrentBrowser().getCurrentTab().findElement(this.by);
            } else {
                logger.debug("Calling parent finder...");
                WebElement rawParent = this.parent.getRawObject();
                logger.debug(String.format("Parent=%s (%s)", this.parent.getTypeName(), this.displayName));
                return rawParent.findElement(this.by);
            }
        } catch (WebDriverException var2) {
            this.reporter.failed(String.format("%s not found", this.displayName), String.format("Object '%s' on page '%s' could not be found.", this.displayName, this.page.getDisplayName()), this.by.toString(), this.browserManager.getCurrentBrowser().getCurrentTab());
            throw var2;
        }
    }

    public WebElement getRawObjectOrNull() {
        try {
            if (this.parent == null) {
                return this.browserManager.getCurrentBrowser().getCurrentTab().findElement(this.by);
            } else {
                logger.debug("Calling parent finder...");
                WebElement rawParent = this.parent.getRawObject();
                logger.debug(String.format("Parent=%s (%s)", this.parent.getTypeName(), this.displayName));
                return rawParent.findElement(this.by);
            }
        } catch (WebDriverException var2) {
            return null;
        }
    }

    public boolean exists() {
        return this.getRawObjectOrNull() != null;
    }

    public boolean exists(long waitMs) {
        Browser browser = this.browserManager.getCurrentBrowser();
        long implicitWaitTimeMs = browser.getImplicitWaitTimeMs();

        boolean var6;
        try {
            browser.setImplicitWaitTimeMs(waitMs);
            var6 = this.getRawObjectOrNull() != null;
        } finally {
            browser.setImplicitWaitTimeMs(implicitWaitTimeMs);
        }

        return var6;
    }

    public void assertExists(boolean expectedResult, boolean quitOnFail, boolean reportOnPass, boolean screenshotOnPass) {
        this.assertExists(expectedResult, quitOnFail, reportOnPass, screenshotOnPass, this.browserManager.getCurrentBrowser().getImplicitWaitTimeMs());
    }

    public void assertExists(boolean expectedResult, boolean quitOnFail, boolean reportOnPass, boolean screenshotOnPass, long waitMs) {
        boolean objExists = this.exists(waitMs);
        boolean assertPassed = objExists == expectedResult;
        if (!assertPassed || reportOnPass) {
            String expectedStatus = expectedResult ? "exists" : "does NOT exist";
            String actualStatus = objExists ? "exists" : "does NOT exist";
            String stepName = String.format("Assert '%s' %s", this.displayName, expectedStatus);
            String stepDetail;
            if (assertPassed) {
                stepDetail = String.format("'%s' on page '%s' %s (expected).", this.displayName, this.page.getDisplayName(), actualStatus);
                this.reporter.passed(stepName, stepDetail, this.by.toString(), screenshotOnPass ? this.browserManager.getCurrentBrowser().getCurrentTab() : null);
            } else {
                stepDetail = String.format("'%s' on page '%s' %s (unexpected).", this.displayName, this.page.getDisplayName(), actualStatus);
                this.reporter.failed(stepName, stepDetail, this.by.toString(), this.browserManager.getCurrentBrowser().getCurrentTab());
            }

            if (!assertPassed && quitOnFail) {
                throw new ExitScriptException("Exit triggered by quitOnFail = true in assertExists");
            }
        }
    }

    protected void scrollShim(WebElement element) {
        int x = element.getLocation().x;
        int y = element.getLocation().y;
        String scrollByCoord = String.format("window.scrollTo(%d,%d);", x, y);
        String scrollNavOutOfWay = "window.scrollBy(0, -120);";
        WebDriver driver = this.browserManager.getCurrentBrowser().getDriver();
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript(scrollByCoord);
        js.executeScript(scrollNavOutOfWay);
    }

    protected void rawHover(WebElement element, boolean scrollToElement) {
        if (scrollToElement && this.browserManager.getCurrentBrowser().getBrowserType() == BrowserType.FIREFOX) {
            this.scrollShim(element);
        }

        Actions actions = new Actions(this.browserManager.getCurrentBrowser().getDriver());
        actions.moveToElement(element).perform();
    }

    public void hover() {
        this.hover(1000);
    }

    public void hover(int delayMs) {
        WebElement element = this.getRawObject();

        try {
            if (this.browserManager.getCurrentBrowser().getBrowserType() == BrowserType.FIREFOX) {
                this.scrollShim(element);
            }

            Actions actions = new Actions(this.browserManager.getCurrentBrowser().getDriver());
            actions.moveToElement(element).perform();

            try {
                Thread.sleep(delayMs);
            } catch (InterruptedException var5) {
                throw new WebDriverException("Thread interrupted during sleep");
            }
        } catch (WebDriverException var6) {
            this.reporter.failed(String.format("Hover over '%s'", this.displayName), String.format("Failed hover over '%s' for %ds on page '%s'. %s", this.displayName, delayMs, this.page.getDisplayName(), var6.getMessage()), this.by.toString(), this.browserManager.getCurrentBrowser().getCurrentTab());
            throw var6;
        }

        this.reporter.passed(String.format("Hover over '%s'", this.displayName), String.format("Hovered over '%s' for %ds on page '%s'.", this.displayName, delayMs, this.page.getDisplayName()), this.by.toString(), this.browserManager.getCurrentBrowser().getCurrentTab());
    }

    public void leftClick() {
        WebElement element = this.getRawObject();

        try {
            element.click();
        } catch (WebDriverException var3) {
            this.reporter.failed(String.format("Click '%s'", this.displayName), String.format("Failed to click on '%s' on page '%s'. %s", this.displayName, this.page.getDisplayName(), var3.getMessage()), this.by.toString(), this.browserManager.getCurrentBrowser().getCurrentTab());
            throw var3;
        }

        this.reporter.info(String.format("Click '%s'", this.displayName), String.format("Clicked on '%s' on page '%s'.", this.displayName, this.page.getDisplayName()), this.by.toString(), this.browserManager.getCurrentBrowser().getCurrentTab());
    }

    public void rightClick() {
        WebElement element = this.getRawObject();

        try {
            this.rawHover(element, true);
            Actions actions = new Actions(this.browserManager.getCurrentBrowser().getDriver());
            actions.contextClick(element).perform();
        } catch (WebDriverException var3) {
            this.reporter.failed(String.format("Right-click '%s'", this.displayName), String.format("Failed to right-click on '%s' on page '%s'. %s", this.displayName, this.page.getDisplayName(), var3.getMessage()), this.by.toString(), this.browserManager.getCurrentBrowser().getCurrentTab());
            throw var3;
        }

        this.reporter.info(String.format("Right-click '%s'", this.displayName), String.format("Right-clicked on '%s' on page '%s'.", this.displayName, this.page.getDisplayName()), this.by.toString(), this.browserManager.getCurrentBrowser().getCurrentTab());
    }

    public void click() {
        this.leftClick();
    }

    public String getAttribute(String name) {
        WebElement element = this.getRawObject();

        try {
            return element.getAttribute(name);
        } catch (WebDriverException var4) {
            this.reporter.failed(String.format("Get attribute '%s' from '%s'", name, this.displayName), String.format("Failed to get attribute '%s' from '%s' on page '%s'. %s", name, this.displayName, this.page.getDisplayName(), var4.getMessage()), this.by.toString(), this.browserManager.getCurrentBrowser().getCurrentTab());
            throw var4;
        }
    }

    public boolean hasClass(String className) {
        String classes = this.getAttribute("class");
        return (" " + classes + " ").contains(" " + className + " ");
    }

    protected void rawMoveByOffset(int xOffset, int yOffset) {
        Actions actions = new Actions(this.browserManager.getCurrentBrowser().getDriver());
        actions.moveByOffset(xOffset, yOffset);
    }

    protected void rawClickAtOffsetFromElement(WebElement element, int xOffset, int yOffset) {
        Actions actions = new Actions(this.browserManager.getCurrentBrowser().getDriver());
        actions.moveToElement(element, xOffset, yOffset).click().perform();
    }

    public boolean isDisplayed() {
        WebElement element = this.getRawObject();

        try {
            return element.isDisplayed();
        } catch (WebDriverException var3) {
            this.reporter.failed(String.format("Get 'isDisplayed' status from '%s'", this.displayName), String.format("Failed to get 'isDisplayed' status from '%s' on page '%s'. %s", this.displayName, this.page.getDisplayName(), var3.getMessage()), this.by.toString(), this.browserManager.getCurrentBrowser().getCurrentTab());
            throw var3;
        }
    }

    public boolean isEnabled() {
        WebElement element = this.getRawObject();

        try {
            return element.isEnabled();
        } catch (WebDriverException var3) {
            this.reporter.failed(String.format("Get 'isEnabled' status from '%s'", this.displayName), String.format("Failed to get 'isEnabled' status from '%s' on page '%s'. %s", this.displayName, this.page.getDisplayName(), var3.getMessage()), this.by.toString(), this.browserManager.getCurrentBrowser().getCurrentTab());
            throw var3;
        }
    }

    public boolean isSelected() {
        WebElement element = this.getRawObject();

        try {
            return element.isSelected();
        } catch (WebDriverException var3) {
            this.reporter.failed(String.format("Get 'isSelected' status from '%s'", this.displayName), String.format("Failed to get 'isSelected' status from '%s' on page '%s'. %s", this.displayName, this.page.getDisplayName(), var3.getMessage()), this.by.toString(), this.browserManager.getCurrentBrowser().getCurrentTab());
            throw var3;
        }
    }

    public Point getLocation() {
        WebElement element = this.getRawObject();

        try {
            return element.getLocation();
        } catch (WebDriverException var3) {
            this.reporter.failed(String.format("Get 'location' property from '%s'", this.displayName), String.format("Failed to get 'location' property from '%s' on page '%s'. %s", this.displayName, this.page.getDisplayName(), var3.getMessage()), this.by.toString(), this.browserManager.getCurrentBrowser().getCurrentTab());
            throw var3;
        }
    }

    protected void rawRightClick(WebElement element) {
        Actions actions = new Actions(this.browserManager.getCurrentBrowser().getDriver());
        actions.contextClick(element).perform();
    }

    public void sendKeys(String text) {
        this.sendKeys(text, false, 50);
    }

    public void sendKeys(String text, boolean maskText) {
        this.sendKeys(text, maskText, 50);
    }

    public void sendKeys(String text, boolean maskText, int delayMs) {
        WebElement element = this.getRawObject();

        try {
            if (delayMs == 0) {
                element.sendKeys(text);
            } else {
                for (int i = 0; i < text.length(); ++i) {
                    element.sendKeys(String.valueOf(text.charAt(i)));

                    try {
                        Thread.sleep(delayMs);
                    } catch (InterruptedException var7) {
                        throw new WebDriverException("Thread interrupted during sleep");
                    }
                }
            }
        } catch (WebDriverException var8) {
            this.reporter.failed(String.format("Send keys '%s' to '%s'", text, this.displayName), String.format("Failed to send keys '%s' to object '%s' on page '%s'. %s", text, this.displayName, this.page.getDisplayName(), var8.getMessage()), this.by.toString(), this.browserManager.getCurrentBrowser().getCurrentTab());
            throw var8;
        }

        this.reporter.info(String.format("Send keys '%s' to '%s'", text, this.displayName), String.format("Sent keys '%s' to object '%s' on page '%s'.", text, this.displayName, this.page.getDisplayName()), this.by.toString(), this.browserManager.getCurrentBrowser().getCurrentTab());
    }

    protected void rawSendKeys(WebElement element, String text) {
        this.rawSendKeys(element, text, 50);
    }

    protected void rawSendKeys(WebElement element, String text, int delayMs) {
        if (delayMs == 0) {
            element.sendKeys(text);
        } else {
            for (int i = 0; i < text.length(); ++i) {
                element.sendKeys(String.valueOf(text.charAt(i)));

                try {
                    Thread.sleep(delayMs);
                } catch (InterruptedException var6) {
                    throw new WebDriverException("Thread interrupted during sleep");
                }
            }
        }

    }

    public Dimension getSize() {
        WebElement element = this.getRawObject();

        try {
            return element.getSize();
        } catch (WebDriverException var3) {
            this.reporter.failed(String.format("Get 'size' property from '%s'", this.displayName), String.format("Failed to get 'size' property from '%s' on page '%s'. %s", this.displayName, this.page.getDisplayName(), var3.getMessage()), this.by.toString(), this.browserManager.getCurrentBrowser().getCurrentTab());
            throw var3;
        }
    }

    public String getTagName() {
        WebElement element = this.getRawObject();

        try {
            return element.getTagName();
        } catch (WebDriverException var3) {
            this.reporter.failed(String.format("Get 'tagName' property from '%s'", this.displayName), String.format("Failed to get 'tagName' property from '%s' on page '%s'. %s", this.displayName, this.page.getDisplayName(), var3.getMessage()), this.by.toString(), this.browserManager.getCurrentBrowser().getCurrentTab());
            throw var3;
        }
    }

    public String getText() {
        WebElement element = this.getRawObject();

        try {
            return element.getText();
        } catch (WebDriverException var3) {
            this.reporter.failed(String.format("Get 'text' property from '%s'", this.displayName), String.format("Failed to get 'text' property from '%s' on page '%s'. %s", this.displayName, this.page.getDisplayName(), var3.getMessage()), this.by.toString(), this.browserManager.getCurrentBrowser().getCurrentTab());
            throw var3;
        }
    }

    public void waitUntilAttributeContains(long waitSecs, String attribute, String value) {
        Browser browser = this.browserManager.getCurrentBrowser();
        long implicitWait = browser.getImplicitWaitTimeMs();
        WebDriver driver = browser.getDriver();

        try {
            browser.setImplicitWaitTimeMs(0L);
            (new WebDriverWait(driver, waitSecs)).until(ExpectedConditions.attributeContains(this.by, attribute, value));
            logger.debug(String.format("Waited until attribute %s contains %s", attribute, value));
        } catch (TimeoutException var13) {
            throw var13;
        } finally {
            browser.setImplicitWaitTimeMs(implicitWait);
        }

    }

    public void waitUntilAttributeNotContains(long waitSecs, String attribute, String value) {
        Browser browser = this.browserManager.getCurrentBrowser();
        long implicitWait = browser.getImplicitWaitTimeMs();
        WebDriver driver = browser.getDriver();

        try {
            browser.setImplicitWaitTimeMs(0L);
            (new WebDriverWait(driver, waitSecs)).until(ExpectedConditions.not(ExpectedConditions.attributeContains(this.by, attribute, value)));
            logger.debug(String.format("Waited until attribute %s does NOT contain %s", attribute, value));
        } catch (TimeoutException var13) {
            throw var13;
        } finally {
            browser.setImplicitWaitTimeMs(implicitWait);
        }

    }

    public void waitUntilAttributeEquals(long waitSecs, String attribute, String value) {
        Browser browser = this.browserManager.getCurrentBrowser();
        long implicitWait = browser.getImplicitWaitTimeMs();
        WebDriver driver = browser.getDriver();

        try {
            browser.setImplicitWaitTimeMs(0L);
            (new WebDriverWait(driver, waitSecs)).until(ExpectedConditions.attributeToBe(this.by, attribute, value));
            logger.debug(String.format("Waited until attribute %s equals %s", attribute, value));
        } catch (TimeoutException var13) {
            throw var13;
        } finally {
            browser.setImplicitWaitTimeMs(implicitWait);
        }

    }

    public void waitUntilAttributeNotEquals(long waitSecs, String attribute, String value) {
        Browser browser = this.browserManager.getCurrentBrowser();
        long implicitWait = browser.getImplicitWaitTimeMs();
        WebDriver driver = browser.getDriver();

        try {
            browser.setImplicitWaitTimeMs(0L);
            (new WebDriverWait(driver, waitSecs)).until(ExpectedConditions.not(ExpectedConditions.attributeToBe(this.by, attribute, value)));
            logger.debug(String.format("Waited until attribute %s does NOT equal %s", attribute, value));
        } catch (TimeoutException var13) {
            throw var13;
        } finally {
            browser.setImplicitWaitTimeMs(implicitWait);
        }

    }

    public void waitUntilAttributeIsEmpty(long waitSecs, String attribute) {
        Browser browser = this.browserManager.getCurrentBrowser();
        long implicitWait = browser.getImplicitWaitTimeMs();
        WebDriver driver = browser.getDriver();
        WebElement element = this.getRawObject();

        try {
            browser.setImplicitWaitTimeMs(0L);
            (new WebDriverWait(driver, waitSecs)).until(ExpectedConditions.not(ExpectedConditions.attributeToBeNotEmpty(element, attribute)));
            logger.debug(String.format("Waited until attribute %s is empty", attribute));
        } catch (TimeoutException var13) {
            throw var13;
        } finally {
            browser.setImplicitWaitTimeMs(implicitWait);
        }

    }

    public void waitUntilAttributeIsNotEmpty(long waitSecs, String attribute) {
        Browser browser = this.browserManager.getCurrentBrowser();
        long implicitWait = browser.getImplicitWaitTimeMs();
        WebDriver driver = browser.getDriver();
        WebElement element = this.getRawObject();

        try {
            browser.setImplicitWaitTimeMs(0L);
            (new WebDriverWait(driver, waitSecs)).until(ExpectedConditions.attributeToBeNotEmpty(element, attribute));
            logger.debug(String.format("Waited until attribute %s is NOT empty", attribute));
        } catch (TimeoutException var13) {
            throw var13;
        } finally {
            browser.setImplicitWaitTimeMs(implicitWait);
        }

    }

    public void waitUntilIsClickable(long waitSecs) {
        Browser browser = this.browserManager.getCurrentBrowser();
        long implicitWait = browser.getImplicitWaitTimeMs();
        WebDriver driver = browser.getDriver();

        try {
            browser.setImplicitWaitTimeMs(0L);
            (new WebDriverWait(driver, waitSecs)).until(ExpectedConditions.elementToBeClickable(this.by));
            logger.debug("Waited until element is clickable");
        } catch (TimeoutException var11) {
            throw var11;
        } finally {
            browser.setImplicitWaitTimeMs(implicitWait);
        }

    }

    public void waitUntilIsNotClickable(long waitSecs) {
        Browser browser = this.browserManager.getCurrentBrowser();
        long implicitWait = browser.getImplicitWaitTimeMs();
        WebDriver driver = browser.getDriver();

        try {
            browser.setImplicitWaitTimeMs(0L);
            (new WebDriverWait(driver, waitSecs)).until(ExpectedConditions.not(ExpectedConditions.elementToBeClickable(this.by)));
            logger.debug("Waited until element is NOT clickable");
        } catch (TimeoutException var11) {
            throw var11;
        } finally {
            browser.setImplicitWaitTimeMs(implicitWait);
        }

    }

    public void waitUntilExists(long waitSecs) {
        Browser browser = this.browserManager.getCurrentBrowser();
        long implicitWait = browser.getImplicitWaitTimeMs();
        WebDriver driver = browser.getDriver();

        try {
            browser.setImplicitWaitTimeMs(0L);
            (new WebDriverWait(driver, waitSecs)).until(ExpectedConditions.presenceOfElementLocated(this.by));
            logger.debug("Waited until element exists");
        } catch (TimeoutException var11) {
            throw var11;
        } finally {
            browser.setImplicitWaitTimeMs(implicitWait);
        }

    }

    public void waitUntilNotExists(long waitSecs) {
        Browser browser = this.browserManager.getCurrentBrowser();
        long implicitWait = browser.getImplicitWaitTimeMs();
        WebDriver driver = browser.getDriver();

        try {
            browser.setImplicitWaitTimeMs(0L);
            (new WebDriverWait(driver, waitSecs)).until(ExpectedConditions.not(ExpectedConditions.presenceOfElementLocated(this.by)));
            logger.debug("Waited until element does NOT exist");
        } catch (TimeoutException var11) {
            throw var11;
        } finally {
            browser.setImplicitWaitTimeMs(implicitWait);
        }

    }

    public void waitUntilIsVisible(long waitSecs) {
        Browser browser = this.browserManager.getCurrentBrowser();
        long implicitWait = browser.getImplicitWaitTimeMs();
        WebDriver driver = browser.getDriver();

        try {
            browser.setImplicitWaitTimeMs(0L);
            (new WebDriverWait(driver, waitSecs)).until(ExpectedConditions.visibilityOfElementLocated(this.by));
            logger.debug("Waited until element is visible");
        } catch (TimeoutException var11) {
            throw var11;
        } finally {
            browser.setImplicitWaitTimeMs(implicitWait);
        }

    }

    public void waitUntilIsNotVisible(long waitSecs) {
        Browser browser = this.browserManager.getCurrentBrowser();
        long implicitWait = browser.getImplicitWaitTimeMs();
        WebDriver driver = browser.getDriver();

        try {
            browser.setImplicitWaitTimeMs(0L);
            (new WebDriverWait(driver, waitSecs)).until(ExpectedConditions.invisibilityOfElementLocated(this.by));
            logger.debug("Waited until element is NOT visible");
        } catch (TimeoutException var11) {
            throw var11;
        } finally {
            browser.setImplicitWaitTimeMs(implicitWait);
        }

    }

    public void waitUntilTextContains(long waitSecs, String value) {
        Browser browser = this.browserManager.getCurrentBrowser();
        long implicitWait = browser.getImplicitWaitTimeMs();
        WebDriver driver = browser.getDriver();

        try {
            browser.setImplicitWaitTimeMs(0L);
            (new WebDriverWait(driver, waitSecs)).until(ExpectedConditions.textToBePresentInElementLocated(this.by, value));
            logger.debug(String.format("Waited until text contains %s", value));
        } catch (TimeoutException var12) {
            throw var12;
        } finally {
            browser.setImplicitWaitTimeMs(implicitWait);
        }

    }

    public void waitUntilTextNotContains(long waitSecs, String value) {
        Browser browser = this.browserManager.getCurrentBrowser();
        long implicitWait = browser.getImplicitWaitTimeMs();
        WebDriver driver = browser.getDriver();

        try {
            browser.setImplicitWaitTimeMs(0L);
            (new WebDriverWait(driver, waitSecs)).until(ExpectedConditions.not(ExpectedConditions.textToBePresentInElementLocated(this.by, value)));
            logger.debug(String.format("Waited until text does NOT contain %s", value));
        } catch (TimeoutException var12) {
            throw var12;
        } finally {
            browser.setImplicitWaitTimeMs(implicitWait);
        }

    }

    public void waitUntilTextEquals(long waitSecs, String value) {
        Browser browser = this.browserManager.getCurrentBrowser();
        long implicitWait = browser.getImplicitWaitTimeMs();
        WebDriver driver = browser.getDriver();

        try {
            browser.setImplicitWaitTimeMs(0L);
            (new WebDriverWait(driver, waitSecs)).until(ExpectedConditions.textToBe(this.by, value));
            logger.debug(String.format("Waited until text equals %s", value));
        } catch (TimeoutException var12) {
            throw var12;
        } finally {
            browser.setImplicitWaitTimeMs(implicitWait);
        }

    }

    public void waitUntilTextNotEquals(long waitSecs, String value) {
        Browser browser = this.browserManager.getCurrentBrowser();
        long implicitWait = browser.getImplicitWaitTimeMs();
        WebDriver driver = browser.getDriver();

        try {
            browser.setImplicitWaitTimeMs(0L);
            (new WebDriverWait(driver, waitSecs)).until(ExpectedConditions.not(ExpectedConditions.textToBe(this.by, value)));
            logger.debug(String.format("Waited until text does NOT equal %s", value));
        } catch (TimeoutException var12) {
            throw var12;
        } finally {
            browser.setImplicitWaitTimeMs(implicitWait);
        }

    }

    public void waitUntilTextMatches(long waitSecs, Pattern pattern) {
        Browser browser = this.browserManager.getCurrentBrowser();
        long implicitWait = browser.getImplicitWaitTimeMs();
        WebDriver driver = browser.getDriver();

        try {
            browser.setImplicitWaitTimeMs(0L);
            (new WebDriverWait(driver, waitSecs)).until(ExpectedConditions.textMatches(this.by, pattern));
            logger.debug(String.format("Waited until text matches pattern %s", pattern.toString()));
        } catch (TimeoutException var12) {
            throw var12;
        } finally {
            browser.setImplicitWaitTimeMs(implicitWait);
        }

    }

    public void waitUntilTextNotMatches(long waitSecs, Pattern pattern) {
        Browser browser = this.browserManager.getCurrentBrowser();
        long implicitWait = browser.getImplicitWaitTimeMs();
        WebDriver driver = browser.getDriver();

        try {
            browser.setImplicitWaitTimeMs(0L);
            (new WebDriverWait(driver, waitSecs)).until(ExpectedConditions.not(ExpectedConditions.textMatches(this.by, pattern)));
            logger.debug(String.format("Waited until text does NOT match pattern %s", pattern.toString()));
        } catch (TimeoutException var12) {
            throw var12;
        } finally {
            browser.setImplicitWaitTimeMs(implicitWait);
        }

    }
}

