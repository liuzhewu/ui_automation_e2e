package com.liu.util;

import com.liu.pojo.UiElement;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 元素管理类，负责从ui.xml读取所有元素信息
 */
public class UIUtil {
    private static final Logger logger = LoggerFactory.getLogger(UIUtil.class);
    public static Map<String, Map<String, UiElement>> elementMap = new HashMap<>();

    static {
        loadPages();
    }

    private static void loadPages() {
        SAXReader reader = new SAXReader();
        InputStream in;
        try {
            in = new FileInputStream("src" + File.separator + "test" + File.separator + "resources" + File.separator + "ui.xml");

            Document document = reader.read(in);
            Element root = document.getRootElement();
            List<Element> pages = root.elements("page");
            loadElements(pages);
        } catch (Exception e) {
            logger.error("读取ui.xml出现异常", e);

        }

    }

    private static void loadElements(List<Element> pages) {
        for (Element pageElement : pages) {
            String pageKeyword = pageElement.attributeValue("keyWord");
            List<Element> elements = pageElement.elements("uiElement");
            Map<String, UiElement> eleMap = new HashMap<>();
            for (Element element : elements) {
                String keyWord = element.attributeValue("keyWord");
                String by = element.attributeValue("by");
                String value = element.attributeValue("value");
                UiElement uiElement = new UiElement(keyWord, by, value);
                eleMap.put(keyWord, uiElement);
            }
            elementMap.put(pageKeyword, eleMap);
            List<Element> childPage = pageElement.elements("page");
            loadElements(childPage);
        }

    }

    public static void main(String[] args) {
        UiElement uiElement = elementMap.get("物模型_从指令库添加").get("重启");
        System.out.println(uiElement.toString());
    }


}
