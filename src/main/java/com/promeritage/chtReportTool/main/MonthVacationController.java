package com.promeritage.chtReportTool.main;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.gdata.util.ServiceException;
import com.promeritage.chtReportTool.dto.JobDto;
import com.promeritage.chtReportTool.dto.PersonalWorkDto;
import com.promeritage.chtReportTool.dto.ProUser;
import com.promeritage.chtReportTool.manager.CalendarManager;
import com.promeritage.chtReportTool.utils.SendMailUtil;
import com.promeritage.chtReportTool.utils.UserUtil;

public class MonthVacationController {

    private static String[] nameList = new String[] { "凌鉅泰", "黃志偉", "馬秉宏", "吳彥霖", "林彥成" };

    public static void main(String[] args) throws MalformedURLException, IOException,
            ServiceException {

        LocalDateTime now = LocalDateTime.now();
        now = now.withMonthOfYear(3);
        sendVacationLog(now.dayOfMonth().withMinimumValue(), now.dayOfMonth().withMaximumValue());

    }

    public static void sendVacationLog(LocalDateTime start, LocalDateTime end)
            throws MalformedURLException, IOException, ServiceException {
        ProUser proUser = UserUtil.getUser();
        String startDay = start.toString(titleForPattern);
        String endDay = end.toString(titleForPattern); // "2014-03-31";
        List<JobDto> leaveWorkList = CalendarManager.queryEvents(proUser.getAllLeaveWorkUrl(),
                startDay + "T00:00:00", endDay + "T23:59:59");
        List<JobDto> workOverList = CalendarManager.queryEvents(proUser.getAllWorkOverTimeUrl(),
                startDay + "T00:00:00", endDay + "T23:59:59");

        List<PersonalWorkDto> personalWorkDtoList = configPersonWorkDto(leaveWorkList, workOverList);
        String msg = configMailMsg(personalWorkDtoList);

        String password = proUser.getPassword();
        String subject = String.format("%s ~ %s 假時統計", startDay, endDay);
        SendMailUtil.send(proUser.getEmail(), password, proUser.getHrMailAddr(), subject, msg);
    }

    private static String configMailMsg(List<PersonalWorkDto> personalWorkDtoList) {
        StringBuilder msg = new StringBuilder();
        for (PersonalWorkDto personalWorkDto : personalWorkDtoList) {
            msg.append(String.format("=== %s ===<br>", personalWorkDto.getName()));
            msg.append(String.format("請假共<span style='color:rgb(255,0,0)'>%d</span>小時",
                    personalWorkDto.totalLeaveWorkHours()));
            int totalWorkOverHours = personalWorkDto.totalWorkOverHours();
            if (totalWorkOverHours > 0) {
                msg.append(String.format(", 加班共<span style='color:rgb(0,0,255)'>%d</span>小時",
                        totalWorkOverHours));
            }
            msg.append("<br>");

            List<JobDto> leaveWorkList = personalWorkDto.getLeaveWorkList();
            List<JobDto> workOverList = personalWorkDto.getWorkOverList();
            if (!leaveWorkList.isEmpty() || !workOverList.isEmpty()) {
                msg.append("<br>詳細明細：<br>");
            }
            if (!leaveWorkList.isEmpty()) {
                for (JobDto leaveWork : leaveWorkList) {
                    msg.append(String.format(
                            "請假：%s %s~%s 共<span style='color:rgb(255,0,0)'>%d</span>小時<br>",
                            getDate(leaveWork.getStartDate()),
                            getTime(leaveWork.getStartDate()),
                            getTime(leaveWork.getEndDate()),
                            PersonalWorkDto.countWorkHours(leaveWork.getStartDate(),
                                    leaveWork.getEndDate())));
                }
            }

            if (!workOverList.isEmpty()) {
                msg.append("<br>");
                for (JobDto leaveWork : workOverList) {
                    msg.append(String.format(
                            "加班：%s %s~%s 共<span style='color:rgb(0,0,255)'>%d</span>小時<br>",
                            getDate(leaveWork.getStartDate()),
                            getTime(leaveWork.getStartDate()),
                            getTime(leaveWork.getEndDate()),
                            PersonalWorkDto.countWorkHours(leaveWork.getStartDate(),
                                    leaveWork.getEndDate())));
                }
            }
            msg.append("<br><br>");
        }
        return msg.toString();
    }

    private static List<PersonalWorkDto> configPersonWorkDto(List<JobDto> leaveWorkList,
            List<JobDto> workOverList) {
        List<PersonalWorkDto> personalWorkDtoList = new ArrayList<PersonalWorkDto>();
        for (String name : nameList) {
            PersonalWorkDto personalWorkDto = new PersonalWorkDto();
            personalWorkDto.setName(name);

            List<JobDto> personLeaveWorkList = personalWorkDto.getLeaveWorkList();
            for (JobDto jobDto : leaveWorkList) {
                if (name.equals(jobDto.getWorkContent())) {
                    personLeaveWorkList.add(jobDto);
                }
            }
            List<JobDto> personWorkOverList = personalWorkDto.getWorkOverList();
            for (JobDto jobDto : workOverList) {
                if (name.equals(jobDto.getWorkContent())) {
                    personWorkOverList.add(jobDto);
                }
            }
            personalWorkDtoList.add(personalWorkDto);
        }
        return personalWorkDtoList;
    }

    private static DateTimeFormatter titleForPattern = DateTimeFormat.forPattern("yyyy-MM-dd");
    private static DateTimeFormatter forPattern = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm");

    private static String getDate(String str) {
        LocalDateTime startLocalDateTime = LocalDateTime.parse(str, forPattern);
        return startLocalDateTime.toString(DateTimeFormat.forPattern("MM月dd日"));
    }

    private static String getTime(String str) {
        LocalDateTime startLocalDateTime = LocalDateTime.parse(str, forPattern);
        return startLocalDateTime.toString(DateTimeFormat.forPattern("HH:mm"));
    }

}
