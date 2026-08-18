package com.sunrisedental.model;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * JavaBean / DTO: Patient
 */
public class Patient implements Serializable {
    private static final long serialVersionUID = 1L;

    private int patientId;
    private String patientCode;
    private String fullName;
    private String nicPassport;
    private Date dob;
    private String gender;
    private String phone;
    private String email;
    private String address;
    private String emergencyContact;
    private String medicalHistory;
    private String allergies;
    private Timestamp createdAt;

    public Patient() {}

    public Patient(int patientId, String patientCode, String fullName, String nicPassport, Date dob, String gender, String phone, String email, String address) {
        this.patientId = patientId;
        this.patientCode = patientCode;
        this.fullName = fullName;
        this.nicPassport = nicPassport;
        this.dob = dob;
        this.gender = gender;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public String getPatientCode() {
        return patientCode;
    }

    public void setPatientCode(String patientCode) {
        this.patientCode = patientCode;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getNicPassport() {
        return nicPassport;
    }

    public void setNicPassport(String nicPassport) {
        this.nicPassport = nicPassport;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public String getMedicalHistory() {
        return medicalHistory;
    }

    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
