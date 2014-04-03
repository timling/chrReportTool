package com.promeritage.chtReportTool.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;

import com.promeritage.chtReportTool.dto.JobDto;
import com.promeritage.chtReportTool.dto.ProUser;
import com.promeritage.chtReportTool.manager.CalendarManager;
import com.promeritage.chtReportTool.utils.SendMailUtil;
import com.promeritage.chtReportTool.utils.UserUtil;

public class DailyReportController {

    public static void main(String[] args) throws Exception {
        LocalDate now = LocalDate.now();
        // now = now.withMonthOfYear(3);
        // now = now.withDayOfMonth(31);
        genReport(now);
    }

    public static void genReport(LocalDate now) throws Exception {
        ProUser proUser = UserUtil.getUser();
        String startDateStr = now.toString("yyyy-MM-dd'T'00:00:00");
        String endDateStr = now.toString("yyyy-MM-dd'T'23:59:59");
        List<JobDto> workList = CalendarManager.queryEvents(proUser.getWorkUrl(), startDateStr,
                endDateStr);
        if (workList.isEmpty()) {
            throw new RuntimeException(
                    "今日無工作內容, 請先在 https://www.google.com/calendar/render?tab=mc 設定工作內容");
        }

        Collections.sort(workList, new Comparator<JobDto>() {
            public int compare(JobDto o1, JobDto o2) {
                return o1.getStartDate().compareTo(o2.getStartDate());
            }
        });
        for (JobDto jobDto : workList) {
            LocalDateTime time = LocalDateTime.parse(jobDto.getEndDate(),
                    DateTimeFormat.forPattern("yyyy/MM/dd HH:mm"));
            jobDto.setEndDate(String.valueOf(time.getYear() - 1911) + time.toString("/MM/dd(如期)"));
        }

        for (int i = workList.size(); i < 5; i++) {
            workList.add(new JobDto());
        }

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("name", proUser.getName());
        parameters.put("date", String.valueOf(now.getYear() - 1911) + now.toString("年MM月dd日"));

        InputStream is = DailyReportController.class.getClassLoader().getResourceAsStream(
                "jrxml/dailyReport.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(is);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters,
                new JRBeanCollectionDataSource(workList));
        File file = new File(String.format("工作報告-(%s)%s.docx", proUser.getName(),
                now.toString("yyyyMMdd")));
        FileOutputStream outputStream = new FileOutputStream(file);

        JRDocxExporter docxExporter = new JRDocxExporter();
        docxExporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
        docxExporter.setParameter(JRExporterParameter.OUTPUT_STREAM, outputStream);
        docxExporter.exportReport();

        is.close();
        outputStream.close();

        String subject = String.format("工作日報 - %s %s",
                (String.valueOf(now.getYear() - 1911) + now.toString("/MM/dd")), proUser.getName());

        SendMailUtil.send(proUser.getEmail(), proUser.getPassword(), proUser.getDailyMailAddr(),
                subject, "", file);

        System.out.println("Done!");
    }
}
