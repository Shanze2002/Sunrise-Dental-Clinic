package com.sunrisedental.service.factory;

import com.sunrisedental.service.*;

/**
 * Factory Pattern: ServiceFactory
 * Centralized factory for creating and providing Business Service instances.
 */
public class ServiceFactory {

    private static final UserService USER_SERVICE = new UserService();
    private static final PatientService PATIENT_SERVICE = new PatientService();
    private static final DoctorService DOCTOR_SERVICE = new DoctorService();
    private static final TreatmentService TREATMENT_SERVICE = new TreatmentService();
    private static final AppointmentService APPOINTMENT_SERVICE = new AppointmentService();
    private static final BillingService BILLING_SERVICE = new BillingService();
    private static final ReportService REPORT_SERVICE = new ReportService();
    private static final AuditService AUDIT_SERVICE = new AuditService();

    public static UserService getUserService() {
        return USER_SERVICE;
    }

    public static PatientService getPatientService() {
        return PATIENT_SERVICE;
    }

    public static DoctorService getDoctorService() {
        return DOCTOR_SERVICE;
    }

    public static TreatmentService getTreatmentService() {
        return TREATMENT_SERVICE;
    }

    public static AppointmentService getAppointmentService() {
        return APPOINTMENT_SERVICE;
    }

    public static BillingService getBillingService() {
        return BILLING_SERVICE;
    }

    public static ReportService getReportService() {
        return REPORT_SERVICE;
    }

    public static AuditService getAuditService() {
        return AUDIT_SERVICE;
    }
}
