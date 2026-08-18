package com.sunrisedental.dao;

import com.sunrisedental.config.DBConnectionManager;
import com.sunrisedental.model.Payment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object: PaymentDAO
 */
public class PaymentDAO {

    private static final Logger LOGGER = Logger.getLogger(PaymentDAO.class.getName());
    private final DBConnectionManager dbManager = DBConnectionManager.getInstance();

    public List<Payment> findByBillId(int billId) {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT p.*, u.full_name AS cashier_name " +
                     "FROM payments p " +
                     "LEFT JOIN users u ON p.cashier_id = u.user_id " +
                     "WHERE p.bill_id = ? " +
                     "ORDER BY p.payment_date ASC";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, billId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapPayment(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error listing payments for bill: " + billId, e);
        }
        return list;
    }

    public List<Payment> findRecent(int limit) {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT p.*, u.full_name AS cashier_name " +
                     "FROM payments p " +
                     "LEFT JOIN users u ON p.cashier_id = u.user_id " +
                     "ORDER BY p.payment_date DESC LIMIT ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapPayment(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error listing recent payments", e);
        }
        return list;
    }

    public boolean create(Payment p) {
        if (p.getReceiptNumber() == null || p.getReceiptNumber().trim().isEmpty()) {
            p.setReceiptNumber(generateReceiptNumber());
        }

        String sql = "INSERT INTO payments (bill_id, receipt_number, amount, payment_method, cashier_id, transaction_reference, remarks) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, p.getBillId());
            ps.setString(2, p.getReceiptNumber());
            ps.setDouble(3, p.getAmount());
            ps.setString(4, p.getPaymentMethod());
            if (p.getCashierId() != null) {
                ps.setInt(5, p.getCashierId());
            } else {
                ps.setNull(5, Types.INTEGER);
            }
            ps.setString(6, p.getTransactionReference());
            ps.setString(7, p.getRemarks());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        p.setPaymentId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving payment for bill: " + p.getBillId(), e);
        }
        return false;
    }

    private String generateReceiptNumber() {
        String datePart = new java.text.SimpleDateFormat("yyyyMMdd").format(new java.util.Date());
        String sql = "SELECT MAX(payment_id) FROM payments";
        int nextId = 1;
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                nextId = rs.getInt(1) + 1;
            }
        } catch (SQLException ignored) {}
        return String.format("REC-%s-%04d", datePart, nextId);
    }

    private Payment mapPayment(ResultSet rs) throws SQLException {
        Payment p = new Payment();
        p.setPaymentId(rs.getInt("payment_id"));
        p.setBillId(rs.getInt("bill_id"));
        p.setReceiptNumber(rs.getString("receipt_number"));
        p.setPaymentDate(rs.getTimestamp("payment_date"));
        p.setAmount(rs.getDouble("amount"));
        p.setPaymentMethod(rs.getString("payment_method"));
        p.setCashierId((Integer) rs.getObject("cashier_id"));
        p.setCashierName(rs.getString("cashier_name"));
        p.setTransactionReference(rs.getString("transaction_reference"));
        p.setRemarks(rs.getString("remarks"));
        return p;
    }
}
