package com.sunrisedental.service;

import com.sunrisedental.dao.AppointmentDAO;
import com.sunrisedental.dao.BillDAO;
import com.sunrisedental.dao.PaymentDAO;
import com.sunrisedental.model.Appointment;
import com.sunrisedental.model.Bill;
import com.sunrisedental.model.Payment;
import com.sunrisedental.service.strategy.DiscountStrategy;
import com.sunrisedental.service.strategy.DiscountStrategyFactory;

import java.util.List;

/**
 * Service: BillingService
 * Coordinates bill calculation using Strategy Pattern, invoice issuance, and payment processing.
 */
public class BillingService {

    private final BillDAO billDAO = new BillDAO();
    private final PaymentDAO paymentDAO = new PaymentDAO();
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();

    public Bill getBillById(int billId) {
        Bill b = billDAO.findById(billId);
        if (b != null) {
            b.setPaymentHistory(paymentDAO.findByBillId(billId));
        }
        return b;
    }

    public Bill getBillByInvoiceNumber(String invoiceNumber) {
        if (invoiceNumber == null || invoiceNumber.trim().isEmpty()) return null;
        Bill b = billDAO.findByInvoiceNumber(invoiceNumber.trim());
        if (b != null) {
            b.setPaymentHistory(paymentDAO.findByBillId(b.getBillId()));
        }
        return b;
    }

    public Bill getBillByAppointmentId(int appointmentId) {
        Bill b = billDAO.findByAppointmentId(appointmentId);
        if (b != null) {
            b.setPaymentHistory(paymentDAO.findByBillId(b.getBillId()));
        }
        return b;
    }

    public List<Bill> getRecentBills() {
        return billDAO.findAllRecent();
    }

    public List<Bill> getUnpaidBills() {
        return billDAO.findUnpaidOrPartial();
    }

    public Bill generateBill(int appointmentId, double additionalCharges, String discountType, double taxPercentage) {
        Appointment app = appointmentDAO.findById(appointmentId);
        if (app == null) return null;
        return generateBill(appointmentId, app.getDoctorConsultationFee(), app.getTreatmentCost(), additionalCharges, discountType, taxPercentage, null, null);
    }

    public Bill generateBill(int appointmentId, double consultationFee, double treatmentCost, double additionalCharges, String discountType, double taxPercentage, Integer cashierId, String remarks) {
        Bill existing = billDAO.findByAppointmentId(appointmentId);
        if (existing != null) {
            return existing;
        }

        Appointment app = appointmentDAO.findById(appointmentId);
        if (app == null) return null;

        double subtotal = consultationFee + treatmentCost + additionalCharges;

        // Apply Discount Strategy (Strategy Pattern)
        DiscountStrategy strategy = DiscountStrategyFactory.getStrategy(discountType);
        double discountAmount = strategy.calculateDiscount(subtotal);
        double discountPercentage = strategy.getPercentage();

        double taxableSubtotal = subtotal - discountAmount;
        if (taxableSubtotal < 0) taxableSubtotal = 0.0;

        double taxAmount = Math.round(((taxableSubtotal * (taxPercentage / 100.0))) * 100.0) / 100.0;
        double totalAmount = Math.round((taxableSubtotal + taxAmount) * 100.0) / 100.0;

        Bill b = new Bill();
        b.setAppointmentId(appointmentId);
        b.setPatientId(app.getPatientId());
        b.setConsultationFee(consultationFee);
        b.setTreatmentCost(treatmentCost);
        b.setAdditionalCharges(additionalCharges);
        b.setDiscountType(strategy.getStrategyName());
        b.setDiscountPercentage(discountPercentage);
        b.setDiscountAmount(discountAmount);
        b.setTaxPercentage(taxPercentage);
        b.setTaxAmount(taxAmount);
        b.setTotalAmount(totalAmount);
        b.setPaidAmount(0.00);
        b.setBalanceAmount(totalAmount);
        b.setPaymentStatus(Bill.STATUS_UNPAID);
        b.setCreatedBy(cashierId);

        boolean created = billDAO.create(b);
        if (created) {
            return b;
        }
        return null;
    }

    public boolean processPayment(int billId, double amount, String paymentMethod, Integer cashierId, String reference, String remarks) {
        if (billId <= 0 || amount <= 0) return false;

        Bill bill = billDAO.findById(billId);
        if (bill == null) return false;

        Payment p = new Payment(billId, null, amount, paymentMethod, cashierId, reference, remarks);
        boolean saved = paymentDAO.create(p);
        if (saved) {
            billDAO.updatePayment(billId, amount);
            return true;
        }
        return false;
    }

    public Payment processPayment(int billId, double amount, String paymentMethod, String reference, Integer cashierId, String remarks) {
        if (billId <= 0 || amount <= 0) return null;

        Bill bill = billDAO.findById(billId);
        if (bill == null) return null;

        Payment p = new Payment(billId, null, amount, paymentMethod, cashierId, reference, remarks);
        boolean saved = paymentDAO.create(p);
        if (saved) {
            billDAO.updatePayment(billId, amount);
            return p;
        }
        return null;
    }

    public List<Payment> getRecentPayments(int limit) {
        return paymentDAO.findRecent(limit);
    }

    public double getTodayRevenue() {
        return billDAO.getTodayRevenue();
    }
}
