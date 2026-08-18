package com.sunrisedental.dao;

import com.sunrisedental.config.DBConnectionManager;
import com.sunrisedental.model.Patient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object: PatientDAO
 */
public class PatientDAO {

    private static final Logger LOGGER = Logger.getLogger(PatientDAO.class.getName());
    private final DBConnectionManager dbManager = DBConnectionManager.getInstance();

    public Patient findById(int patientId) {
        String sql = "SELECT * FROM patients WHERE patient_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapPatient(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding patient by ID: " + patientId, e);
        }
        return null;
    }

    public Patient findByCode(String patientCode) {
        String sql = "SELECT * FROM patients WHERE patient_code = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, patientCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapPatient(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding patient by code: " + patientCode, e);
        }
        return null;
    }

    public List<Patient> search(String query) {
        List<Patient> list = new ArrayList<>();
        String pattern = "%" + (query == null ? "" : query.trim()) + "%";
        String sql = "SELECT * FROM patients WHERE full_name LIKE ? OR phone LIKE ? OR nic_passport LIKE ? OR patient_code LIKE ? " +
                     "ORDER BY patient_id DESC LIMIT 50";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            ps.setString(4, pattern);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapPatient(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching patients with query: " + query, e);
        }
        return list;
    }

    public List<Patient> findAll() {
        List<Patient> list = new ArrayList<>();
        String sql = "SELECT * FROM patients ORDER BY patient_id DESC LIMIT 100";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapPatient(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error listing patients", e);
        }
        return list;
    }

    public boolean create(Patient p) {
        // Generate patient code if not set
        if (p.getPatientCode() == null || p.getPatientCode().trim().isEmpty()) {
            p.setPatientCode(generateNextPatientCode());
        }

        String sql = "INSERT INTO patients (patient_code, full_name, nic_passport, dob, gender, phone, email, address, emergency_contact, medical_history) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, p.getPatientCode());
            ps.setString(2, p.getFullName());
            ps.setString(3, p.getNicPassport());
            ps.setDate(4, p.getDob());
            ps.setString(5, p.getGender());
            ps.setString(6, p.getPhone());
            ps.setString(7, p.getEmail());
            ps.setString(8, p.getAddress());
            ps.setString(9, p.getEmergencyContact());
            ps.setString(10, p.getMedicalHistory());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        p.setPatientId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating patient: " + p.getFullName(), e);
        }
        return false;
    }

    public boolean update(Patient p) {
        String sql = "UPDATE patients SET full_name = ?, nic_passport = ?, dob = ?, gender = ?, phone = ?, email = ?, " +
                     "address = ?, emergency_contact = ?, medical_history = ? WHERE patient_id = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getFullName());
            ps.setString(2, p.getNicPassport());
            ps.setDate(3, p.getDob());
            ps.setString(4, p.getGender());
            ps.setString(5, p.getPhone());
            ps.setString(6, p.getEmail());
            ps.setString(7, p.getAddress());
            ps.setString(8, p.getEmergencyContact());
            ps.setString(9, p.getMedicalHistory());
            ps.setInt(10, p.getPatientId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating patient: " + p.getPatientId(), e);
        }
        return false;
    }

    public int countTotalPatients() {
        String sql = "SELECT COUNT(*) FROM patients";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting total patients", e);
        }
        return 0;
    }

    private String generateNextPatientCode() {
        String sql = "SELECT MAX(patient_id) FROM patients";
        int nextId = 1;
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                nextId = rs.getInt(1) + 1;
            }
        } catch (SQLException ignored) {}
        return String.format("PT-%04d", nextId);
    }

    private Patient mapPatient(ResultSet rs) throws SQLException {
        Patient p = new Patient();
        p.setPatientId(rs.getInt("patient_id"));
        p.setPatientCode(rs.getString("patient_code"));
        p.setFullName(rs.getString("full_name"));
        p.setNicPassport(rs.getString("nic_passport"));
        p.setDob(rs.getDate("dob"));
        p.setGender(rs.getString("gender"));
        p.setPhone(rs.getString("phone"));
        p.setEmail(rs.getString("email"));
        p.setAddress(rs.getString("address"));
        p.setEmergencyContact(rs.getString("emergency_contact"));
        p.setMedicalHistory(rs.getString("medical_history"));
        p.setCreatedAt(rs.getTimestamp("created_at"));
        return p;
    }
}
