package com.sunrisedental.model;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * JavaBean / DTO: Appointment
 */
public class Appointment implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String STATUS_SCHEDULED = "Scheduled";
    public static final String STATUS_CONFIRMED = "Confirmed";
    public static final String STATUS_IN_TREATMENT = "In-Treatment";
    public static final String STATUS_COMPLETED = "Completed";
    public static final String STATUS_CANCELLED = "Cancelled";
    public static final String STATUS_NO_SHOW = "No-Show";

    private int appointmentId;
    private String appointmentNumber;
    private int patientId;
    private int doctorId;
    private int treatmentId;
    private Date appointmentDate;
    private Time appointmentTime;
    private String status;
    private String reason;
    private String clinicalNotes;
    private String toothNumbers;
    private String prescription;
    private Integer createdBy;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Joined / Presentation fields
    private String patientName;
    private String patientCode;
    private String patientPhone;
    private String patientEmail;
    private String patientGender;
    private String patientAddress;
    private String doctorName;
    private String doctorSpecialization;
    private String doctorRoom;
    private double doctorConsultationFee;
    private String treatmentName;
    private String treatmentCode;
    private double treatmentCost;
    private String billingStatus; // 'Unpaid', 'Paid', 'Unbilled'
    private Integer billId;
    private String invoiceNumber;

    public Appointment() {}

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getAppointmentNumber() {
        return appointmentNumber;
    }

    public void setAppointmentNumber(String appointmentNumber) {
        this.appointmentNumber = appointmentNumber;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public int getTreatmentId() {
        return treatmentId;
    }

    public void setTreatmentId(int treatmentId) {
        this.treatmentId = treatmentId;
    }

    public Date getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(Date appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public Time getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(Time appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getClinicalNotes() {
        return clinicalNotes;
    }

    public void setClinicalNotes(String clinicalNotes) {
        this.clinicalNotes = clinicalNotes;
    }

    public String getToothNumbers() {
        return toothNumbers;
    }

    public void setToothNumbers(String toothNumbers) {
        this.toothNumbers = toothNumbers;
    }

    public String getPrescription() {
        return prescription;
    }

    public String getPrescriptions() {
        return prescription;
    }

    public void setPrescription(String prescription) {
        this.prescription = prescription;
    }

    public void setPrescriptions(String prescription) {
        this.prescription = prescription;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Joined fields
    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientCode() {
        return patientCode;
    }

    public void setPatientCode(String patientCode) {
        this.patientCode = patientCode;
    }

    public String getPatientPhone() {
        return patientPhone;
    }

    public void setPatientPhone(String patientPhone) {
        this.patientPhone = patientPhone;
    }

    public String getPatientEmail() {
        return patientEmail;
    }

    public void setPatientEmail(String patientEmail) {
        this.patientEmail = patientEmail;
    }

    public String getPatientGender() {
        return patientGender;
    }

    public void setPatientGender(String patientGender) {
        this.patientGender = patientGender;
    }

    public String getPatientAddress() {
        return patientAddress;
    }

    public void setPatientAddress(String patientAddress) {
        this.patientAddress = patientAddress;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDoctorSpecialization() {
        return doctorSpecialization;
    }

    public void setDoctorSpecialization(String doctorSpecialization) {
        this.doctorSpecialization = doctorSpecialization;
    }

    public String getDoctorRoom() {
        return doctorRoom;
    }

    public void setDoctorRoom(String doctorRoom) {
        this.doctorRoom = doctorRoom;
    }

    public double getDoctorConsultationFee() {
        return doctorConsultationFee;
    }

    public void setDoctorConsultationFee(double doctorConsultationFee) {
        this.doctorConsultationFee = doctorConsultationFee;
    }

    public double getConsultationFee() {
        return this.doctorConsultationFee;
    }

    public String getTreatmentName() {
        return treatmentName;
    }

    public void setTreatmentName(String treatmentName) {
        this.treatmentName = treatmentName;
    }

    public String getTreatmentCode() {
        return treatmentCode;
    }

    public void setTreatmentCode(String treatmentCode) {
        this.treatmentCode = treatmentCode;
    }

    public double getTreatmentCost() {
        return treatmentCost;
    }

    public void setTreatmentCost(double treatmentCost) {
        this.treatmentCost = treatmentCost;
    }

    public String getBillingStatus() {
        return billingStatus;
    }

    public void setBillingStatus(String billingStatus) {
        this.billingStatus = billingStatus;
    }

    public Integer getBillId() {
        return billId;
    }

    public void setBillId(Integer billId) {
        this.billId = billId;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public double getEstimatedTotal() {
        return this.doctorConsultationFee + this.treatmentCost;
    }
}
