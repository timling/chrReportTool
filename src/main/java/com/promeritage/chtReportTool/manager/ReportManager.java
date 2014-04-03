package com.promeritage.chtReportTool.manager;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.promeritage.chtReportTool.dto.JobDto;

public class ReportManager {
    // private static CustomBean[] data = {
    // new CustomBean("10/01", "日", "做很多事情", null),
    // new CustomBean("10/01", "日", "做很多事情1", null),
    // new CustomBean("10/02", "一", "做很多事情", null),
    // new CustomBean("10/02", "一", "做很多事情1", null),
    // new CustomBean("10/03", "二", "做很多事情", null),
    // new CustomBean("10/04", "三", "做很多事情", null),
    // new CustomBean("10/05", "四", "做很多事情1", null) };

    public static final String WORK_CONTENT_TEMPLATE = "工作${workNum}:\n"
        + "(1) 工作內容 (進度%)：${workContent}(${percentage}%)\n" + "(2) 預定完成日/執行情形(落後/如期/超前)：${endDate}(如期)\n"
        + "(3) 困難與議題(需主管協助解決問題)：無\n";

    public static String getReportFormatWorkContent(JobDto customBean) {
    String template = new String(WORK_CONTENT_TEMPLATE);
    String workNum = String.valueOf(customBean.getWorkNum());
    String percentage = String.valueOf(Math.round((customBean.getNum() * 100 / customBean.getTotal())));
    return template.replace("${workNum}", workNum).replace("${workContent}", customBean.getWorkContent())
            .replace("${percentage}", percentage).replace("${endDate}", customBean.getEndDate());
    }

    public static Collection<JobDto> getBeanCollection(List<JobDto> jobDtoList) {

    // List<CustomBean> result = new
    // ArrayList<CustomBean>(Arrays.asList(data));
    List<JobDto> result = jobDtoList;
    if (result.isEmpty()) {
        result.add(new JobDto());
        return result;
    }

    Collections.sort(result, new Comparator<JobDto>() {
        public int compare(JobDto o1, JobDto o2) {
        if (o1.getWorkContent().equals(o2.getWorkContent())) {
            return o1.getStartDate().compareTo(o2.getStartDate());
        } else {
            return o1.getWorkContent().compareTo(o2.getWorkContent());
        }
        }
    });
    for (int i = 0; i < result.size(); i++) {
        String workContent = result.get(i).getWorkContent();
        if (i != 0) {
        if (workContent.equals(result.get(i - 1).getWorkContent())) {
            result.get(i).setNum(result.get(i - 1).getNum() + 1);
            result.get(i).setTotal(result.get(i - 1).getTotal() + 1);
        }
        }
    }

    String workContent = result.get(result.size() - 1).getWorkContent();
    int total = result.get(result.size() - 1).getTotal();
    String endDate = result.get(result.size() - 1).getEndDate();
    for (int i = result.size() - 1; i >= 0; i--) {
        if (!workContent.equals(result.get(i).getWorkContent())) {
        workContent = result.get(i).getWorkContent();
        total = result.get(i).getTotal();
        endDate = result.get(i).getEndDate();
        continue;
        }
        result.get(i).setTotal(total);
        result.get(i).setEndDate(endDate);
    }

    Collections.sort(result, new Comparator<JobDto>() {
        public int compare(JobDto o1, JobDto o2) {
        if (o1.getStartDate().equals(o2.getStartDate())) {
            if (o1.isWorkOverTime()) {
            return 1;
            } else {
            return -1;
            }
        }
        return o1.getStartDate().compareTo(o2.getStartDate());
        }
    });
    // 同天工作+1
    for (int i = 0; i < result.size(); i++) {
        String startDate = result.get(i).getStartDate();
        if (i != 0) {
        if (startDate.equals(result.get(i - 1).getStartDate())) {
            result.get(i).setWorkNum(result.get(i - 1).getWorkNum() + 1);
        }
        }
    }

    StringBuffer tmp = new StringBuffer();
    for (int i = result.size() - 1; i >= 0; i--) {
        tmp.insert(0, getReportFormatWorkContent(result.get(i)));
        if (result.get(i).getWorkNum() > 1) {
        result.remove(i);
        } else {
        result.get(i).setFormatWorkContent(tmp.toString());
        tmp.setLength(0);
        }
    }

    return result;
    }

    public static Collection<JobDto> getBeanCollection2(List<JobDto> jobDtoList) {

    // List<CustomBean> result = new
    // ArrayList<CustomBean>(Arrays.asList(data));
    List<JobDto> result = jobDtoList;
    if (result.isEmpty()) {
        result.add(new JobDto());
        return result;
    }

    Collections.sort(result, new Comparator<JobDto>() {
        public int compare(JobDto o1, JobDto o2) {
        if (o1.getWorkContent().equals(o2.getWorkContent())) {
            return o1.getStartDate().compareTo(o2.getStartDate());
        } else {
            return o1.getWorkContent().compareTo(o2.getWorkContent());
        }
        }
    });

    String workContent = result.get(result.size() - 1).getWorkContent();
    String endDate = result.get(result.size() - 1).getEndDate();
    for (int i = result.size() - 2; i >= 0; i--) {
        if (!workContent.equals(result.get(i).getWorkContent())) {
        workContent = result.get(i).getWorkContent();
        endDate = result.get(i).getEndDate();
        continue;
        }
        result.get(i).setEndDate(endDate);
    }

    // for(JobDto JobDto : result){
    // System.out.println(JobDto);
    // }
    workContent = result.get(result.size() - 1).getWorkContent();
    for (int i = result.size() - 2; i >= 0; i--) {
        if (!workContent.equals(result.get(i).getWorkContent())) {
        workContent = result.get(i).getWorkContent();
        continue;
        }
        result.remove(i + 1);
    }

    Collections.sort(result, new Comparator<JobDto>() {
        public int compare(JobDto o1, JobDto o2) {
        return o1.getStartDate().compareTo(o2.getStartDate());
        }
    });

    return result;
    }
}
