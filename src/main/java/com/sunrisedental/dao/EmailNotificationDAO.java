package com.sunrisedental.dao;

import com.sunrisedental.config.DBConnectionManager;
import com.sunrisedental.model.EmailNotification;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object: EmailNotificationDAO
 */
public class EmailNotificationDAO {

    private static final Logger LOGGER = Logger.getLogger(EmailNotificationDAO.class.getName());
    private final DBConnectionManager dbManager = DBConnectionManager.getInstance();

    public boolean create(EmailNotification email) {
        String sql = "INSERT INTO email_outbox (recipient, subject, body, event_type, delivery_status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, email.getRecipient());
            ps.setString(2, email.getSubject());
            ps.setString(3, email.getBody());
            ps.setString(4, email.getEventType());
            ps.setString(5, email.getDeliveryStatus());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        email.setEmailId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving email notification", e);
        }
        return false;
    }

    public boolean updateStatus(int emailId, String status) {
        String sql = "UPDATE email_outbox SET delivery_status = ? WHERE email_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, emailId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating email status", e);
        }
        return false;
    }

    public List<EmailNotification> findRecent(int limit) {
        List<EmailNotification> list = new ArrayList<>();
        String sql = "SELECT * FROM email_outbox ORDER BY created_at DESC LIMIT ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error listing email notifications", e);
        }
        return list;
    }

    private EmailNotification mapRow(ResultSet rs) throws SQLException {
        EmailNotification e = new EmailNotification();
        e.setEmailId(rs.getInt("email_id"));
        e.setRecipient(rs.getString("recipient"));
        e.setSubject(rs.getString("subject"));
        e.setBody(rs.getString("body"));
        e.setEventType(rs.getString("event_type"));
        e.setDeliveryStatus(rs.getString("delivery_status"));
        e.setCreatedAt(rs.getTimestamp("created_at"));
        return e;
    }
}
