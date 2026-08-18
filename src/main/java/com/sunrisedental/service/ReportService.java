package com.sunrisedental.service;

import com.sunrisedental.dao.ReportDAO;
import com.sunrisedental.model.MonthlyReportDTO;
import com.sunrisedental.util.DateUtil;

/**
 * Service: ReportService
 * Aggregates clinical, operational, and financial analytics for executive decision-making.
 */
public class ReportService {

    private final ReportDAO reportDAO = new ReportDAO();

    public MonthlyReportDTO getMonthlyReport(String yearMonth) {
        if (yearMonth == null || yearMonth.trim().isEmpty()) {
            yearMonth = DateUtil.getCurrentYearMonth();
        }
        return reportDAO.getMonthlyReport(yearMonth.trim());
    }
}
