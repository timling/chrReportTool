package com.promeritage.chtReportTool.dto;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import lombok.Data;

@Data
public class PersonalWorkDto {
    private String name;
    private List<JobDto> leaveWorkList = new ArrayList<JobDto>();
    private List<JobDto> workOverList = new ArrayList<JobDto>();
    private static final DateTimeFormatter forPattern = DateTimeFormat
            .forPattern("yyyy/MM/dd HH:mm");

    public int totalLeaveWorkHours() {
        return this.getTotalWorkHours(leaveWorkList);
    }

    public int totalWorkOverHours() {
        return this.getTotalWorkHours(workOverList);
    }

    private int getTotalWorkHours(List<JobDto> jobDtoList) {
        int totalHours = 0;
        for (JobDto jobDto : jobDtoList) {
            totalHours += countWorkHours(jobDto.getStartDate(), jobDto.getEndDate());
        }
        return totalHours;
    }

    public static int countWorkHours(String start, String end) {
        int workHours = 0;
        LocalDateTime startLocalDateTime = LocalDateTime.parse(start, forPattern);
        LocalDateTime endLocalDateTime = LocalDateTime.parse(end, forPattern);

        LocalDateTime withHourOfDay = startLocalDateTime.withHourOfDay(12);
        LocalDateTime withHourOfDay2 = endLocalDateTime.withHourOfDay(13);

        if (startLocalDateTime.isBefore(withHourOfDay) && endLocalDateTime.isAfter(withHourOfDay2)) {
            Period diffStart = new Period(startLocalDateTime, withHourOfDay);
            workHours += diffStart.getHours();

            Period diffEnd = new Period(withHourOfDay2, endLocalDateTime);
            workHours += diffEnd.getHours();
        } else {
            Period diff = new Period(startLocalDateTime, endLocalDateTime);
            workHours += diff.getHours();
        }
        return workHours;
    }
}
