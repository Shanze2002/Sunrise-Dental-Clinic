package com.sunrisedental.controller;

import java.io.IOException;
import java.util.List;
import com.sunrisedental.config.AppConfig;
import com.sunrisedental.model.*;
import com.sunrisedental.service.*;
import com.sunrisedental.service.factory.ServiceFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "DashboardServlet", urlPatterns = {"/dashboard"})
public class DashboardServlet extends HttpServlet {

    private final AppointmentService appointmentService = ServiceFactory.getAppointmentService();
    private final PatientService patientService = ServiceFactory.getPatientService();
    private final DoctorService doctorService = ServiceFactory.getDoctorService();
    private final BillingService billingService = ServiceFactory.getBillingService();
    private final AuditService auditService = ServiceFactory.getAuditService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute(AppConfig.SESSION_USER) : null;

        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        String role = user.getRoleName();
        if (Role.ROLE_ADMIN.equalsIgnoreCase(role)) {
            renderAdminDashboard(req, resp);
        } else if (Role.ROLE_DOCTOR.equalsIgnoreCase(role)) {
            renderDoctorDashboard(req, resp, user);
        } else if (Role.ROLE_CASHIER.equalsIgnoreCase(role)) {
            renderCashierDashboard(req, resp);
        } else {
            // Default RECEPTIONIST
            renderReceptionistDashboard(req, resp);
        }
    }

    private void renderReceptionistDashboard(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Appointment> todayApps = appointmentService.getTodayAppointments();
        int totalPatients = patientService.getTotalPatientsCount();
        List<Doctor> activeDocs = doctorService.getActiveDoctors();

        req.setAttribute("todayAppointments", todayApps);
        req.setAttribute("totalPatients", totalPatients);
        req.setAttribute("activeDoctors", activeDocs);
        req.getRequestDispatcher("/receptionist_dashboard.jsp").forward(req, resp);
    }

    private void renderDoctorDashboard(HttpServletRequest req, HttpServletResponse resp, User user) throws ServletException, IOException {
        Doctor doc = doctorService.getDoctorByUserId(user.getUserId());
        if (doc != null) {
            List<Appointment> schedule = appointmentService.getDoctorDailySchedule(doc.getDoctorId(), new java.sql.Date(System.currentTimeMillis()));
            req.setAttribute("doctor", doc);
            req.setAttribute("schedule", schedule);
        }
        req.getRequestDispatcher("/doctor_schedule.jsp").forward(req, resp);
    }

    private void renderCashierDashboard(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Bill> unpaidBills = billingService.getUnpaidBills();
        List<Bill> recentBills = billingService.getRecentBills();
        double todayRevenue = billingService.getTodayRevenue();

        req.setAttribute("unpaidBills", unpaidBills);
        req.setAttribute("recentBills", recentBills);
        req.setAttribute("todayRevenue", todayRevenue);
        req.getRequestDispatcher("/cashier_billing.jsp").forward(req, resp);
    }

    private void renderAdminDashboard(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int totalPatients = patientService.getTotalPatientsCount();
        int todayAppsCount = appointmentService.getTodayAppointmentsCount();
        double todayRevenue = billingService.getTodayRevenue();
        List<Appointment> todayApps = appointmentService.getTodayAppointments();
        List<AuditLog> auditLogs = auditService.getRecentLogs(8);
        List<Doctor> activeDocs = doctorService.getActiveDoctors();

        req.setAttribute("totalPatients", totalPatients);
        req.setAttribute("todayAppointmentsCount", todayAppsCount);
        req.setAttribute("todayRevenue", todayRevenue);
        req.setAttribute("todayAppointments", todayApps);
        req.setAttribute("recentAuditLogs", auditLogs);
        req.setAttribute("activeDoctors", activeDocs);
        req.getRequestDispatcher("/admin_dashboard.jsp").forward(req, resp);
    }
}
