package com.sunrisedental.service.notification;

import com.sunrisedental.config.AppConfig;
import com.sunrisedental.model.Appointment;
import com.sunrisedental.model.Bill;
import com.sunrisedental.model.Patient;
import com.sunrisedental.model.Payment;

/**
 * Builds patient-facing email subjects and bodies from domain objects.
 */
public final class NotificationComposer {

    private NotificationComposer() {}

    public static NotificationEvent appointmentBooked(Appointment a) {
        String subject = "Appointment confirmed - " + AppConfig.CLINIC_NAME + " (" + safe(a.getAppointmentNumber()) + ")";
        String body = greeting(a.getPatientName())
                + "Your dental appointment has been scheduled.\n\n"
                + "Appointment No: " + safe(a.getAppointmentNumber()) + "\n"
                + "Date: " + a.getAppointmentDate() + "\n"
                + "Time: " + a.getAppointmentTime() + "\n"
                + "Dentist: " + safe(a.getDoctorName()) + "\n"
                + "Treatment: " + safe(a.getTreatmentName()) + "\n"
                + "Clinic: " + AppConfig.CLINIC_ADDRESS + "\n"
                + "Hotline: " + AppConfig.CLINIC_PHONE + "\n\n"
                + "Please arrive 10 minutes early. Reply to " + AppConfig.CLINIC_EMAIL + " if you need to reschedule.";
        return new NotificationEvent(NotificationEvent.APPOINTMENT_BOOKED, a.getPatientEmail(), a.getPatientName(), subject, body);
    }

    public static NotificationEvent appointmentCancelled(Appointment a) {
        String subject = "Appointment cancelled - " + AppConfig.CLINIC_NAME + " (" + safe(a.getAppointmentNumber()) + ")";
        String body = greeting(a.getPatientName())
                + "Your appointment " + safe(a.getAppointmentNumber())
                + " on " + a.getAppointmentDate() + " at " + a.getAppointmentTime()
                + " with " + safe(a.getDoctorName()) + " has been cancelled.\n\n"
                + "Call " + AppConfig.CLINIC_PHONE + " to book a new slot.";
        return new NotificationEvent(NotificationEvent.APPOINTMENT_CANCELLED, a.getPatientEmail(), a.getPatientName(), subject, body);
    }

    public static NotificationEvent patientRegistered(Patient p) {
        String subject = "Welcome to " + AppConfig.CLINIC_NAME;
        String body = greeting(p.getFullName())
                + "Your patient registration is complete.\n\n"
                + "Patient No: " + safe(p.getPatientCode()) + "\n"
                + "Name: " + safe(p.getFullName()) + "\n"
                + "Phone: " + safe(p.getPhone()) + "\n\n"
                + "Please bring this patient number when you visit.\n"
                + "Clinic: " + AppConfig.CLINIC_ADDRESS + "\n"
                + "Hotline: " + AppConfig.CLINIC_PHONE + "\n"
                + "Email: " + AppConfig.CLINIC_EMAIL;
        return new NotificationEvent(NotificationEvent.PATIENT_REGISTERED, p.getEmail(), p.getFullName(), subject, body);
    }

    public static NotificationEvent billRemainder(Bill b) {
        String subject = "Balance reminder LKR " + money(b.getBalanceAmount()) + " - Invoice " + safe(b.getInvoiceNumber());
        String body = greeting(b.getPatientName())
                + "This is a reminder of the remaining amount on your dental invoice.\n\n"
                + "Invoice No: " + safe(b.getInvoiceNumber()) + "\n"
                + "Appointment: " + safe(b.getAppointmentNumber()) + "\n"
                + "Treatment: " + safe(b.getTreatmentName()) + "\n"
                + "Total: LKR " + money(b.getTotalAmount()) + "\n"
                + "Paid: LKR " + money(b.getPaidAmount()) + "\n"
                + "REMAINDER DUE: LKR " + money(b.getBalanceAmount()) + "\n\n"
                + "Please settle the remainder at the cashier desk.\n"
                + "Hotline: " + AppConfig.CLINIC_PHONE;
        return new NotificationEvent(NotificationEvent.BILL_REMAINDER, b.getPatientEmail(), b.getPatientName(), subject, body);
    }

    public static NotificationEvent billGenerated(Bill b) {
        String subject = "Invoice " + safe(b.getInvoiceNumber()) + " - " + AppConfig.CLINIC_NAME;
        String body = greeting(b.getPatientName())
                + "An invoice has been generated for your visit.\n\n"
                + "Invoice No: " + safe(b.getInvoiceNumber()) + "\n"
                + "Appointment: " + safe(b.getAppointmentNumber()) + "\n"
                + "Treatment: " + safe(b.getTreatmentName()) + "\n"
                + "Total: LKR " + b.getTotalAmount() + "\n"
                + "Balance: LKR " + b.getBalanceAmount() + "\n\n"
                + "Please settle at the cashier desk or contact " + AppConfig.CLINIC_EMAIL + ".";
        return new NotificationEvent(NotificationEvent.BILL_GENERATED, b.getPatientEmail(), b.getPatientName(), subject, body);
    }

    public static NotificationEvent paymentReceived(Bill b, Payment p) {
        String receipt = p != null ? p.getReceiptNumber() : "-";
        String subject = "Payment received - " + AppConfig.CLINIC_NAME + " (" + safe(b.getInvoiceNumber()) + ")";
        String body = greeting(b.getPatientName())
                + "We have recorded your payment.\n\n"
                + "Invoice: " + safe(b.getInvoiceNumber()) + "\n"
                + "Receipt: " + safe(receipt) + "\n"
                + "Amount paid: LKR " + (p != null ? p.getAmount() : 0) + "\n"
                + "Outstanding balance: LKR " + b.getBalanceAmount() + "\n"
                + "Status: " + safe(b.getPaymentStatus()) + "\n\n"
                + "Thank you for choosing " + AppConfig.CLINIC_NAME + ".";
        return new NotificationEvent(NotificationEvent.PAYMENT_RECEIVED, b.getPatientEmail(), b.getPatientName(), subject, body);
    }

    private static String money(double value) {
        return String.format("%.2f", value);
    }

    private static String greeting(String name) {
        String n = (name == null || name.isBlank()) ? "Patient" : name;
        return "Dear " + n + ",\n\n";
    }

    private static String safe(String value) {
        return value == null ? "-" : value;
    }
}
