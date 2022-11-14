package com.liu.test;

import com.liu.core.*;
import com.liu.util.Tool;
import com.liu.asserts.Assertion;
import com.liu.enums.ControlType;
import com.liu.listener.CustomListener;
import com.liu.pojo.TestData;
import com.liu.pojo.UiElement;
import com.liu.util.ConfigManager;
import com.liu.util.TestDataManager;
import com.liu.util.UIUtil;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Listeners;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

import static com.liu.util.Constant.*;

/**
 * 测试基类，封装常用控件方法
 */
@Listeners(CustomListener.class)
public class Base extends Page {
    protected static final Logger logger = LoggerFactory.getLogger(Base.class);
    public static Browser b1 = null;
    //窗口栈，记录跳转窗口，方便按顺序关闭
    public static Stack<String> windows = new Stack<>();
    //记录当前模块名
    public static String module = "";
    //操作结束的最后1个页面关键字，用于失败截图和记录step
    public static String lastPage = "";
    //操作结束的最后1个元素关键字，用于失败截图和记录step
    public static String lastKeyword = "";
    //操作结束的最后1个元素替代字符串，用于失败截图和记录step
    public static String lastReplaceValue = "";
    protected static BrowserManager browserManager = null;
    //测试用例集合
    protected static Map<String, HashMap<String, ArrayList<TestData>>> testDataList = TestDataManager.testDataList;
    //页面元素集合
    protected static Map<String, Map<String, UiElement>> elementMap = UIUtil.elementMap;
    protected static Properties env = ConfigManager.env;
    protected static Properties common = ConfigManager.common;
    //系统url
    protected static String systemUrl;
    //首页url
    protected static String commonHomeUrl;
    protected static String coreHomeUrl;
    protected static String cpHomeUrl;
    //操作结束的最后1个元素类型，用于失败截图和记录step
    private static ControlType lastControlType = ControlType.BASE;

    /**
     * @return 驱动
     */
    public static WebDriver getDriver() {
        return b1.getDriver();
    }


    public static void step(String msg) {
        step(lastPage, lastKeyword, lastReplaceValue, msg);
    }

    /**
     * 调用allure的step
     *
     * @param page         ui.xml中page的keyWord
     * @param keyword      ui.xml中uiElement的keyWord
     * @param replaceValue ui.xml中uiElement value的%PARAM0
     * @param msg          打印信息
     */
    protected static void step(String page, String keyword, String replaceValue, String msg) {
        String replace = "";
        if (StringUtils.isNotEmpty(replaceValue)) {
            replace = ",replace:" + replaceValue;
        }
        Tool.step(msg + ",元素信息:page" + page + ",keyword:" + keyword + replace);
    }


    protected String getCommonProperty(String key) {
        return common.getProperty(key);
    }

    protected String getEnvProperty(String key) {
        return env.getProperty(key);
    }

    protected Object[] getTestData(String menu, String operate) {
        return testDataList.get(menu).get(operate).toArray();
    }


    /**
     * 非密码的文本框输入
     *
     * @param page    页面的定位信息
     * @param keyword 元素的定位信息
     * @param value   文本值
     */
    protected void sendKey(String page, String keyword, String value) {
        sendKeyNeedReplace(page, keyword, "", value);
    }

    /**
     * 非密码的文本框输入，定位值需要从测试用例替换部分字符串
     *
     * @param page         页面
     * @param keyword      元素关键字
     * @param replaceValue 替换字符串
     * @param value        定位值
     */
    protected void sendKeyNeedReplace(String page, String keyword, String replaceValue, String value) {
        TextBox textBox = (TextBox) findElementNeedReplace(page, keyword, replaceValue, ControlType.TEXTBOX);
        try {
            step();
            textBox.setValue(value, 0);
        } catch (Exception e) {
            logger.warn("元素输入文本出现异常，元素信息:page:" + page + ",keyword:" + keyword + ",replaceValue:" + replaceValue);
            throw e;
        }

    }

    /**
     * 密码的文本框输入
     *
     * @param page    页面
     * @param keyword 元素关键字
     * @param value   定位值
     */
    protected void sendKeyPasswd(String page, String keyword, String value) {
        if (StringUtils.isEmpty(value)) {
            logger.warn("无值发送，元素信息:page" + page + ",keyword:" + keyword);
            return;
        }
        PasswordBox passwordBox = (PasswordBox) findElement(page, keyword, ControlType.PASSWORD);
        try {
            step();
            passwordBox.setValue(value, 0);
        } catch (Exception e) {
            logger.warn("密码输入文本出现异常，元素信息:page:" + page + ",keyword:" + keyword + ",value" + value);
            throw e;
        }

    }

    /**
     * 点击操作
     *
     * @param page    页面
     * @param keyword 元素关键字
     */
    protected void click(String page, String keyword) {
        clickNeedReplace(page, keyword, "");
    }

    /**
     * 点击操作，定位值需要从测试用例替换部分字符串
     *
     * @param page         页面
     * @param keyword      元素关键字
     * @param replaceValue 替换字符串
     */
    protected void clickNeedReplace(String page, String keyword, String replaceValue) {
        ElementBase elementBase = findElementNeedReplace(page, keyword, replaceValue, ControlType.BASE);
        click(elementBase);
    }

    protected void click(ElementBase elementBase) {
        click(elementBase.getRawObject());
    }

    /**
     * 针对部分情况下，没有直接使用关键字驱动(page,keyword)找到的元素
     *
     * @param webElement 页面元素
     */
    protected void click(WebElement webElement) {
        try {
            Assertion.assertClickable(webElement);
            step();
            webElement.click();
        } catch (Error e) {
            //Error是针对断言失败的错误
            logger.warn("元素不可点击，元素信息:page:" + lastPage + ",keyword:" + lastKeyword + ",replaceValue:" + lastReplaceValue, e);
            clickNonstandardControl(webElement);
        } catch (Exception e) {
            //Exception针对webElement.click()失败
            logger.warn("元素点击出现异常，元素信息:page:" + lastPage + ",keyword:" + lastKeyword + ",replaceValue:" + lastReplaceValue, e);
            clickNonstandardControl(webElement);
        }
    }

    protected void clickNonstandardControl(String page, String keyword) {
        clickNeedReplaceNonstandardControl(page, keyword, "");
    }

    protected void clickNeedReplaceNonstandardControl(String page, String keyword, String replaceValue) {
        clickNonstandardControl(findElementNeedReplace(page, keyword, replaceValue).getRawObject());
    }

    protected void clickNonstandardControl(WebElement webElement) {
        try {
            stepByJs();
            executeJs("arguments[0].click();", webElement);
        } catch (Exception e) {
            logger.error("非标准元素点击出现异常，元素信息:page:" + lastPage + ",keyword:" + lastKeyword + ",replaceValue:" + lastReplaceValue);
            throw e;
        }
    }

    /**
     * 获取基本元素
     *
     * @param page    页面
     * @param keyword 元素关键字
     * @return 元素
     */
    protected ElementBase findElement(String page, String keyword) {
        return findElement(page, keyword, ControlType.BASE);
    }

    /**
     * 获取各种类型元素
     *
     * @param page    页面
     * @param keyword 元素关键字
     * @param type    控件类型
     * @return 元素
     */
    protected ElementBase findElement(String page, String keyword, ControlType type) {
        return findElementNeedReplace(page, keyword, "", type);
    }

    public ElementBase findElementNeedReplace(String page, String keyword, String replaceValue) {
        return findElementNeedReplace(page, keyword, replaceValue, ControlType.BASE);
    }

    /**
     * 获取元素，定位值需要从测试用例替换部分字符串
     *
     * @param page         页面
     * @param keyword      元素关键字
     * @param type         控件类型
     * @param replaceValue 替换字符串
     * @return 元素
     */
    public ElementBase findElementNeedReplace(String page, String keyword, String replaceValue, ControlType type) {
        ElementBase elementBase = null;
        By by = getByReplace(page, keyword, replaceValue);
        lastPage = page;
        lastKeyword = keyword;
        lastReplaceValue = replaceValue;
        lastControlType = type;
        switch (type) {
            case BASE:
                elementBase = new ElementBase(this, by, keyword);
                break;
            case CHECKBOX:
                elementBase = new CheckBox(this, by, keyword);
                break;
            case SINGLELIST:
                elementBase = new SingleListBox(this, by, keyword);
                break;
            case TEXTBOX:
                elementBase = new TextBox(this, by, keyword);
                break;
            case PASSWORD:
                elementBase = new PasswordBox(this, by, keyword);
                break;
        }
        return elementBase;
    }

    /**
     * 切换iframe
     * switch to iframe
     *
     * @param index 切换frame索引
     */
    public void switchToFrame(int index) {
        getDriver().switchTo().frame(index);
    }

    /**
     * 打开页签
     * switch page
     *
     * @param url 链接
     */
    protected void to(String url) {
        browserManager.getCurrentBrowser().getCurrentTab().navigate(url);
    }


    protected void scrollTop(ElementBase elementBase) {
        scroll(elementBase.getRawObject(), true);
    }

    protected void scrollTop(WebElement webElement) {
        scroll(webElement, true);
    }

    /**
     * 移动上下滚动条
     * true:使元素element对象的“顶部”与当前窗口的“顶部”对齐,默认true
     * false:使元素element对象的“底部”与当前窗口的“底部”对齐
     * 部分元素定位没问题，但是由于处于低端，display值为空，导致不可点击
     *
     * @param webElement 操作元素
     * @param top        滚动后，是否让元素处于顶部
     */
    protected void scroll(WebElement webElement, boolean top) {
        if (top) {
            executeJs("arguments[0].scrollIntoView(true);", webElement);
        } else {
            executeJs("arguments[0].scrollIntoView(false);", webElement);
        }
    }

    protected void executeJs(String cmd, Object... params) {
        JavascriptExecutor javascriptExecutor = (JavascriptExecutor) getDriver();
        javascriptExecutor.executeScript(cmd, params);
    }

    /**
     * 使鼠标聚焦到某个元素
     *
     * @param elementBase 聚焦元素
     */
    protected void focusElement(ElementBase elementBase) {
        new Actions(getDriver()).moveToElement(elementBase.getRawObject()).perform();
    }

    /**
     * 查看元素，用于辅助定位
     *
     * @param xpath 元素的xpath定位信息
     */
    protected void viewElements(String xpath) {
        List<WebElement> list = getDriver().findElements(By.xpath(xpath));
        System.out.println("元素信息个数:" + list.size());
        for (WebElement element : list) {
            System.out.println("元素信息，xpath:" + xpath);
            System.out.println("element:" + element.toString());
            System.out.println("tag:" + element.getTagName());
            System.out.println("text:" + element.getText());
            System.out.println("enabled:" + element.isEnabled());
            System.out.println("displayed:" + element.isDisplayed());

        }
    }


    /**
     * 休眠一段时间，针对部分元素无法点击，点击错位置问题
     */
    protected void sleep() {
        sleep(SLEEP_TIME);
    }

    protected void sleep(long sleepTime) {
        try {
            Thread.sleep(sleepTime * SECOND);
        } catch (InterruptedException e) {
            logger.error("休眠出现问题", e);
        }
    }

    private By getByReplace(String page, String keyword, String replaceValue) {
        By by = null;
        UiElement uiElement = elementMap.get(page).get(keyword);
        String value = uiElement.getValue();
        if (StringUtils.isNotEmpty(replaceValue)) {
            value = value.replaceAll(REPLACE_STR, replaceValue);
        }
        switch (uiElement.getBy()) {
            case "id":
                by = By.id(value);
                break;
            case "linkText":
                by = By.linkText(value);
                break;
            case "partialLinkText":
                by = By.partialLinkText(value);
                break;
            case "name":
                by = By.name(value);
                break;
            case "tagName":
                by = By.tagName(value);
                break;
            case "xpath":
                by = By.xpath(value);
                break;
            case "className":
                by = By.className(value);
                break;
            case "cssSelector":
                by = By.cssSelector(value);
                break;
            default:
                logger.error("不支持的类型,元素信息:" + uiElement);

        }
        return by;
    }

    protected void switchToNewWindow() {
        Set<String> windowHandles = getDriver().getWindowHandles();
        //从获取到的所有窗口句柄windowHandles删除掉已保存的窗口句柄windows
        windows.forEach(windowHandles::remove);
        if (windowHandles.size() < 1) {
            logger.info("不能切换到新窗口" + windows.size() + ",切换失败");
            Tool.step(" 切换到新窗口" + windows.size() + "失败.");
            return;
        }
        String windowHand = windowHandles.iterator().next();
        b1.switchToTab(windowHand);
        windows.push(windowHand);
        Tool.step(" 切换到新窗口" + windows.size() + ",窗口信息:" + windowHand);
    }

    protected void switchToOldWindow() {
        Tool.step(" 关闭窗口" + windows.size() + ",窗口信息:" + windows.peek());
        b1.closeTab(windows.pop());
        b1.switchToTab(windows.peek(), false);
        Tool.step(" 切换到旧窗口" + windows.size() + ",窗口信息:" + windows.peek());
    }


    protected void switchToNew() {
        switchToNewWindow();
    }


    public String getLastPage() {
        return lastPage;
    }

    public String getLastKeyword() {
        return lastKeyword;
    }

    public String getLastReplaceValue() {
        return lastReplaceValue;
    }

    /**
     * 排查错误时，可用此方法截图
     *
     * @param imageName 图片名称
     */
    protected void screenshot(String imageName) {
        String time = Tool.getCurrentTimestamp();
        logger.warn("base截图，时间戳为:" + time);
        File file = ((TakesScreenshot) Base.getDriver()).getScreenshotAs(OutputType.FILE);
        imageName = imageName + "_" + Tool.getCurrentTimestamp() + ".png";
        try {
            Path path = Paths.get(Environment.getInstance().getReportDir().toString(), imageName);
            Files.copy(file.toPath(), path, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            logger.error("base截图失败", e);
        }
    }


    protected void stepByJs() {
        step(" 通过JS操作" + lastControlType.getDesc());
    }

    protected void step() {
        step(" 操作" + lastControlType.getDesc());
    }

    protected void toCommonHomePage() {
        to(commonHomeUrl);
    }

    //    core独有begin
    protected void toCoreHomePage() {
        to(coreHomeUrl);
    }

    //    core独有end

    //    cp独有begin
    protected void toCpHomePage() {
        to(cpHomeUrl);
    }


    //    cp独有end


}
