package com.liu.core;


import org.apache.commons.lang3.SystemUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

public class BrowserManager {
    private static final Logger logger = LoggerFactory.getLogger(BrowserManager.class);
    private static final BrowserManager instance = new BrowserManager();
    private final Map<String, Browser> browsers = new HashMap();
    private final Reporter reporter;
    private String currentBrowser = null;

    private BrowserManager() {
        if (SystemUtils.IS_OS_LINUX) {
            System.setProperty("webdriver.chrome.driver", "./src/test/resources/selenium-drivers/linux/chromedriver");
            System.setProperty("webdriver.gecko.driver", "./src/test/resources/selenium-drivers/linux/geckodriver");
        } else if (SystemUtils.IS_OS_MAC) {
            System.setProperty("webdriver.chrome.driver", "./src/test/resources/selenium-drivers/mac/chromedriver");
            System.setProperty("webdriver.gecko.driver", "./src/test/resources/selenium-drivers/mac/geckodriver");
        } else {
            if (!SystemUtils.IS_OS_WINDOWS) {
                throw new AssertionError("Unsupported OS: " + System.getProperty("os.name"));
            }

            System.setProperty("webdriver.chrome.driver", "./src/test/resources/selenium-drivers/windows/chromedriver.exe");
            System.setProperty("webdriver.gecko.driver", "./src/test/resources/selenium-drivers/windows/geckodriver.exe");
        }

        this.reporter = Reporter.getInstance();
    }

    public static BrowserManager getInstance() {
        return instance;
    }

    public Browser openNewBrowser(BrowserType browserType, String name) {
        Object driver = null;

        try {
            Objects.requireNonNull(name, "'name' must not be null");
            if (name.isBlank()) {
                throw new RuntimeException("'name' must not be an empty or blank string");
            }

            if (this.browsers.containsKey(name)) {
                throw new RuntimeException(String.format("Browser with name '%s' already exists", name));
            }

            switch (browserType) {
                case CHROME:
                    ChromeOptions options = new ChromeOptions();
                    options.addArguments("--no-sandbox");
                    options.addArguments("enable-automation");
                    options.addArguments("--disable-infobars");
                    options.addArguments("--disable-dev-shm-usage");
                    options.addArguments("--disable-browser-side-navigation");
                    options.addArguments("--disable-gpu");
                    if (Environment.getInstance().getIsHeadless()) {
                        options.addArguments("--headless");
                    }

                    driver = new ChromeDriver(options);
                    ((WebDriver) driver).get("about:blank");
                    break;
                case FIREFOX:
                    FirefoxOptions fire_options = new FirefoxOptions();
                    fire_options.setHeadless(Environment.getInstance().getIsHeadless());
                    fire_options.setProfile(new FirefoxProfile());
                    driver = new FirefoxDriver(fire_options);
                    ((WebDriver) driver).get("about:logo");
                    break;
                default:
                    throw new RuntimeException(String.format("Browser type '%s' is currently unsupported", browserType));
            }

            Browser newBrowser = new Browser(this, (WebDriver) driver, browserType, name);
            this.browsers.put(name, newBrowser);
            newBrowser.resetSizeAndPosition();
        } catch (Exception var5) {
            this.reporter.error("Error opening new browser", var5.toString(), null, null);
            throw var5;
        }

        this.reporter.info(String.format("Open new %s browser", browserType), String.format("Opened a new %s browser.", browserType), null, null);
        this.switchToBrowser(name);
        return this.browsers.get(name);
    }

    public Browser switchToBrowser(String browserName) {
        if (!this.browsers.containsKey(browserName)) {
            this.reporter.error(String.format("Error switching to browser '%s'", browserName), String.format("Browser with name '%s' does not exist.", browserName), null, null);
            throw new RuntimeException(String.format("Browser with name '%s' does not exist", browserName));
        } else {
            this.currentBrowser = browserName;
            return this.browsers.get(browserName);
        }
    }

    public Browser getCurrentBrowser() {
        if (this.currentBrowser == null) {
            this.reporter.error("Error getting current browser", "Current browser is not set - please set with .setCurrentBrowser().", null, null);
            throw new RuntimeException("Current browser is not set - please set with .setCurrentBrowser()");
        } else {
            return this.browsers.get(this.currentBrowser);
        }
    }

    public void closeBrowser(String browserName) {
        if (!this.browsers.containsKey(browserName)) {
            logger.error(String.format("Error closing browser '%s' - no browser of that name is being managed by the BrowserManager", browserName));
            this.reporter.warning(String.format("Error closing browser '%s'", browserName), "No browser of that name is being managed by the BrowserManager.", null, null);
        } else {
            try {
                WebDriver driver = this.browsers.get(browserName).getDriver();
                this.browsers.remove(browserName);
                driver.quit();
            } catch (Exception var3) {
                logger.error(String.format("Error closing browser '%s' - %s", browserName, var3));
                this.reporter.warning(String.format("Error closing browser '%s'", browserName), var3.toString(), null, null);
            }
        }

        if (this.currentBrowser != null && this.currentBrowser.equals(browserName)) {
            this.currentBrowser = null;
        }

    }

    public void closeAllBrowsers() {
        Iterator var1 = this.browsers.entrySet().iterator();

        while (var1.hasNext()) {
            Entry<String, Browser> entry = (Entry) var1.next();
            entry.getValue().close();
        }

    }
}
