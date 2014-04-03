package com.promeritage.chtReportTool.dto;

import lombok.Data;

@Data
public class JobDto {

    private int workNum = 1;
    private int num = 1;
    private int total = 1;
    private boolean workOverTime = false; // 是否為加班
    private String startDate;
    private String endDate;
    private String workContent;
    
    // 報表用
    private String group; // 跳頁用
    private String week;
    private String remark; 
    private String formatWorkContent;
    private String myName;
    private String masterName;
    
    public JobDto(){
    }
    
    public JobDto(String startDate, String week, String workContent, String remark) {
        this.startDate = startDate;
        this.week = week;
        this.workContent = workContent;
        this.remark = remark;
        this.endDate = startDate;
    }
}
