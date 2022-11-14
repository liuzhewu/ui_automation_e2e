package com.liu.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * 配置管理，获取common.properties全局变量信息
 */
public class ConfigManager {
    private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);
    //对应common.properties
    public static Properties common = new Properties();
    //env中的各个环境的常用常量，包括ali、wmmp,对应ali.properties之类的
    public static Properties env = new Properties();

    static {
        //读取共有变量
        loadCommonConfig();
    }

    public static void loadCommonConfig() {
        try {
            common.load(new FileInputStream("src" + File.separator + "test" + File.separator + "resources" + File.separator + "common.properties"));
        } catch (IOException e) {
            logger.error("读取全局变量失败", e);
        }
    }

    public static void loadEnvConfig(String file) {
        try {
            env.load(new FileInputStream(file));
        } catch (IOException e) {
            logger.error("读取全局变量失败", e);
        }
    }

    public static void main(String[] args) {
        System.out.println(common.getProperty("DEFAULT_ENV"));

    }


}
