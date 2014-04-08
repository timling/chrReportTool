package com.promeritage.chtReportTool.utils;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.calendar.CalendarEntry;
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.data.calendar.CalendarFeed;
import com.google.gdata.util.AuthenticationException;
import com.promeritage.chtReportTool.dto.JobDto;
import com.promeritage.chtReportTool.dto.ProUser;

public class CalendarUtil {
    private static CalendarService service = null;

    public static CalendarService getService(ProUser proUser) throws AuthenticationException {
        try {
            if (service == null) {
                service = new CalendarService("My Application");
                service.setUserCredentials(proUser.getEmail(), proUser.getPassword());
                setCalendarUrls(service, proUser);
            }
        } catch (AuthenticationException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        return service;
    }

    public static void setCalendarUrls(CalendarService service, ProUser proUser) {
        try {
            URL feedUrl = new URL("https://www.google.com/calendar/feeds/default/owncalendars/full");
            CalendarFeed resultFeed = service.getFeed(feedUrl, CalendarFeed.class);

            for (int i = 0; i < resultFeed.getEntries().size(); i++) {
                CalendarEntry entry = resultFeed.getEntries().get(i);
                String calendarName = entry.getTitle().getPlainText();
                URL configUrl = new URL(entry.getId().replace("default/calendars/", "")
                        + "/private/full");
                if ("普瑞德in中華電信".equals(calendarName)) {
                    proUser.setWorkUrl(configUrl);
                } else if ("普瑞德in中華電信-加班".equals(calendarName)) {
                    proUser.setWorkOverTimeUrl(configUrl);
                } else if ("普瑞德in中華電信-請假".equals(calendarName)) {
                    proUser.setLeaveWorkUrl(configUrl);
                } else if ("全體請假紀錄".equals(calendarName)) {
                    proUser.setAllLeaveWorkUrl(configUrl);
                } else if ("全體加班紀錄".equals(calendarName)) {
                    proUser.setAllWorkOverTimeUrl(configUrl);
                }
            }

            if (proUser.getWorkUrl() == null) {
                proUser.setWorkUrl(createCalendar(service, "普瑞德in中華電信"));
            }
            if (proUser.getWorkOverTimeUrl() == null) {
                proUser.setWorkOverTimeUrl(createCalendar(service, "普瑞德in中華電信-加班"));
            }
            if (proUser.getLeaveWorkUrl() == null) {
                proUser.setLeaveWorkUrl(createCalendar(service, "普瑞德in中華電信-請假"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static URL createCalendar(CalendarService service, String calendarName) {
        CalendarEntry calendar = new CalendarEntry();
        calendar.setTitle(new PlainTextConstruct(calendarName));
        try {
            // Insert the calendar
            URL postUrl = new URL("https://www.google.com/calendar/feeds/default/owncalendars/full");
            CalendarEntry returnedCalendar = service.insert(postUrl, calendar);
            return new URL(returnedCalendar.getId().replace("default/calendars/", "")
                    + "/private/full");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static SimpleDateFormat calendarUse = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat calendarUse2 = new SimpleDateFormat("HH:mm:ss");

    public static String getDateTime(Calendar c) {
        return calendarUse.format(c.getTime()) + "T" + calendarUse2.format(c.getTime());
    }

    public static String getWorkStartDateTime(Calendar c) {
        return calendarUse.format(c.getTime()) + "T09:00:00";
    }

    public static String getWorkEndDateTime(Calendar c) {
        return calendarUse.format(c.getTime()) + "T18:00:00";
    }

    public static String getWorkOverStartDateTime(Calendar c) {
        String time = null;
        switch (c.get(Calendar.DAY_OF_WEEK)) {
        case Calendar.SUNDAY:
        case Calendar.SATURDAY:
            time = "T09:00:00";
            break;
        default:
            time = "T18:00:00";
        }
        return calendarUse.format(c.getTime()) + time;
    }

    public static String getWorkOverEndDateTime(Calendar c) {
        String time = null;
        switch (c.get(Calendar.DAY_OF_WEEK)) {
        case Calendar.SUNDAY:
        case Calendar.SATURDAY:
            time = "T18:00:00";
            break;
        default:
            time = "T22:00:00";
        }
        return calendarUse.format(c.getTime()) + time;
    }

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");

    public static String getChineseDayOfWeek(LocalDateTime rightNow) {
        String chineseDayOfWeek = null;

        switch (rightNow.getDayOfWeek()) {
        case DateTimeConstants.SUNDAY:
            chineseDayOfWeek = "日";
            break;
        case DateTimeConstants.MONDAY:
            chineseDayOfWeek = "一";
            break;
        case DateTimeConstants.TUESDAY:
            chineseDayOfWeek = "二";
            break;
        case DateTimeConstants.WEDNESDAY:
            chineseDayOfWeek = "三";
            break;
        case DateTimeConstants.THURSDAY:
            chineseDayOfWeek = "四";
            break;
        case DateTimeConstants.FRIDAY:
            chineseDayOfWeek = "五";
            break;
        case DateTimeConstants.SATURDAY:
            chineseDayOfWeek = "六";
            break;
        }

        return chineseDayOfWeek;
    }

    public static List<JobDto> transferToJobDtoList(List<CalendarEventEntry> entries) {
        List<JobDto> customBeanList = new ArrayList<JobDto>();
        for (CalendarEventEntry entry : entries) {
            LocalDateTime startDate = new LocalDateTime(entry.getTimes().get(0).getStartTime()
                    .getValue());
            LocalDateTime endDate = new LocalDateTime(entry.getTimes().get(0).getEndTime()
                    .getValue());

            Period p = new Period(startDate, endDate, PeriodType.days());
            if (p.getDays() > 0) {
                startDate = startDate.withHourOfDay(8);
                startDate = startDate.withMinuteOfHour(30);

                endDate = endDate.minusDays(1);
                endDate = endDate.withHourOfDay(17);
                endDate = endDate.withMinuteOfHour(30);
            }

            JobDto bean = new JobDto();
            bean.setStartDate(sdf.format(startDate.toDate()));
            bean.setEndDate(sdf.format(endDate.toDate()));
            bean.setWeek(getChineseDayOfWeek(startDate));
            bean.setWorkContent(entry.getTitle().getPlainText());
            customBeanList.add(bean);
        }
        return customBeanList;
    }

    public static Calendar toCalendar(DateTime dateTime) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(dateTime.getValue());
        // c.set(Calendar.YEAR, dateTime.getYear());
        // c.set(Calendar.MONTH, dateTime.getMonth());
        // c.set(Calendar.DAY_OF_MONTH, dateTime.getDay());
        return c;
    }

}
