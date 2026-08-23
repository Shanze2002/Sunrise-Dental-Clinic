package com.sunrisedental.dao;

import com.sunrisedental.config.DBConnectionManager;
import com.sunrisedental.model.Appointment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object: AppointmentDAO
 */
public class AppointmentDAO {

    private static final Logger LOGGER = Logger.getLogger(AppointmentDAO.class.getName());
    private final DBConnectionManager dbManager = DBConnectionManager.getInstance();

    private static final String BASE_SELECT = 
        "SELECT a.*, " +
        "p.patient_code, p.full_name AS patient_name, p.phone AS patient_phone, p.email AS patient_email, p.gender AS patient_gender, p.address AS patient_address, " +
        "u.full_name AS doctor_name, d.specialization AS doctor_specialization, d.room_number AS doctor_room, d.consultation_fee, " +
        "t.treatment_name, t.treatment_code, t.standard_cost AS treatment_cost, " +
        "b.bill_id, b.invoice_number, b.payment_status AS billing_status " +
        "FROM appointments a " +
        "JOIN patients p ON a.patient_id = p.patient_id " +
        "JOIN doctors d ON a.doctor_id = d.doctor_id " +
        "JOIN users u ON d.user_id = u.user_id " +
        "JOIN treatments t ON a.treatment_id = t.treatment_id " +
        "LEFT JOIN bills b ON a.appointment_id = b.appointment_id ";

    public Appointment findById(int appointmentId) {
        String sql = BASE_SELECT + "WHERE a.appointment_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, appointmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapAppointment(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding appointment by ID: " + appointmentId, e);
        }
        return null;
    }

    public Appointment findByNumber(String appointmentNumber) {
        String sql = BASE_SELECT + "WHERE a.appointment_number = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, appointmentNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapAppointment(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding appointment by number: " + appointmentNumber, e);
        }
        return null;
    }

    public List<Appointment> findAll() {
        List<Appointment> list = new ArrayList<>();
        String sql = BASE_SELECT + "ORDER BY a.appointment_date DESC, a.appointment_time DESC LIMIT 200";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapAppointment(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching all appointments", e);
        }
        return list;
    }

    public List<Appointment> findByStatus(String status) {
        List<Appointment> list = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE a.status = ? ORDER BY a.appointment_date DESC, a.appointment_time DESC";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapAppointment(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching appointments by status", e);
        }
        return list;
    }

    public List<Appointment> findByPatientId(int patientId) {
        List<Appointment> list = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE a.patient_id = ? ORDER BY a.appointment_date DESC, a.appointment_time DESC";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapAppointment(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching patient appointment history", e);
        }
        return list;
    }

    public boolean isDoctorSlotBooked(int doctorId, Date date, Time time, Integer excludeAppointmentId) {
        String sql = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appointment_date = ? AND appointment_time = ? " +
                     "AND status NOT IN ('Cancelled', 'No-Show')";
        if (excludeAppointmentId != null && excludeAppointmentId > 0) {
            sql += " AND appointment_id <> " + excludeAppointmentId;
        }

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, doctorId);
            ps.setDate(2, date);
            ps.setTime(3, time);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking doctor slot conflict", e);
        }
        return false;
    }

    public List<Appointment> findDoctorDailySchedule(int doctorId, Date date) {
        List<Appointment> list = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE a.doctor_id = ? AND a.appointment_date = ? ORDER BY a.appointment_time ASC";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, doctorId);
            ps.setDate(2, date);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapAppointment(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching doctor schedule", e);
        }
        return list;
    }

    public List<Appointment> findTodayAppointments() {
        List<Appointment> list = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE a.appointment_date = CURDATE() ORDER BY a.appointment_time ASC";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapAppointment(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching today's appointments", e);
        }
        return list;
    }

    public List<Appointment> search(String query, String status, Date fromDate, Date toDate, Integer doctorId) {
        List<Appointment> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(BASE_SELECT).append("WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (query != null && !query.trim().isEmpty()) {
            sql.append("AND (a.appointment_number LIKE ? OR p.full_name LIKE ? OR p.phone LIKE ? OR p.nic_passport LIKE ? OR p.patient_code LIKE ?) ");
            String pat = "%" + query.trim() + "%";
            params.add(pat);
            params.add(pat);
            params.add(pat);
            params.add(pat);
            params.add(pat);
        }

        if (status != null && !status.trim().isEmpty() && !"ALL".equalsIgnoreCase(status)) {
            sql.append("AND a.status = ? ");
            params.add(status.trim());
        }

        if (fromDate != null) {
            sql.append("AND a.appointment_date >= ? ");
            params.add(fromDate);
        }

        if (toDate != null) {
            sql.append("AND a.appointment_date <= ? ");
            params.add(toDate);
        }

        if (doctorId != null && doctorId > 0) {
            sql.append("AND a.doctor_id = ? ");
            params.add(doctorId);
        }

        sql.append("ORDER BY a.appointment_date DESC, a.appointment_time DESC LIMIT 100");

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapAppointment(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching appointments", e);
        }
        return list;
    }

    public boolean create(Appointment a) {
        if (a.getAppointmentNumber() == null || a.getAppointmentNumber().trim().isEmpty()) {
            a.setAppointmentNumber(generateAppointmentNumber(a.getAppointmentDate()));
        }

        String sql = "INSERT INTO appointments (appointment_number, patient_id, doctor_id, treatment_id, " +
                     "appointment_date, appointment_time, status, clinical_notes, tooth_numbers, prescription, created_by) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, a.getAppointmentNumber());
            ps.setInt(2, a.getPatientId());
            ps.setInt(3, a.getDoctorId());
            ps.setInt(4, a.getTreatmentId());
            ps.setDate(5, a.getAppointmentDate());
            ps.setTime(6, a.getAppointmentTime());
            ps.setString(7, a.getStatus() != null ? a.getStatus() : Appointment.STATUS_SCHEDULED);
            ps.setString(8, a.getClinicalNotes());
            ps.setString(9, a.getToothNumbers());
            ps.setString(10, a.getPrescription());
            if (a.getCreatedBy() != null) {
                ps.setInt(11, a.getCreatedBy());
            } else {
                ps.setNull(11, Types.INTEGER);
            }

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        a.setAppointmentId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating appointment", e);
        }
        return false;
    }

    public boolean updateStatus(int appointmentId, String status) {
        String sql = "UPDATE appointments SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE appointment_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, appointmentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating appointment status", e);
        }
        return false;
    }

    public boolean updateClinicalDetails(int appointmentId, String status, String toothNumbers, String notes, String prescription) {
        String sql = "UPDATE appointments SET status = ?, tooth_numbers = ?, clinical_notes = ?, prescription = ?, updated_at = CURRENT_TIMESTAMP WHERE appointment_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setString(2, toothNumbers);
            ps.setString(3, notes);
            ps.setString(4, prescription);
            ps.setInt(5, appointmentId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating clinical notes", e);
        }
        return false;
    }

    public int countTodayAppointments() {
        String sql = "SELECT COUNT(*) FROM appointments WHERE appointment_date = CURDATE()";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting today's appointments", e);
        }
        return 0;
    }

    public int countTotalAppointments() {
        String sql = "SELECT COUNT(*) FROM appointments";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting total appointments", e);
        }
        return 0;
    }

    private String generateAppointmentNumber(Date date) {
        String datePart = (date != null) ? date.toString().replace("-", "") : "20260817";
        int randomSeq = (int) (Math.random() * 9000) + 1000;
        return "APT-" + datePart + "-" + randomSeq;
    }

    private Appointment mapAppointment(ResultSet rs) throws SQLException {
        Appointment a = new Appointment();
        a.setAppointmentId(rs.getInt("appointment_id"));
        a.setAppointmentNumber(rs.getString("appointment_number"));
        a.setPatientId(rs.getInt("patient_id"));
        a.setDoctorId(rs.getInt("doctor_id"));
        a.setTreatmentId(rs.getInt("treatment_id"));
        a.setAppointmentDate(rs.getDate("appointment_date"));
        a.setAppointmentTime(rs.getTime("appointment_time"));
        a.setStatus(rs.getString("status"));
        a.setClinicalNotes(rs.getString("clinical_notes"));
        a.setToothNumbers(rs.getString("tooth_numbers"));
        a.setPrescription(rs.getString("prescription"));
        a.setCreatedBy((Integer) rs.getObject("created_by"));
        a.setCreatedAt(rs.getTimestamp("created_at"));
        a.setUpdatedAt(rs.getTimestamp("updated_at"));

        // Joined fields
        a.setPatientCode(rs.getString("patient_code"));
        a.setPatientName(rs.getString("patient_name"));
        a.setPatientPhone(rs.getString("patient_phone"));
        a.setPatientEmail(rs.getString("patient_email"));
        a.setPatientGender(rs.getString("patient_gender"));
        a.setPatientAddress(rs.getString("patient_address"));
        a.setDoctorName(rs.getString("doctor_name"));
        a.setDoctorSpecialization(rs.getString("doctor_specialization"));
        a.setDoctorRoom(rs.getString("doctor_room"));
        a.setDoctorConsultationFee(rs.getDouble("consultation_fee"));
        a.setTreatmentName(rs.getString("treatment_name"));
        try { a.setTreatmentCode(rs.getString("treatment_code")); } catch (Exception ignored) {}
        a.setTreatmentCost(rs.getDouble("treatment_cost"));
        a.setBillingStatus(rs.getString("billing_status") != null ? rs.getString("billing_status") : "Unbilled");
        a.setBillId((Integer) rs.getObject("bill_id"));
        a.setInvoiceNumber(rs.getString("invoice_number"));

        return a;
    }
}
