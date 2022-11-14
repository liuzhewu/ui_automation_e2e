package com.liu.asserts;

import com.liu.test.Base;
import com.liu.util.Tool;
import com.liu.core.Browser;
import com.liu.core.ElementBase;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.liu.util.Constant.LONG_WAIT_TIME;
import static com.liu.util.Constant.WAIT_TIME;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * 封装断言，方便调用
 */
public class Assertion {
    protected static final Logger logger = LoggerFactory.getLogger(Assertion.class);
    public static Browser b1 = Base.b1;
    private static final WebDriverWait wait = new WebDriverWait(b1.getDriver(), WAIT_TIME);

    /**
     * 判断当前页面url包含预期url
     * judge current url contain expected urlContains
     *
     * @param urlContains 包含url
     * @param msg         断言信息
     */
    public static void assertUrlContains(String urlContains, String msg) {
        boolean isContainsUrl = true;
        try {
            wait.until(ExpectedConditions.urlContains(urlContains));
        } catch (Exception e) {
            isContainsUrl = false;
        }
        String currentUrl = b1.getDriver().getCurrentUrl();
        logger.info("校验url为:" + urlContains + "当前url为:" + currentUrl);
        assertTrue(isContainsUrl, msg, false);
    }


    /**
     * 判断元素可点击
     *
     * @param elementBase 断言元素
     */
    public static void assertClickable(ElementBase elementBase) {
        assertClickable(elementBase, WAIT_TIME);
    }


    public static void assertClickable(ElementBase elementBase, long waitTime) {
        assertClickable(elementBase.getRawObject(), waitTime);
    }


    public static void assertClickable(WebElement webElement) {
        assertClickable(webElement, WAIT_TIME);
    }

    public static void assertClickable(WebElement webElement, long waitTime) {
        boolean clickable = true;
        try {
            new WebDriverWait(b1.getDriver(), waitTime).until(ExpectedConditions.elementToBeClickable(webElement));
        } catch (Exception e) {
            clickable = false;
        }
        assertTrue(clickable, "元素可点击(等待时间" + waitTime + "s)");
    }


    /**
     * 判断元素可见
     *
     * @param elementBase 断言元素
     */
    public static void assertElementVisibly(ElementBase elementBase) {
        boolean elementPresent = true;
        try {
            elementBase.waitUntilIsVisible(WAIT_TIME);
        } catch (Exception e) {
            elementPresent = false;
        }
        assertTrue(elementPresent, "元素可见(等待时间" + WAIT_TIME + "s)");
    }

    /**
     * 判断元素不可见
     *
     * @param elementBase 断言元素
     */
    public static void assertElementInvisibly(ElementBase elementBase) {
        assertElementInvisibly(elementBase, WAIT_TIME);
    }

    public static void assertElementInvisibly(ElementBase elementBase, long waitTime) {
        boolean elementInvisibly = true;
        try {
            elementBase.waitUntilIsNotVisible(waitTime);
        } catch (Exception e) {
            elementInvisibly = false;
        }
        assertTrue(elementInvisibly, "元素不可见(等待时间" + waitTime + "s)");
    }


    /**
     * 判断元素出现，elementBase.exists根本不行
     *
     * @param elementBase 断言元素
     */
    public static void assertExist(ElementBase elementBase) {
        assertExist(elementBase, WAIT_TIME);
    }

    public static void assertExistLong(ElementBase elementBase) {
        assertExist(elementBase, LONG_WAIT_TIME);
    }

    public static void assertExist(ElementBase elementBase, long waitTime) {
        boolean exist = true;
        try {
            elementBase.waitUntilExists(waitTime);
        } catch (Exception e) {
            exist = false;
        }
        assertTrue(exist, "元素存在(等待时间" + waitTime + "s)");
    }


    public static void assertTextContainsLong(ElementBase elementBase, String text) {
        assertTextContains(elementBase, text, LONG_WAIT_TIME);
    }

    public static void assertTextContains(ElementBase elementBase, String text) {
        assertTextContains(elementBase, text, WAIT_TIME);
    }

    public static void assertTextContains(ElementBase elementBase, String text, long waitTime) {
        boolean textContains = true;
        try {
            elementBase.waitUntilTextContains(waitTime, text);
        } catch (Exception e) {
            textContains = false;
        }
        assertTrue(textContains, "期待文本出现(等待时间" + waitTime + "s)" + "，文本信息：" + text);
    }

    public static void assertTextNotContains(ElementBase elementBase, String text) {
        assertTextNotContains(elementBase, text, WAIT_TIME);
    }

    public static void assertTextNotContains(ElementBase elementBase, String text, long waitTime) {
        boolean textContains = true;
        try {
            elementBase.waitUntilTextNotContains(waitTime, text);
        } catch (Exception e) {
            textContains = false;
        }
        assertTrue(textContains, "期待文本不出现(等待时间" + waitTime + "s)" + "，文本信息：" + text);
    }


    /**
     * 等待元素包含属性
     *
     * @param elementBase 元素
     * @param attribute   属性
     * @param value       值
     */
    public static void assertAttributeContains(ElementBase elementBase, String attribute, String value) {
        boolean textContains = true;
        try {
            elementBase.waitUntilAttributeContains(WAIT_TIME, attribute, value);
        } catch (Exception e) {
            textContains = false;
        }
        assertTrue(textContains, "期待属性出现" + "，属性：" + attribute + "，值：" + value);
    }


    /**
     * 根据提供的元素的定位方式，校验符合的个数
     *
     * @param elementBase 提供的元素
     * @param number      预期元素个数
     */
    public static void assertNumberOfElementsToBe(ElementBase elementBase, int number) {
        boolean numberOfElementsFit = true;
        try {
            wait.until(ExpectedConditions.numberOfElementsToBe(elementBase.getBy(), number));
        } catch (Exception e) {
            numberOfElementsFit = false;
        }
        assertTrue(numberOfElementsFit, "出现符合的元素个数，元素信息：" + elementBase.getBy() + "，期待个数：" + number + "，实际个数为：" + b1.getDriver().findElements(elementBase.getBy()).size());
    }

    /**
     * 失败断言
     */
    public static void assertFalse(String msg) {
        assertTrue(false, msg);
    }

    public static void assertTrue(boolean flag, String msg) {
        assertTrue(flag, msg, true);
    }

    /**
     * 是否使用到元素，部分情况下没有操作元素信息，比如:跳转页面
     *
     * @param flag       断言结果，true或者false
     * @param msg        断言信息
     * @param useElement 使用元素
     */
    public static void assertTrue(boolean flag, String msg, boolean useElement) {
        String result = " 执行断言，断言信息：" + msg + "，结果：" + flag;
        if (useElement) {
            Base.step(result);
        } else {
            Tool.step(result);
        }
        assertThat(flag).as(msg).isTrue();
    }


}
