package com.promeritage.chtReportTool.utils;

import java.util.Properties;

import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.util.AuthenticationException;
import com.promeritage.chtReportTool.dto.ProUser;

public class UserUtil {
    private static ProUser proUser = null;

    public static ProUser getUser() {
        if (proUser == null) {
            Properties properties = PropertiesUtil
                    .loadOrCreateProperties(PropertiesUtil.POPERTIES_NAME);

            proUser = new ProUser();
            proUser.setEmail(properties.getProperty("email"));
            proUser.setPassword(properties.getProperty("password"));
            proUser.setName(properties.getProperty("name"));
            proUser.setPmName(properties.getProperty("pmName"));

            proUser.setDailyMailAddr(properties.getProperty("dailyMailAddr"));
            proUser.setHrMailAddr(properties.getProperty("hrMailAddr"));

            configProxy(properties);

            try {
                CalendarService service = CalendarUtil.getService(proUser);
                CalendarUtil.setCalendarUrls(service, proUser);
            } catch (AuthenticationException e) {
                e.printStackTrace();
            }
        }
        return proUser;
    }

    private static void configProxy(Properties properties) {
        String proxySet = properties.getProperty("proxySet");
        if ("true".equals(proxySet)) {
            System.getProperties().put("proxySet", "true");
            System.getProperties().put("proxyHost", properties.getProperty("proxyHost"));
            System.getProperties().put("proxyPort", properties.getProperty("proxyPort"));
        }
    }

    public static void setUser(ProUser proUser) {
        UserUtil.proUser = proUser;
    }

}
