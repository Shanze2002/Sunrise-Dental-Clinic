package com.sunrisedental.controller;

import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.util.List;
import com.sunrisedental.config.AppConfig;
import com.sunrisedental.model.*;
import com.sunrisedental.service.*;
import com.sunrisedental.service.factory.ServiceFactory;
import com.sunrisedental.util.DateUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "AppointmentServlet", urlPatterns = {
    "/appointments", "/appointments/book", "/appointments/view", "/appointments/cancel", "/appointments/search"
})
public class AppointmentServlet extends HttpServlet {

    private final AppointmentService appointmentService = ServiceFactory.getAppointmentService();
    private final PatientService patientService = ServiceFactory.getPatientService();
    private final DoctorService doctorService = ServiceFactory.getDoctorService();
    private final TreatmentService treatmentService = ServiceFactory.getTreatmentService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        if ("/appointments/book".equals(path)) {
            prepareBookingForm(req, resp);
        } else if ("/appointments/view".equals(path)) {
            handleViewGet(req, resp);
        } else if ("/appointments/cancel".equals(path)) {
            handleCancelGet(req, resp);
        } else if ("/appointments/search".equals(path)) {
            handleSearchGet(req, resp);
        } else {
            // List all appointments
            String status = req.getParameter("status");
            List<Appointment> apps;
            if (status != null && !status.isEmpty()) {
                apps = appointmentService.getAppointmentsByStatus(status);
            } else {
                apps = appointmentService.getAllAppointments();
            }
            req.setAttribute("appointments", apps);
            req.getRequestDispatcher("/appointments_list.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        if ("/appointments/book".equals(path)) {
            handleBookPost(req, resp);
        } else {
            resp.sendRedirect(req.getContextPath() + "/appointments");
        }
    }

    private void prepareBookingForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Patient> patients = patientService.getAllPatients();
        List<Doctor> doctors = doctorService.getActiveDoctors();
        List<Treatment> treatments = treatmentService.getActiveTreatments();

        req.setAttribute("patients", patients);
        req.setAttribute("doctors", doctors);
        req.setAttribute("treatments", treatments);

        String preSelectPatientId = req.getParameter("patientId");
        if (preSelectPatientId != null) {
            req.setAttribute("selectedPatientId", preSelectPatientId);
        }

        req.getRequestDispatcher("/book_appointment.jsp").forward(req, resp);
    }

    private void handleBookPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            int patientId = Integer.parseInt(req.getParameter("patientId"));
            int doctorId = Integer.parseInt(req.getParameter("doctorId"));
            int treatmentId = Integer.parseInt(req.getParameter("treatmentId"));
            Date apptDate = DateUtil.parseSqlDate(req.getParameter("appointmentDate"));
            Time apptTime = DateUtil.parseSqlTime(req.getParameter("appointmentTime"));
            String reason = req.getParameter("reason");

            HttpSession session = req.getSession(false);
            User user = (session != null) ? (User) session.getAttribute(AppConfig.SESSION_USER) : null;
            Integer createdBy = (user != null) ? user.getUserId() : null;

            Appointment appt = new Appointment();
            appt.setPatientId(patientId);
            appt.setDoctorId(doctorId);
            appt.setTreatmentId(treatmentId);
            appt.setAppointmentDate(apptDate);
            appt.setAppointmentTime(apptTime);
            appt.setReason(reason);
            appt.setCreatedBy(createdBy);

            String result = appointmentService.bookAppointment(appt);
            if ("SUCCESS".equals(result)) {
                resp.sendRedirect(req.getContextPath() + "/appointments/view?id=" + appt.getAppointmentId() + "&success=Appointment+scheduled+successfully.+Appointment+No:+" + appt.getAppointmentNumber());
            } else {
                req.setAttribute("errorMessage", result);
                prepareBookingForm(req, resp);
            }
        } catch (Exception e) {
            req.setAttribute("errorMessage", "Error booking appointment: " + e.getMessage());
            prepareBookingForm(req, resp);
        }
    }

    private void handleViewGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idStr = req.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/appointments");
            return;
        }
        try {
            int apptId = Integer.parseInt(idStr);
            Appointment app = appointmentService.getAppointmentById(apptId);
            if (app == null) {
                resp.sendRedirect(req.getContextPath() + "/appointments?error=Appointment+not+found");
                return;
            }
            req.setAttribute("appointment", app);
            req.getRequestDispatcher("/appointment_view.jsp").forward(req, resp);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/appointments");
        }
    }

    private void handleCancelGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String idStr = req.getParameter("id");
        if (idStr != null && !idStr.isEmpty()) {
            try {
                int apptId = Integer.parseInt(idStr);
                appointmentService.cancelAppointment(apptId);
                resp.sendRedirect(req.getContextPath() + "/appointments/view?id=" + apptId + "&success=Appointment+cancelled");
                return;
            } catch (Exception ignored) {}
        }
        resp.sendRedirect(req.getContextPath() + "/appointments");
    }

    private void handleSearchGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String query = req.getParameter("q");
        if (query != null && !query.trim().isEmpty()) {
            List<Appointment> results = appointmentService.searchAppointments(query.trim());
            req.setAttribute("searchResults", results);
            req.setAttribute("searchQuery", query.trim());
        }
        req.getRequestDispatcher("/search_appointment.jsp").forward(req, resp);
    }
}
