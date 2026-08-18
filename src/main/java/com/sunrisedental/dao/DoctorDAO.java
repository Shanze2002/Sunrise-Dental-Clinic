package com.sunrisedental.dao;

import com.sunrisedental.config.DBConnectionManager;
import com.sunrisedental.model.Doctor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object: DoctorDAO
 */
public class DoctorDAO {

    private static final Logger LOGGER = Logger.getLogger(DoctorDAO.class.getName());
    private final DBConnectionManager dbManager = DBConnectionManager.getInstance();

    public Doctor findById(int doctorId) {
        String sql = "SELECT d.*, u.full_name AS doctor_name, u.email, u.phone " +
                     "FROM doctors d " +
                     "JOIN users u ON d.user_id = u.user_id " +
                     "WHERE d.doctor_id = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, doctorId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapDoctor(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding doctor by ID: " + doctorId, e);
        }
        return null;
    }

    public Doctor findByUserId(int userId) {
        String sql = "SELECT d.*, u.full_name AS doctor_name, u.email, u.phone " +
                     "FROM doctors d " +
                     "JOIN users u ON d.user_id = u.user_id " +
                     "WHERE d.user_id = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapDoctor(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding doctor by user ID: " + userId, e);
        }
        return null;
    }

    public List<Doctor> findAllActive() {
        List<Doctor> list = new ArrayList<>();
        String sql = "SELECT d.*, u.full_name AS doctor_name, u.email, u.phone " +
                     "FROM doctors d " +
                     "JOIN users u ON d.user_id = u.user_id " +
                     "WHERE d.is_active = TRUE AND u.is_active = TRUE " +
                     "ORDER BY d.doctor_id ASC";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapDoctor(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding active doctors", e);
        }
        return list;
    }

    public List<Doctor> findAll() {
        List<Doctor> list = new ArrayList<>();
        String sql = "SELECT d.*, u.full_name AS doctor_name, u.email, u.phone " +
                     "FROM doctors d " +
                     "JOIN users u ON d.user_id = u.user_id " +
                     "ORDER BY d.doctor_id ASC";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapDoctor(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding all doctors", e);
        }
        return list;
    }

    public boolean create(Doctor d) {
        String sql = "INSERT INTO doctors (user_id, specialization, license_number, consultation_fee, room_number, available_days, start_time, end_time, slot_duration_mins, is_active) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, d.getUserId());
            ps.setString(2, d.getSpecialization());
            ps.setString(3, d.getLicenseNumber());
            ps.setDouble(4, d.getConsultationFee());
            ps.setString(5, d.getRoomNumber());
            ps.setString(6, d.getAvailableDays());
            ps.setTime(7, d.getStartTime());
            ps.setTime(8, d.getEndTime());
            ps.setInt(9, d.getSlotDurationMins());
            ps.setBoolean(10, d.isActive());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        d.setDoctorId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating doctor profile: " + d.getUserId(), e);
        }
        return false;
    }

    public boolean update(Doctor d) {
        String sql = "UPDATE doctors SET specialization = ?, license_number = ?, consultation_fee = ?, room_number = ?, " +
                     "available_days = ?, start_time = ?, end_time = ?, slot_duration_mins = ?, is_active = ? " +
                     "WHERE doctor_id = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, d.getSpecialization());
            ps.setString(2, d.getLicenseNumber());
            ps.setDouble(3, d.getConsultationFee());
            ps.setString(4, d.getRoomNumber());
            ps.setString(5, d.getAvailableDays());
            ps.setTime(6, d.getStartTime());
            ps.setTime(7, d.getEndTime());
            ps.setInt(8, d.getSlotDurationMins());
            ps.setBoolean(9, d.isActive());
            ps.setInt(10, d.getDoctorId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating doctor profile: " + d.getDoctorId(), e);
        }
        return false;
    }

    private Doctor mapDoctor(ResultSet rs) throws SQLException {
        Doctor d = new Doctor();
        d.setDoctorId(rs.getInt("doctor_id"));
        d.setUserId(rs.getInt("user_id"));
        d.setDoctorName(rs.getString("doctor_name"));
        d.setEmail(rs.getString("email"));
        d.setPhone(rs.getString("phone"));
        d.setSpecialization(rs.getString("specialization"));
        d.setLicenseNumber(rs.getString("license_number"));
        d.setConsultationFee(rs.getDouble("consultation_fee"));
        d.setRoomNumber(rs.getString("room_number"));
        d.setAvailableDays(rs.getString("available_days"));
        d.setStartTime(rs.getTime("start_time"));
        d.setEndTime(rs.getTime("end_time"));
        d.setSlotDurationMins(rs.getInt("slot_duration_mins"));
        d.setActive(rs.getBoolean("is_active"));
        return d;
    }
}
