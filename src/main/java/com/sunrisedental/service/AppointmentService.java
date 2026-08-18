package com.sunrisedental.service;

import com.sunrisedental.dao.AppointmentDAO;
import com.sunrisedental.model.Appointment;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

/**
 * Service: AppointmentService
 * Handles appointment scheduling, conflict detection, search, and clinical updates.
 */
public class AppointmentService {

    private final AppointmentDAO appointmentDAO = new AppointmentDAO();

    public Appointment getAppointmentById(int appointmentId) {
        return appointmentDAO.findById(appointmentId);
    }

    public Appointment getAppointmentByNumber(String appointmentNumber) {
        if (appointmentNumber == null || appointmentNumber.trim().isEmpty()) return null;
        return appointmentDAO.findByNumber(appointmentNumber.trim());
    }

    public List<Appointment> getAllAppointments() {
        return appointmentDAO.findAll();
    }

    public List<Appointment> getAppointmentsByStatus(String status) {
        return appointmentDAO.findByStatus(status);
    }

    public List<Appointment> getPatientAppointmentHistory(int patientId) {
        return appointmentDAO.findByPatientId(patientId);
    }

    /**
     * Book appointment with strict double-booking prevention
     * @return Status result message (e.g. "SUCCESS" or error description)
     */
    public String bookAppointment(Appointment appointment) {
        if (appointment == null) return "Invalid appointment details.";
        if (appointment.getPatientId() <= 0) return "Please select a valid patient.";
        if (appointment.getDoctorId() <= 0) return "Please select a dentist.";
        if (appointment.getTreatmentId() <= 0) return "Please select a treatment type.";
        if (appointment.getAppointmentDate() == null) return "Appointment date is required.";
        if (appointment.getAppointmentTime() == null) return "Appointment time slot is required.";

        // Check for double booking collision
        boolean slotBooked = appointmentDAO.isDoctorSlotBooked(
            appointment.getDoctorId(),
            appointment.getAppointmentDate(),
            appointment.getAppointmentTime(),
            null
        );

        if (slotBooked) {
            return "The selected dentist already has a scheduled appointment at this date and time slot. Please choose another slot.";
        }

        boolean saved = appointmentDAO.create(appointment);
        if (saved) {
            return "SUCCESS";
        } else {
            return "Failed to save appointment. Please try again.";
        }
    }

    public boolean updateStatus(int appointmentId, String newStatus) {
        return appointmentDAO.updateStatus(appointmentId, newStatus);
    }

    public boolean cancelAppointment(int appointmentId) {
        return updateStatus(appointmentId, Appointment.STATUS_CANCELLED);
    }

    public boolean updateClinicalRecord(int appointmentId, String status, String toothNumbers, String clinicalNotes, String prescription) {
        return appointmentDAO.updateClinicalDetails(appointmentId, status, toothNumbers, clinicalNotes, prescription);
    }

    public boolean updateTreatmentDetails(int appointmentId, String status, String toothNumbers, String clinicalNotes, String prescription) {
        return updateClinicalRecord(appointmentId, status, toothNumbers, clinicalNotes, prescription);
    }

    public List<Appointment> getDoctorDailySchedule(int doctorId, Date date) {
        return appointmentDAO.findDoctorDailySchedule(doctorId, date);
    }

    public List<Appointment> getTodayAppointments() {
        return appointmentDAO.findTodayAppointments();
    }

    public List<Appointment> searchAppointments(String query, String status, Date fromDate, Date toDate, Integer doctorId) {
        return appointmentDAO.search(query, status, fromDate, toDate, doctorId);
    }

    public List<Appointment> searchAppointments(String query) {
        return appointmentDAO.search(query, null, null, null, null);
    }

    public int getTodayAppointmentsCount() {
        return appointmentDAO.countTodayAppointments();
    }

    public int getTotalAppointmentsCount() {
        return appointmentDAO.countTotalAppointments();
    }
}
