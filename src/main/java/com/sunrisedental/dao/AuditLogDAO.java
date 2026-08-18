package com.sunrisedental.dao;

import com.sunrisedental.config.DBConnectionManager;
import com.sunrisedental.model.AuditLog;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object: AuditLogDAO
 */
public class AuditLogDAO {

    private static final Logger LOGGER = Logger.getLogger(AuditLogDAO.class.getName());
    private final DBConnectionManager dbManager = DBConnectionManager.getInstance();

    public void log(Integer userId, String action, String entityName, String entityId, String details, String ipAddress) {
        String sql = "INSERT INTO audit_logs (user_id, action, entity_name, entity_id, details, ip_address) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (userId != null) {
                ps.setInt(1, userId);
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            ps.setString(2, action);
            ps.setString(3, entityName);
            ps.setString(4, entityId);
            ps.setString(5, details);
            ps.setString(6, ipAddress);

            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Failed to write audit log: " + action, e);
        }
    }

    public List<AuditLog> findRecent(int limit) {
        List<AuditLog> list = new ArrayList<>();
        String sql = "SELECT l.*, u.username " +
                     "FROM audit_logs l " +
                     "LEFT JOIN users u ON l.user_id = u.user_id " +
                     "ORDER BY l.log_id DESC LIMIT ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AuditLog log = new AuditLog();
                    log.setLogId(rs.getInt("log_id"));
                    log.setUserId((Integer) rs.getObject("user_id"));
                    log.setUsername(rs.getString("username"));
                    log.setAction(rs.getString("action"));
                    log.setEntityName(rs.getString("entity_name"));
                    log.setEntityId(rs.getString("entity_id"));
                    log.setDetails(rs.getString("details"));
                    log.setIpAddress(rs.getString("ip_address"));
                    log.setCreatedAt(rs.getTimestamp("created_at"));
                    list.add(log);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error listing recent audit logs", e);
        }
        return list;
    }
}
