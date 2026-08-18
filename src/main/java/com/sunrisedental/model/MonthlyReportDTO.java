package com.sunrisedental.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * DTO: MonthlyReportDTO
 * Encapsulates aggregated statistical and financial data for clinic decision making
 */
public class MonthlyReportDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String period; // e.g. "2026-08" or "August 2026"
    private int totalAppointments;
    private int completedAppointments;
    private int cancelledAppointments;
    private int newPatientsRegistered;
    private int totalInvoicesIssued;
    private double grossConsultationIncome;
    private double grossTreatmentIncome;
    private double grossAdditionalCharges;
    private double totalDiscountsGranted;
    private double totalTaxCollected;
    private double totalNetRevenue;
    private double totalCashCollected;
    private double outstandingBalance;

    private Map<String, Integer> treatmentsDistribution = new HashMap<>();
    private Map<String, Double> doctorRevenueDistribution = new HashMap<>();

    public MonthlyReportDTO() {}

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public int getTotalAppointments() {
        return totalAppointments;
    }

    public void setTotalAppointments(int totalAppointments) {
        this.totalAppointments = totalAppointments;
    }

    public int getCompletedAppointments() {
        return completedAppointments;
    }

    public void setCompletedAppointments(int completedAppointments) {
        this.completedAppointments = completedAppointments;
    }

    public int getCancelledAppointments() {
        return cancelledAppointments;
    }

    public void setCancelledAppointments(int cancelledAppointments) {
        this.cancelledAppointments = cancelledAppointments;
    }

    public int getNewPatientsRegistered() {
        return newPatientsRegistered;
    }

    public void setNewPatientsRegistered(int newPatientsRegistered) {
        this.newPatientsRegistered = newPatientsRegistered;
    }

    public int getTotalInvoicesIssued() {
        return totalInvoicesIssued;
    }

    public void setTotalInvoicesIssued(int totalInvoicesIssued) {
        this.totalInvoicesIssued = totalInvoicesIssued;
    }

    public double getGrossConsultationIncome() {
        return grossConsultationIncome;
    }

    public void setGrossConsultationIncome(double grossConsultationIncome) {
        this.grossConsultationIncome = grossConsultationIncome;
    }

    public double getGrossTreatmentIncome() {
        return grossTreatmentIncome;
    }

    public void setGrossTreatmentIncome(double grossTreatmentIncome) {
        this.grossTreatmentIncome = grossTreatmentIncome;
    }

    public double getGrossAdditionalCharges() {
        return grossAdditionalCharges;
    }

    public void setGrossAdditionalCharges(double grossAdditionalCharges) {
        this.grossAdditionalCharges = grossAdditionalCharges;
    }

    public double getTotalDiscountsGranted() {
        return totalDiscountsGranted;
    }

    public void setTotalDiscountsGranted(double totalDiscountsGranted) {
        this.totalDiscountsGranted = totalDiscountsGranted;
    }

    public double getTotalTaxCollected() {
        return totalTaxCollected;
    }

    public void setTotalTaxCollected(double totalTaxCollected) {
        this.totalTaxCollected = totalTaxCollected;
    }

    public double getTotalNetRevenue() {
        return totalNetRevenue;
    }

    public void setTotalNetRevenue(double totalNetRevenue) {
        this.totalNetRevenue = totalNetRevenue;
    }

    public double getTotalCashCollected() {
        return totalCashCollected;
    }

    public void setTotalCashCollected(double totalCashCollected) {
        this.totalCashCollected = totalCashCollected;
    }

    public double getOutstandingBalance() {
        return outstandingBalance;
    }

    public void setOutstandingBalance(double outstandingBalance) {
        this.outstandingBalance = outstandingBalance;
    }

    public Map<String, Integer> getTreatmentsDistribution() {
        return treatmentsDistribution;
    }

    public void setTreatmentsDistribution(Map<String, Integer> treatmentsDistribution) {
        this.treatmentsDistribution = treatmentsDistribution;
    }

    public Map<String, Double> getDoctorRevenueDistribution() {
        return doctorRevenueDistribution;
    }

    public void setDoctorRevenueDistribution(Map<String, Double> doctorRevenueDistribution) {
        this.doctorRevenueDistribution = doctorRevenueDistribution;
    }
}
