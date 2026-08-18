package com.sunrisedental.controller;

import java.io.IOException;
import com.sunrisedental.model.MonthlyReportDTO;
import com.sunrisedental.service.ReportService;
import com.sunrisedental.service.factory.ServiceFactory;
import com.sunrisedental.util.DateUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "ReportServlet", urlPatterns = {"/reports/monthly", "/reports/annual"})
public class ReportServlet extends HttpServlet {

    private final ReportService reportService = ServiceFactory.getReportService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String month = req.getParameter("month");
        if (month == null || month.trim().isEmpty()) {
            month = DateUtil.getCurrentYearMonth();
        }

        MonthlyReportDTO report = reportService.getMonthlyReport(month.trim());
        req.setAttribute("report", report);
        req.setAttribute("selectedMonth", month);
        req.getRequestDispatcher("/admin_reports.jsp").forward(req, resp);
    }
}
