package com.sunrisedental.service;

import com.sunrisedental.dao.PatientDAO;
import com.sunrisedental.model.Patient;
import com.sunrisedental.service.notification.NotificationComposer;
import com.sunrisedental.service.notification.NotificationService;
import com.sunrisedental.util.ValidationUtil;

import java.util.List;

/**
 * Service: PatientService
 * Handles patient registration, validation, search, and profile updates.
 */
public class PatientService {

    private final PatientDAO patientDAO = new PatientDAO();

    public Patient getPatientById(int id) {
        return patientDAO.findById(id);
    }

    public Patient getPatientByCode(String code) {
        return patientDAO.findByCode(code);
    }

    public List<Patient> getAllPatients() {
        return patientDAO.findAll();
    }

    public List<Patient> searchPatients(String query) {
        return patientDAO.search(query);
    }

    public boolean registerPatient(Patient patient) {
        if (patient == null) return false;
        if (!ValidationUtil.isNotEmpty(patient.getFullName())) return false;
        if (!ValidationUtil.isNotEmpty(patient.getPhone())) return false;
        if (!ValidationUtil.isValidEmail(patient.getEmail())) return false;
        if (!ValidationUtil.isNotEmpty(patient.getAddress())) return false;
        if (patient.getDob() == null) return false;

        boolean saved = patientDAO.create(patient);
        if (saved) {
            NotificationService.getInstance().publish(NotificationComposer.patientRegistered(patient));
        }
        return saved;
    }

    public boolean updatePatient(Patient patient) {
        if (patient == null || patient.getPatientId() <= 0) return false;
        return patientDAO.update(patient);
    }

    public int getTotalPatientsCount() {
        return patientDAO.countTotalPatients();
    }
}
