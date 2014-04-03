package com.promeritage.chtReportTool.manager;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.google.gdata.client.calendar.CalendarQuery;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.data.calendar.CalendarEventFeed;
import com.google.gdata.data.extensions.When;
import com.google.gdata.util.ServiceException;
import com.promeritage.chtReportTool.dto.JobDto;
import com.promeritage.chtReportTool.dto.ProUser;
import com.promeritage.chtReportTool.utils.CalendarUtil;
import com.promeritage.chtReportTool.utils.UserUtil;

public class CalendarManager {

    public static List<JobDto> queryEvents(URL feedUrl, String minStartTime, String maxStartTime)
            throws MalformedURLException, IOException, ServiceException {
        CalendarQuery myQuery = new CalendarQuery(feedUrl);
        myQuery.setMaxResults(200);
        myQuery.setMinimumStartTime(DateTime.parseDateTime(minStartTime));
        myQuery.setMaximumStartTime(DateTime.parseDateTime(maxStartTime));
        ProUser proUser = UserUtil.getUser();
        List<CalendarEventEntry> entries = CalendarUtil.getService(proUser)
                .query(myQuery, CalendarEventFeed.class).getEntries();

        return CalendarUtil.transferToJobDtoList(entries);
    }

    public static CalendarEventEntry createEvent(URL feedUrl, String title, String startTimeStr,
            String endTimeStr) throws MalformedURLException, IOException, ServiceException {
        CalendarEventEntry myEntry = new CalendarEventEntry();

        myEntry.setTitle(new PlainTextConstruct(title));
        DateTime startTime = DateTime.parseDateTime(startTimeStr);
        DateTime endTime = DateTime.parseDateTime(endTimeStr);
        When eventTimes = new When();
        eventTimes.setStartTime(startTime);
        eventTimes.setEndTime(endTime);
        myEntry.addTime(eventTimes);
        ProUser proUser = UserUtil.getUser();
        return CalendarUtil.getService(proUser).insert(feedUrl, myEntry);
    }

}
