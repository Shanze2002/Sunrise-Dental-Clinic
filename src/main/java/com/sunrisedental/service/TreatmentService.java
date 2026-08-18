package com.sunrisedental.service;

import com.sunrisedental.dao.TreatmentDAO;
import com.sunrisedental.model.Treatment;

import java.util.List;

/**
 * Service: TreatmentService
 */
public class TreatmentService {

    private final TreatmentDAO treatmentDAO = new TreatmentDAO();

    public Treatment getTreatmentById(int id) {
        return treatmentDAO.findById(id);
    }

    public List<Treatment> getActiveTreatments() {
        return treatmentDAO.findAllActive();
    }

    public List<Treatment> getAllTreatments() {
        return treatmentDAO.findAll();
    }

    public boolean createTreatment(Treatment treatment) {
        return treatmentDAO.create(treatment);
    }

    public boolean updateTreatment(Treatment treatment) {
        return treatmentDAO.update(treatment);
    }
}
