package com.sunrisedental.dao;

import com.sunrisedental.config.DBConnectionManager;
import com.sunrisedental.model.Treatment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object: TreatmentDAO
 */
public class TreatmentDAO {

    private static final Logger LOGGER = Logger.getLogger(TreatmentDAO.class.getName());
    private final DBConnectionManager dbManager = DBConnectionManager.getInstance();

    public Treatment findById(int treatmentId) {
        String sql = "SELECT * FROM treatments WHERE treatment_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, treatmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapTreatment(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding treatment by ID: " + treatmentId, e);
        }
        return null;
    }

    public List<Treatment> findAllActive() {
        List<Treatment> list = new ArrayList<>();
        String sql = "SELECT * FROM treatments WHERE is_active = TRUE ORDER BY category ASC, treatment_name ASC";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapTreatment(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding active treatments", e);
        }
        return list;
    }

    public List<Treatment> findAll() {
        List<Treatment> list = new ArrayList<>();
        String sql = "SELECT * FROM treatments ORDER BY treatment_id ASC";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapTreatment(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error listing all treatments", e);
        }
        return list;
    }

    public boolean create(Treatment t) {
        if (t.getTreatmentCode() == null || t.getTreatmentCode().trim().isEmpty()) {
            t.setTreatmentCode(generateNextTreatmentCode());
        }

        String sql = "INSERT INTO treatments (treatment_code, treatment_name, category, standard_cost, estimated_duration_mins, description, is_active) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, t.getTreatmentCode());
            ps.setString(2, t.getTreatmentName());
            ps.setString(3, t.getCategory());
            ps.setDouble(4, t.getStandardCost());
            ps.setInt(5, t.getEstimatedDurationMins());
            ps.setString(6, t.getDescription());
            ps.setBoolean(7, t.isActive());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        t.setTreatmentId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating treatment: " + t.getTreatmentName(), e);
        }
        return false;
    }

    public boolean update(Treatment t) {
        String sql = "UPDATE treatments SET treatment_name = ?, category = ?, standard_cost = ?, " +
                     "estimated_duration_mins = ?, description = ?, is_active = ? WHERE treatment_id = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, t.getTreatmentName());
            ps.setString(2, t.getCategory());
            ps.setDouble(3, t.getStandardCost());
            ps.setInt(4, t.getEstimatedDurationMins());
            ps.setString(5, t.getDescription());
            ps.setBoolean(6, t.isActive());
            ps.setInt(7, t.getTreatmentId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating treatment: " + t.getTreatmentId(), e);
        }
        return false;
    }

    private String generateNextTreatmentCode() {
        String sql = "SELECT MAX(treatment_id) FROM treatments";
        int nextId = 1;
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                nextId = rs.getInt(1) + 1;
            }
        } catch (SQLException ignored) {}
        return String.format("TRT-%03d", nextId);
    }

    private Treatment mapTreatment(ResultSet rs) throws SQLException {
        Treatment t = new Treatment();
        t.setTreatmentId(rs.getInt("treatment_id"));
        t.setTreatmentCode(rs.getString("treatment_code"));
        t.setTreatmentName(rs.getString("treatment_name"));
        t.setCategory(rs.getString("category"));
        t.setStandardCost(rs.getDouble("standard_cost"));
        t.setEstimatedDurationMins(rs.getInt("estimated_duration_mins"));
        t.setDescription(rs.getString("description"));
        t.setActive(rs.getBoolean("is_active"));
        return t;
    }
}
