package com.sunrisedental.dao;

import com.sunrisedental.config.DBConnectionManager;
import com.sunrisedental.model.MonthlyReportDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object: ReportDAO
 * Generates aggregated statistical and financial reports
 */
public class ReportDAO {

    private static final Logger LOGGER = Logger.getLogger(ReportDAO.class.getName());
    private final DBConnectionManager dbManager = DBConnectionManager.getInstance();

    public MonthlyReportDTO getMonthlyReport(String yearMonth) {
        MonthlyReportDTO dto = new MonthlyReportDTO();
        dto.setPeriod(yearMonth);

        // 1. Financial summary for the month
        String billSql = "SELECT " +
                         "COUNT(DISTINCT bill_id) AS total_bills, " +
                         "COALESCE(SUM(consultation_fee), 0) AS total_consult, " +
                         "COALESCE(SUM(treatment_cost), 0) AS total_treat, " +
                         "COALESCE(SUM(additional_charges), 0) AS total_add, " +
                         "COALESCE(SUM(discount_amount), 0) AS total_disc, " +
                         "COALESCE(SUM(tax_amount), 0) AS total_tax, " +
                         "COALESCE(SUM(total_amount), 0) AS total_net, " +
                         "COALESCE(SUM(paid_amount), 0) AS total_paid, " +
                         "COALESCE(SUM(balance_amount), 0) AS total_balance " +
                         "FROM bills WHERE DATE_FORMAT(created_at, '%Y-%m') = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(billSql)) {

            ps.setString(1, yearMonth);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    dto.setTotalInvoicesIssued(rs.getInt("total_bills"));
                    dto.setGrossConsultationIncome(rs.getDouble("total_consult"));
                    dto.setGrossTreatmentIncome(rs.getDouble("total_treat"));
                    dto.setGrossAdditionalCharges(rs.getDouble("total_add"));
                    dto.setTotalDiscountsGranted(rs.getDouble("total_disc"));
                    dto.setTotalTaxCollected(rs.getDouble("total_tax"));
                    dto.setTotalNetRevenue(rs.getDouble("total_net"));
                    dto.setTotalCashCollected(rs.getDouble("total_paid"));
                    dto.setOutstandingBalance(rs.getDouble("total_balance"));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error aggregating monthly bills for: " + yearMonth, e);
        }

        // 2. Appointment counts for the month
        String appSql = "SELECT " +
                        "COUNT(*) AS total_apps, " +
                        "SUM(CASE WHEN status = 'Completed' THEN 1 ELSE 0 END) AS completed_apps, " +
                        "SUM(CASE WHEN status = 'Cancelled' THEN 1 ELSE 0 END) AS cancelled_apps " +
                        "FROM appointments WHERE DATE_FORMAT(appointment_date, '%Y-%m') = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(appSql)) {

            ps.setString(1, yearMonth);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    dto.setTotalAppointments(rs.getInt("total_apps"));
                    dto.setCompletedAppointments(rs.getInt("completed_apps"));
                    dto.setCancelledAppointments(rs.getInt("cancelled_apps"));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error aggregating monthly appointments for: " + yearMonth, e);
        }

        // 3. New Patients registered in the month
        String patSql = "SELECT COUNT(*) FROM patients WHERE DATE_FORMAT(created_at, '%Y-%m') = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(patSql)) {

            ps.setString(1, yearMonth);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    dto.setNewPatientsRegistered(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error aggregating new patients for: " + yearMonth, e);
        }

        // 4. Treatment distribution
        String treatSql = "SELECT t.treatment_name, COUNT(a.appointment_id) AS cnt " +
                          "FROM appointments a JOIN treatments t ON a.treatment_id = t.treatment_id " +
                          "WHERE DATE_FORMAT(a.appointment_date, '%Y-%m') = ? " +
                          "GROUP BY t.treatment_name ORDER BY cnt DESC LIMIT 5";

        Map<String, Integer> treatMap = new HashMap<>();
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(treatSql)) {

            ps.setString(1, yearMonth);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    treatMap.put(rs.getString("treatment_name"), rs.getInt("cnt"));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error aggregating treatment stats for: " + yearMonth, e);
        }
        dto.setTreatmentsDistribution(treatMap);

        return dto;
    }
}
