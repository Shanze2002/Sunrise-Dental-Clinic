package com.sunrisedental.model;

import java.io.Serializable;

/**
 * JavaBean / DTO: Treatment / Dental Service
 */
public class Treatment implements Serializable {
    private static final long serialVersionUID = 1L;

    private int treatmentId;
    private String treatmentCode;
    private String treatmentName;
    private String category;
    private double standardCost;
    private int estimatedDurationMins;
    private String description;
    private boolean active;

    public Treatment() {}

    public Treatment(int treatmentId, String treatmentCode, String treatmentName, String category, double standardCost, int estimatedDurationMins) {
        this.treatmentId = treatmentId;
        this.treatmentCode = treatmentCode;
        this.treatmentName = treatmentName;
        this.category = category;
        this.standardCost = standardCost;
        this.estimatedDurationMins = estimatedDurationMins;
        this.active = true;
    }

    public int getTreatmentId() {
        return treatmentId;
    }

    public void setTreatmentId(int treatmentId) {
        this.treatmentId = treatmentId;
    }

    public String getTreatmentCode() {
        return treatmentCode;
    }

    public void setTreatmentCode(String treatmentCode) {
        this.treatmentCode = treatmentCode;
    }

    public String getTreatmentName() {
        return treatmentName;
    }

    public void setTreatmentName(String treatmentName) {
        this.treatmentName = treatmentName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getStandardCost() {
        return standardCost;
    }

    public void setStandardCost(double standardCost) {
        this.standardCost = standardCost;
    }

    public int getEstimatedDurationMins() {
        return estimatedDurationMins;
    }

    public void setEstimatedDurationMins(int estimatedDurationMins) {
        this.estimatedDurationMins = estimatedDurationMins;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
