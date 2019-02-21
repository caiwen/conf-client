package com.gbconf.serverclient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesUtil {
    public static String getValue(String key) {
        Properties prop = new Properties();
        try {
            //装载配置文件
            prop.load(new FileInputStream(new File("config.cfg")));

        } catch (IOException e) {
            e.printStackTrace();
        }
        //返回获取的值
        return prop.getProperty(key);
    }
}
