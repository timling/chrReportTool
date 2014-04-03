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

import com.promeritage.chtReportTool.dto.JobDto;
import com.promeritage.chtReportTool.dto.ProUser;
import com.promeritage.chtReportTool.manager.CalendarManager;
import com.promeritage.chtReportTool.utils.UserUtil;

public class MonthReportController {

    public static void main(String[] args) throws Exception {
        LocalDate now = LocalDate.now();
        // now = now.withMonthOfYear(3);
        genMonthReport(now.dayOfMonth().withMinimumValue(), now.dayOfMonth().withMaximumValue());
    }

    public static void genMonthReport(LocalDate startDate, LocalDate endDate) throws Exception {
        ProUser proUser = UserUtil.getUser();
        String startDateStr = startDate.toString("yyyy-MM-dd'T'00:00:00");
        String endDateStr = endDate.toString("yyyy-MM-dd'T'23:59:59");
        List<JobDto> workList = CalendarManager.queryEvents(proUser.getWorkUrl(), startDateStr,
                endDateStr);
        if (workList.isEmpty()) {
            throw new RuntimeException(String.format("%s~%s沒有工作內容, 請至 calendar 補登",
                    startDate.toString("yyyy/MM/dd"), endDate.toString("yyyy/MM/dd")));
        }

        Collections.sort(workList, new Comparator<JobDto>() {
            public int compare(JobDto o1, JobDto o2) {
                return o1.getStartDate().compareTo(o2.getStartDate());
            }
        });

        int size = 25 - (workList.size() % 25);
        for (int i = 0; i < size; i++) {
            workList.add(new JobDto());
        }

        int group = 0;
        int i = 0;
        for (JobDto jobDto : workList) {
            if (i % 25 == 0) {
                group++;
            }
            jobDto.setGroup(String.valueOf(group));

            if (jobDto.getWorkContent() != null) {
                jobDto.setMyName(proUser.getName());
            }
            i++;
        }

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("name", proUser.getName());
        parameters.put("pmName", proUser.getPmName());
        parameters.put("year", String.valueOf(startDate.getYear() - 1911));
        parameters.put("mounth", startDate.toString("MM"));

        InputStream is = DailyReportController.class.getClassLoader().getResourceAsStream(
                "jrxml/monthReport.jrxml");

        JasperReport jasperReport = JasperCompileManager.compileReport(is);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters,
                new JRBeanCollectionDataSource(workList));
        File file = new File(
                String.format("%s_%s_工作進度審查表_%s.docx", startDate.toString("yyyy-MM-dd"),
                        endDate.toString("yyyy-MM-dd"), proUser.getName()));
        FileOutputStream outputStream = new FileOutputStream(file);

        JRDocxExporter docxExporter = new JRDocxExporter();
        docxExporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
        docxExporter.setParameter(JRExporterParameter.OUTPUT_STREAM, outputStream);
        docxExporter.exportReport();

        is.close();
        outputStream.close();

        System.out.println("Done!");
    }
}
