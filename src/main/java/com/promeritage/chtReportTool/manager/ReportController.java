package com.promeritage.chtReportTool.manager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;

import org.joda.time.LocalDate;

import com.google.gdata.util.ServiceException;
import com.promeritage.chtReportTool.dto.JobDto;
import com.promeritage.chtReportTool.dto.ProUser;
import com.promeritage.chtReportTool.utils.PropertiesUtil;
import com.promeritage.chtReportTool.utils.UserUtil;

public class ReportController {

    /**
     * @param args
     * @throws JRException
     * @throws IOException
     * @throws ServiceException
     */
    public static void main(String[] args) throws Exception {
        LocalDate now = LocalDate.now();
        now = now.withMonthOfYear(3);
        genMonthReport(now.dayOfMonth().withMinimumValue(), now.dayOfMonth().withMaximumValue());
    }

    public static void genMonthReport(LocalDate startDate, LocalDate endDate) throws Exception {
        ProUser proUser = UserUtil.getUser();
        String startDateStr = startDate.toString("yyyy-MM-dd'T'00:00:00");
        String endDateStr = endDate.toString("yyyy-MM-dd'T'23:59:59");
        List<JobDto> workList = CalendarManager.queryEvents(proUser.getWorkUrl(), startDateStr,
                endDateStr);

        Collections.sort(workList, new Comparator<JobDto>() {
            public int compare(JobDto o1, JobDto o2) {
                return o1.getStartDate().compareTo(o2.getStartDate());
            }
        });

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("name", proUser.getName());
        parameters.put("pmName", proUser.getPmName());
        parameters.put("year", String.valueOf(startDate.getYear() - 1911));
        parameters.put("mounth", startDate.toString("MM"));

        JasperReport jasperReport = JasperCompileManager
                .compileReport("src/main/resources/jrxml/monthReport.jrxml");
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters,
                new JRBeanCollectionDataSource(workList));
        File file = new File(String.format("%s_%s_工作進度審查表_%s.docx",
                startDate.toString("yyyy-MM-dd"),
                endDate.toString("yyyy-MM-dd"), proUser.getName()));
        FileOutputStream outputStream = new FileOutputStream(file);

        JRDocxExporter docxExporter = new JRDocxExporter();
        docxExporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
        docxExporter.setParameter(JRExporterParameter.OUTPUT_STREAM, outputStream);
        docxExporter.exportReport();
        System.out.println("Done!");
    }

    public static void genReport1(String startDate, String endDate) throws Exception {
        long start = System.currentTimeMillis();
        ProUser proUser = UserUtil.getUser();
        List<JobDto> workList = CalendarManager.queryEvents(proUser.getWorkUrl(), startDate
                + "T00:00:00", endDate + "T23:59:59");
        List<JobDto> workOverTimeList = CalendarManager.queryEvents(proUser.getWorkOverTimeUrl(),
                startDate + "T00:00:00", endDate + "T23:59:59");
        for (JobDto jobDto : workOverTimeList) {
            jobDto.setWorkOverTime(true);
        }
        workList.addAll(workOverTimeList);

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("startDate", startDate.replace("-", "/"));
        parameters.put("endDate", endDate.replace("-", "/"));

        Properties properties = PropertiesUtil.loadProperties(PropertiesUtil.POPERTIES_NAME);
        parameters.put("myName", UserUtil.getUser().getName() != null ? UserUtil.getUser()
                .getName() : PropertiesUtil.getConfig(properties, "name"));
        parameters.put("masterName", UserUtil.getUser().getPmName() != null ? UserUtil.getUser()
                .getPmName() : PropertiesUtil.getConfig(properties, "masterName"));
        // JasperReport jasperReport =
        // JasperCompileManager.compileReport("src/main/jasperreports/report1.jrxml");
        JasperPrint jasperPrint = JasperFillManager.fillReport("generated-jasper/report1.jasper",
                parameters,
                new JRBeanCollectionDataSource(ReportManager.getBeanCollection(workList)));
        File file = new File("report.docx");
        FileOutputStream outputStream = new FileOutputStream(file);

        JRDocxExporter docxExporter = new JRDocxExporter();
        docxExporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
        docxExporter.setParameter(JRExporterParameter.OUTPUT_STREAM, outputStream);
        docxExporter.exportReport();
        System.err.println("Filling time : " + (System.currentTimeMillis() - start));
    }

    public static void genReport2(String startDate, String endDate) throws Exception {

        long start = System.currentTimeMillis();

        Properties properties = PropertiesUtil.loadProperties(PropertiesUtil.POPERTIES_NAME);
        String name = PropertiesUtil.getConfig(properties, "name");
        String masterName = PropertiesUtil.getConfig(properties, "masterName");
        ProUser proUser = UserUtil.getUser();
        List<JobDto> workList = CalendarManager.queryEvents(proUser.getWorkUrl(), startDate
                + "T00:00:00", endDate + "T23:59:59");
        for (JobDto customBean : workList) {
            customBean.setMyName(name);
            customBean.setMasterName(masterName);
        }
        List<JobDto> workOverTimeList = CalendarManager.queryEvents(proUser.getWorkOverTimeUrl(),
                startDate + "T00:00:00", endDate + "T23:59:59");
        for (JobDto customBean : workOverTimeList) {
            customBean.setMyName(name);
            customBean.setMasterName(masterName);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(sdf.parse(startDate));

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("startDate", startDate.replace("-", "/"));
        parameters.put("endDate", endDate.replace("-", "/"));
        parameters.put("parameter1",
                new JRBeanCollectionDataSource(ReportManager.getBeanCollection2(workList)));
        parameters.put("parameter2",
                new JRBeanCollectionDataSource(ReportManager.getBeanCollection2(workOverTimeList)));

        parameters.put("SUBREPORT_DIR", System.getProperty("user.dir") + "/generated-jasper/");

        parameters.put("reportYear", String.valueOf(startCalendar.get(Calendar.YEAR) - 1911));
        parameters.put("reportMonth", String.valueOf(startCalendar.get(Calendar.MONTH) + 1));
        startCalendar.add(Calendar.MONTH, -1);
        parameters.put("preReportMonth", String.valueOf(startCalendar.get(Calendar.MONTH) + 1));

        parameters.put("myName", name);
        parameters.put("masterName", masterName);
        // JasperReport jasperReport =
        // JasperCompileManager.compileReport("src/main/jasperreports/report2.jrxml");
        JasperPrint jasperPrint = JasperFillManager.fillReport("generated-jasper/report2.jasper",
                parameters, new JREmptyDataSource(1));

        File file = new File("report2.docx");
        FileOutputStream outputStream = new FileOutputStream(file);

        JRDocxExporter docxExporter = new JRDocxExporter();
        docxExporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
        docxExporter.setParameter(JRExporterParameter.OUTPUT_STREAM, outputStream);
        docxExporter.exportReport();
        System.err.println("Filling time : " + (System.currentTimeMillis() - start));
    }

}
