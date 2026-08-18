package com.sunrisedental.service;

import com.sunrisedental.dao.DoctorDAO;
import com.sunrisedental.model.Doctor;

import java.util.List;

/**
 * Service: DoctorService
 */
public class DoctorService {

    private final DoctorDAO doctorDAO = new DoctorDAO();

    public Doctor getDoctorById(int doctorId) {
        return doctorDAO.findById(doctorId);
    }

    public Doctor getDoctorByUserId(int userId) {
        return doctorDAO.findByUserId(userId);
    }

    public List<Doctor> getActiveDoctors() {
        return doctorDAO.findAllActive();
    }

    public List<Doctor> getAllDoctors() {
        return doctorDAO.findAll();
    }

    public boolean createDoctor(Doctor doctor) {
        return doctorDAO.create(doctor);
    }

    public boolean updateDoctor(Doctor doctor) {
        return doctorDAO.update(doctor);
    }
}
