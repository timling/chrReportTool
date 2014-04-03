package com.promeritage.chtReportTool.dto;

import java.net.URL;

import lombok.Data;

@Data
public class ProUser {
    private String email;
    private String password;
    private URL workUrl;
    private URL workOverTimeUrl;
    private URL allWorkOverTimeUrl;
    private URL leaveWorkUrl;
    private URL allLeaveWorkUrl;
    private String name;
    private String pmName;
    private String dailyMailAddr;
    private String hrMailAddr;
}
