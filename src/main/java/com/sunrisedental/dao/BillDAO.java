package com.sunrisedental.dao;

import com.sunrisedental.config.DBConnectionManager;
import com.sunrisedental.model.Bill;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object: BillDAO
 */
public class BillDAO {

    private static final Logger LOGGER = Logger.getLogger(BillDAO.class.getName());
    private final DBConnectionManager dbManager = DBConnectionManager.getInstance();

    private static final String BASE_SELECT = 
        "SELECT b.*, " +
        "p.patient_code, p.full_name AS patient_name, p.phone AS patient_phone, p.email AS patient_email, p.address AS patient_address, " +
        "u.full_name AS doctor_name, t.treatment_name, a.appointment_number " +
        "FROM bills b " +
        "JOIN appointments a ON b.appointment_id = a.appointment_id " +
        "JOIN patients p ON b.patient_id = p.patient_id " +
        "JOIN doctors d ON a.doctor_id = d.doctor_id " +
        "JOIN users u ON d.user_id = u.user_id " +
        "JOIN treatments t ON a.treatment_id = t.treatment_id ";

    public Bill findById(int billId) {
        String sql = BASE_SELECT + "WHERE b.bill_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, billId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapBill(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding bill by ID: " + billId, e);
        }
        return null;
    }

    public Bill findByInvoiceNumber(String invoiceNumber) {
        String sql = BASE_SELECT + "WHERE b.invoice_number = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, invoiceNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapBill(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding bill by invoice number: " + invoiceNumber, e);
        }
        return null;
    }

    public Bill findByAppointmentId(int appointmentId) {
        String sql = BASE_SELECT + "WHERE b.appointment_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, appointmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapBill(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding bill by appointment ID: " + appointmentId, e);
        }
        return null;
    }

    public List<Bill> findAllRecent() {
        List<Bill> list = new ArrayList<>();
        String sql = BASE_SELECT + "ORDER BY b.bill_id DESC LIMIT 100";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapBill(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error listing recent bills", e);
        }
        return list;
    }

    public List<Bill> findUnpaidOrPartial() {
        List<Bill> list = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE b.payment_status IN ('Unpaid', 'Partially Paid') ORDER BY b.bill_id DESC";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapBill(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error listing unpaid bills", e);
        }
        return list;
    }

    public boolean create(Bill b) {
        if (b.getInvoiceNumber() == null || b.getInvoiceNumber().trim().isEmpty()) {
            b.setInvoiceNumber(generateInvoiceNumber());
        }

        String sql = "INSERT INTO bills (invoice_number, appointment_id, patient_id, consultation_fee, " +
                     "treatment_cost, additional_charges, discount_type, discount_percentage, discount_amount, " +
                     "tax_percentage, tax_amount, total_amount, paid_amount, balance_amount, payment_status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, b.getInvoiceNumber());
            ps.setInt(2, b.getAppointmentId());
            ps.setInt(3, b.getPatientId());
            ps.setDouble(4, b.getConsultationFee());
            ps.setDouble(5, b.getTreatmentCost());
            ps.setDouble(6, b.getAdditionalCharges());
            ps.setString(7, b.getDiscountType() != null ? b.getDiscountType() : "Standard");
            ps.setDouble(8, b.getDiscountPercentage());
            ps.setDouble(9, b.getDiscountAmount());
            ps.setDouble(10, b.getTaxPercentage());
            ps.setDouble(11, b.getTaxAmount());
            ps.setDouble(12, b.getTotalAmount());
            ps.setDouble(13, b.getPaidAmount());
            ps.setDouble(14, b.getBalanceAmount());
            ps.setString(15, b.getPaymentStatus() != null ? b.getPaymentStatus() : Bill.STATUS_UNPAID);

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        b.setBillId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating bill for appointment: " + b.getAppointmentId(), e);
        }
        return false;
    }

    public boolean updatePayment(int billId, double additionalPaid) {
        String sql = "UPDATE bills SET paid_amount = paid_amount + ?, " +
                     "balance_amount = total_amount - (paid_amount + ?), " +
                     "payment_status = CASE WHEN (paid_amount + ?) >= total_amount THEN 'Paid' ELSE 'Partially Paid' END " +
                     "WHERE bill_id = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, additionalPaid);
            ps.setDouble(2, additionalPaid);
            ps.setDouble(3, additionalPaid);
            ps.setInt(4, billId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating payment for bill: " + billId, e);
        }
        return false;
    }

    public double getTodayRevenue() {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM payments WHERE DATE(payment_date) = CURDATE()";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException ignored) {}
        return 0.0;
    }

    public double getMonthlyRevenue(String yearMonth) {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM payments WHERE DATE_FORMAT(payment_date, '%Y-%m') = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, yearMonth);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        } catch (SQLException ignored) {}
        return 0.0;
    }

    private String generateInvoiceNumber() {
        String datePart = new java.text.SimpleDateFormat("yyyyMMdd").format(new java.util.Date());
        String sql = "SELECT MAX(bill_id) FROM bills";
        int nextId = 1;
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                nextId = rs.getInt(1) + 1;
            }
        } catch (SQLException ignored) {}
        return String.format("INV-%s-%04d", datePart, nextId);
    }

    private Bill mapBill(ResultSet rs) throws SQLException {
        Bill b = new Bill();
        b.setBillId(rs.getInt("bill_id"));
        b.setInvoiceNumber(rs.getString("invoice_number"));
        b.setAppointmentId(rs.getInt("appointment_id"));
        b.setPatientId(rs.getInt("patient_id"));
        b.setConsultationFee(rs.getDouble("consultation_fee"));
        b.setTreatmentCost(rs.getDouble("treatment_cost"));
        b.setAdditionalCharges(rs.getDouble("additional_charges"));
        b.setDiscountType(rs.getString("discount_type"));
        b.setDiscountPercentage(rs.getDouble("discount_percentage"));
        b.setDiscountAmount(rs.getDouble("discount_amount"));
        b.setTaxPercentage(rs.getDouble("tax_percentage"));
        b.setTaxAmount(rs.getDouble("tax_amount"));
        b.setTotalAmount(rs.getDouble("total_amount"));
        b.setPaidAmount(rs.getDouble("paid_amount"));
        b.setBalanceAmount(rs.getDouble("balance_amount"));
        b.setPaymentStatus(rs.getString("payment_status"));
        b.setCreatedAt(rs.getTimestamp("created_at"));
        b.setUpdatedAt(rs.getTimestamp("updated_at"));

        b.setPatientCode(rs.getString("patient_code"));
        b.setPatientName(rs.getString("patient_name"));
        b.setPatientPhone(rs.getString("patient_phone"));
        b.setPatientEmail(rs.getString("patient_email"));
        b.setPatientAddress(rs.getString("patient_address"));
        b.setDoctorName(rs.getString("doctor_name"));
        b.setTreatmentName(rs.getString("treatment_name"));
        b.setAppointmentNumber(rs.getString("appointment_number"));

        return b;
    }
}
