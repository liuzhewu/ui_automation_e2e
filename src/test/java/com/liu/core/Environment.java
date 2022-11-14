package com.liu.core;


import org.openqa.selenium.Dimension;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Environment {
    private static final Pattern browserSizePattern = Pattern.compile("^([1-9][0-9]*)x([1-9][0-9]*)$");
    private static Environment instance = null;

    public Environment() {
    }

    public static Environment getInstance() {
        if (instance == null) {
            instance = new Environment();
        }

        return instance;
    }

    public boolean getIsHeadless() {
        String headless = System.getProperty("critic.headless");
        if (headless == null) {
            return false;
        } else if (headless.equalsIgnoreCase("true")) {
            return true;
        } else if (headless.equalsIgnoreCase("false")) {
            return false;
        } else {
            throw new RuntimeException(String.format("System property 'critic.headless' has an invalid value: '%1$s'. Accepted values (case-insensitive): 'true' or 'false'.", headless));
        }
    }

    public boolean getSwitchTabResetSizeAndPosition() {
        String resize = System.getProperty("critic.browser.switchTabResetSizeAndPosition");
        if (resize == null) {
            return true;
        } else if (resize.equalsIgnoreCase("true")) {
            return true;
        } else if (resize.equalsIgnoreCase("false")) {
            return false;
        } else {
            throw new RuntimeException(String.format("System property 'critic.browser.switchTabResetSizeAndPosition' has an invalid value: '%1$s'. Accepted values (case-insensitive): 'true' or 'false'.", resize));
        }
    }

    public boolean getBrowserMaximize() {
        String maximize = System.getProperty("critic.browser.maximize");
        if (maximize == null) {
            return true;
        } else if (maximize.equalsIgnoreCase("true")) {
            return true;
        } else if (maximize.equalsIgnoreCase("false")) {
            return false;
        } else {
            throw new RuntimeException(String.format("System property 'critic.browser.maximize' has an invalid value: '%1$s'. Accepted values (case-insensitive): 'true' or 'false'.", maximize));
        }
    }

    public Dimension getBrowserSize() {
        String size = System.getProperty("critic.browser.size");
        if (size == null) {
            return new Dimension(1920, 1080);
        } else {
            Matcher matcher = browserSizePattern.matcher(size);
            if (!matcher.find()) {
                throw new RuntimeException(String.format("System property 'critic.browser.size' has an invalid format: '%1$s'. Expected format: /^([1-9][0-9]*)x([1-9][0-9]*)$/ E.g. 1920x1080", size));
            } else {
                return new Dimension(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)));
            }
        }
    }

    public Path getReportDir() {
        String reportDir = System.getProperty("report_dir");
        if (reportDir == null) {
            throw new RuntimeException("'report_dir' is required as a system property");
        } else {
            return Paths.get(reportDir).toAbsolutePath();
        }
    }

    public Path getScriptPath() {
        String scriptPath = System.getProperty("script_path");
        if (scriptPath == null) {
            throw new RuntimeException("'script_path' is required as a system property");
        } else {
            return Paths.get(scriptPath).toAbsolutePath();
        }
    }
}

