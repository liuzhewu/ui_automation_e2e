package com.liu.core;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class Browser {
    private static final Logger logger = LoggerFactory.getLogger(Browser.class);
    private final BrowserManager browserManager;
    private final WebDriver driver;
    private final BrowserType browserType;
    private final Reporter reporter;
    private final String browserName;
    private long implicitWaitTimeMs;
    private long pageLoadTimeoutMs;
    private String currentTabHandle;

    public Browser(BrowserManager browserManager, WebDriver driver, BrowserType browserType, String browserName) {
        this(browserManager, driver, browserType, browserName, Reporter.getInstance());
    }

    public Browser(BrowserManager browserManager, WebDriver driver, BrowserType browserType, String browserName, Reporter reporter) {
        this.browserManager = Objects.requireNonNull(browserManager, "'testBase' must not be null");
        this.driver = Objects.requireNonNull(driver, "'driver' must not be null");
        this.browserType = Objects.requireNonNull(browserType, "'browserType' must not be null");
        this.browserName = Objects.requireNonNull(browserName, "'name' must not be null");
        if (browserName.isBlank()) {
            throw new RuntimeException("'name' must not be an empty or blank string");
        } else {
            this.reporter = Objects.requireNonNull(reporter);
            this.setImplicitWaitTimeMs(0L);
            this.setPageLoadTimeoutMs(10000L);
            this.currentTabHandle = this.driver.getWindowHandle();
        }
    }

    public void close() {
        this.browserManager.closeBrowser(this.browserName);
    }

    public WebDriver getDriver() {
        return this.driver;
    }

    public BrowserType getBrowserType() {
        return this.browserType;
    }

    public String getBrowserName() {
        return this.browserName;
    }

    public long getImplicitWaitTimeMs() {
        return this.implicitWaitTimeMs;
    }

    public void setImplicitWaitTimeMs(long implicitWaitTimeMs) {
        this.implicitWaitTimeMs = implicitWaitTimeMs;
        this.driver.manage().timeouts().implicitlyWait(this.implicitWaitTimeMs, TimeUnit.MILLISECONDS);
    }

    public long getPageLoadTimeoutMs() {
        return this.pageLoadTimeoutMs;
    }

    public void setPageLoadTimeoutMs(long pageLoadTimeoutMs) {
        this.pageLoadTimeoutMs = pageLoadTimeoutMs;
        this.driver.manage().timeouts().pageLoadTimeout(this.pageLoadTimeoutMs, TimeUnit.MILLISECONDS);
    }

    public Tab getTab(String tabHandle) {
        logger.debug(String.format("Getting current tab for browser '%s'...", this.browserName));
        if (!this.handleExists(tabHandle)) {
            throw new RuntimeException(String.format("Browser '%s' does not have a tab with handle '%s'", this.browserName, tabHandle));
        } else {
            return new Tab(this, tabHandle);
        }
    }

    public Tab getCurrentTab() {
        return this.getTab(this.currentTabHandle);
    }

    private boolean handleExists(String tabHandle) {
        Set<String> handles = this.driver.getWindowHandles();
        Iterator var3 = handles.iterator();

        String handle;
        do {
            if (!var3.hasNext()) {
                return false;
            }

            handle = (String) var3.next();
        } while (!handle.equals(tabHandle));

        return true;
    }

    public void resetSizeAndPosition() {
        if (!Environment.getInstance().getIsHeadless() && Environment.getInstance().getBrowserMaximize()) {
            this.driver.manage().window().maximize();
        } else {
            this.driver.manage().window().setSize(Environment.getInstance().getBrowserSize());
            this.driver.manage().window().setPosition(new Point(0, 0));
        }

    }

    public Tab openNewTab() {
        logger.debug(String.format("Opening a new tab in browser '%s'...", this.browserName));
        Set<String> handles1 = this.driver.getWindowHandles();
        switch (this.browserType) {
            case CHROME:
                ((JavascriptExecutor) this.driver).executeScript("window.open('about:blank', '_blank');");
                Instant expires = Instant.now().plusMillis(5000L);

                while (Instant.now().isBefore(expires) && this.driver.getWindowHandles().size() <= handles1.size()) {
                    try {
                        Thread.sleep(100L);
                    } catch (InterruptedException var5) {
                    }
                }

                Set<String> handles2 = this.driver.getWindowHandles();
                handles2.removeAll(handles1);
                if (handles2.size() != 1) {
                    throw new RuntimeException(String.format("Critic can't determine which (if any) new tab has been opened. Difference in window handles before and after: %d", handles2.size()));
                } else {
                    String newTabHandle = handles2.iterator().next();
                    logger.debug(String.format("New tabHandle=%s", newTabHandle));
                    logger.info(String.format("Opened a new tab in browser '%s'", this.browserName));
                    this.reporter.info(String.format("Open new tab in browser '%s'", this.browserName), String.format("Opened a new tab in browser '%s'.", this.browserName), null, null);
                    return this.switchToTab(newTabHandle, true);
                }
            default:
                throw new RuntimeException(String.format("Browser type '%s' is currently unsupported", this.browserType));
        }
    }

    public Tab addNewTabAfter(Runnable func) {
        return this.addNewTabAfter(func, Environment.getInstance().getSwitchTabResetSizeAndPosition());
    }

    public Tab addNewTabAfter(Runnable func, boolean resetSizeAndPosition) {
        return this.addNewTabAfter(func, resetSizeAndPosition, 5000L);
    }

    public Tab addNewTabAfter(Runnable func, boolean resetSizeAndPosition, long timeoutMs) {
        logger.debug(String.format("Adding new tab to browser '%s' after function call...", this.browserName));
        Set<String> handles1 = this.driver.getWindowHandles();
        func.run();
        Instant expires = Instant.now().plusMillis(timeoutMs);

        while (Instant.now().isBefore(expires) && this.driver.getWindowHandles().size() <= handles1.size()) {
            try {
                Thread.sleep(100L);
            } catch (InterruptedException var9) {
            }
        }

        Set<String> handles2 = this.driver.getWindowHandles();
        handles2.removeAll(handles1);
        if (handles2.size() != 1) {
            throw new RuntimeException(String.format("Critic can't determine which (if any) new tab has been opened. Difference in window handles before and after: %d", handles2.size()));
        } else {
            String newTabHandle = handles2.iterator().next();
            logger.debug(String.format("New tabHandle=%s", newTabHandle));
            logger.info(String.format("Added a new tab to browser '%s' after calling a function", this.browserName));
            this.reporter.info(String.format("Add new tab to browser '%s' after function call", this.browserName), String.format("Added a new tab to browser '%s' after calling a function.", this.browserName), null, null);
            return this.switchToTab(newTabHandle, resetSizeAndPosition);
        }
    }

    public Tab switchToTab(String tabHandle) {
        return this.switchToTab(tabHandle, Environment.getInstance().getSwitchTabResetSizeAndPosition());
    }

    public Tab switchToTab(String tabHandle, boolean resetSizeAndPosition) {
        logger.debug(String.format("Switching to tab '%s' in browser '%s'...", tabHandle, this.browserName));
        if (!this.handleExists(tabHandle)) {
            throw new RuntimeException(String.format("Browser '%s' does not have a tab with handle '%s'", this.browserName, tabHandle));
        } else {
            if (!tabHandle.equals(this.currentTabHandle)) {
                this.currentTabHandle = tabHandle;
                this.driver.switchTo().window(this.currentTabHandle);
                if (resetSizeAndPosition) {
                    this.resetSizeAndPosition();
                }

                logger.info(String.format("Switched to tab '%s' in browser '%s'", tabHandle, this.browserName));
                this.reporter.info(String.format("Switch to tab '%s' in browser '%s'", tabHandle, this.browserName), String.format("Switched to tab '%s' in browser '%s'.", tabHandle, this.browserName), null, new Tab(this, this.currentTabHandle));
            }

            return new Tab(this, this.currentTabHandle);
        }
    }

    public void closeTab(String tabHandle) {
        logger.debug(String.format("Closing tab '%s' in browser '%s'...", tabHandle, this.browserName));
        if (!this.handleExists(tabHandle)) {
            logger.warn(String.format("Failed to close tab '%s' in browser '%s' - a tab with that handle does NOT exist", tabHandle, this.browserName));
            this.reporter.warning(String.format("Failed to close tab '%s' in browser '%s'", tabHandle, this.browserName), String.format("Failed to close tab '%s' in browser '%s' - a tab with that handle does NOT exist.", tabHandle, this.browserName), null, null);
        } else {
            this.driver.switchTo().window(tabHandle);
            this.driver.close();
            logger.info(String.format("Closed tab '%s' in browser '%s'", tabHandle, this.browserName));
            this.reporter.info(String.format("Close tab '%s' in browser '%s'", tabHandle, this.browserName), String.format("Closed tab '%s' in browser '%s'.", tabHandle, this.browserName), null, null);
        }
    }

    public void withImplicitWait(long waitMs, Runnable func) {
        long currentWait = this.getImplicitWaitTimeMs();

        try {
            this.setImplicitWaitTimeMs(waitMs);
            func.run();
        } finally {
            this.setImplicitWaitTimeMs(currentWait);
        }

    }
}
