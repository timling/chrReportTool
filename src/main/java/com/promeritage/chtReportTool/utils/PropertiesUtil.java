package com.promeritage.chtReportTool.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Properties;

import com.promeritage.chtReportTool.dto.ProUser;

public class PropertiesUtil {
    public static final String POPERTIES_NAME = "config.properties";

    public static Properties loadOrCreateProperties(String propertiesName) {
        Properties props = null;
        File configFile = new File(propertiesName);
        if (!configFile.exists()) {
            createConfigFile(propertiesName, null);
        } else {
            props = loadProperties(propertiesName);
        }
        return props;
    }

    public static Properties loadProperties(String propertiesName) {
        Properties props = new Properties();
        try {
            props.load(new InputStreamReader(new FileInputStream(propertiesName), "UTF8"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return props;
    }

    private static String nullToEmpty(String str) {
        return str == null ? "" : str;
    }

    public static void createConfigFile(String config, ProUser proUser) {
        Writer out = null;
        if (proUser == null) {
            proUser = new ProUser();
        }
        try {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(config), "UTF8"));
            String sep = System.getProperty("line.separator");

            out.append("email=").append(nullToEmpty(proUser.getEmail())).append(sep);
            out.append("password=").append(sep);
            out.append("name=").append(sep);
            out.append("pmName=").append(sep);

            out.append("proxySet=false").append(sep);
            out.append("proxyHost=10.160.3.88").append(sep);
            out.append("proxyPort=8080").append(sep);

            out.append("dailyMailAddr=ireneliu@cht.com.tw,evanhuang@cht.com.tw,annyhu@cht.com.tw")
                    .append(sep);
            out.append("hrMailAddr=funny.wu@promeritage.com.tw,queenie.chang@promeritage.com.tw")
                    .append(sep);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public static String getConfig(Properties props, String key) {
        return getConfig(props, key, System.getProperty("file.encoding"));
    }

    public static String getConfig(Properties props, String key, String encoding) {
        String value = props.getProperty(key);
        if (value == null) {
            return value;
        }
        try {
            value = new String(value.getBytes("MS950"), encoding);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return value;
    }
}
