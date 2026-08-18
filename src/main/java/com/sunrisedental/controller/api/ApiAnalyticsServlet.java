package com.sunrisedental.controller.api;

import com.sunrisedental.model.MonthlyReportDTO;
import com.sunrisedental.service.ReportService;
import com.sunrisedental.service.factory.ServiceFactory;
import com.sunrisedental.util.DateUtil;
import com.sunrisedental.util.JsonUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * REST Web Service: ApiAnalyticsServlet
 * Endpoint: /api/analytics
 * Provides aggregated analytics data formatted for Chart.js / web dashboards.
 */
@WebServlet(name = "ApiAnalyticsServlet", urlPatterns = {"/api/analytics"})
public class ApiAnalyticsServlet extends HttpServlet {

    private final ReportService reportService = ServiceFactory.getReportService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String month = req.getParameter("month");
        if (month == null || month.trim().isEmpty()) {
            month = DateUtil.getCurrentYearMonth();
        }

        MonthlyReportDTO report = reportService.getMonthlyReport(month);

        List<String> treatLabels = new ArrayList<>();
        List<String> treatValues = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : report.getTreatmentsDistribution().entrySet()) {
            treatLabels.add("\"" + JsonUtil.escape(entry.getKey()) + "\"");
            treatValues.add(String.valueOf(entry.getValue()));
        }

        String dataJson = "{" +
            "\"period\":\"" + JsonUtil.escape(report.getPeriod()) + "\"," +
            "\"totalAppointments\":" + report.getTotalAppointments() + "," +
            "\"completedAppointments\":" + report.getCompletedAppointments() + "," +
            "\"cancelledAppointments\":" + report.getCancelledAppointments() + "," +
            "\"newPatients\":" + report.getNewPatientsRegistered() + "," +
            "\"totalInvoices\":" + report.getTotalInvoicesIssued() + "," +
            "\"grossConsultation\":" + report.getGrossConsultationIncome() + "," +
            "\"grossTreatment\":" + report.getGrossTreatmentIncome() + "," +
            "\"totalDiscounts\":" + report.getTotalDiscountsGranted() + "," +
            "\"netRevenue\":" + report.getTotalNetRevenue() + "," +
            "\"cashCollected\":" + report.getTotalCashCollected() + "," +
            "\"outstanding\":" + report.getOutstandingBalance() + "," +
            "\"treatmentLabels\":[" + String.join(",", treatLabels) + "]," +
            "\"treatmentValues\":[" + String.join(",", treatValues) + "]" +
            "}";

        resp.getWriter().write(JsonUtil.toJsonSuccess("Analytics retrieved", dataJson));
    }
}
