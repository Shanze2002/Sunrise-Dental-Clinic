package com.sunrisedental.controller;

import java.io.IOException;
import java.util.List;
import com.sunrisedental.model.Patient;
import com.sunrisedental.service.PatientService;
import com.sunrisedental.service.factory.ServiceFactory;
import com.sunrisedental.util.DateUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "PatientServlet", urlPatterns = {"/patients", "/patients/new", "/patients/view", "/patients/edit", "/patients/search"})
public class PatientServlet extends HttpServlet {

    private final PatientService patientService = ServiceFactory.getPatientService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        if ("/patients/new".equals(path)) {
            req.setAttribute("isEdit", false);
            req.getRequestDispatcher("/patient_register.jsp").forward(req, resp);
        } else if ("/patients/edit".equals(path)) {
            handleEditGet(req, resp);
        } else if ("/patients/view".equals(path)) {
            handleViewGet(req, resp);
        } else {
            // Patients list / search
            String query = req.getParameter("q");
            List<Patient> patients;
            if (query != null && !query.trim().isEmpty()) {
                patients = patientService.searchPatients(query.trim());
                req.setAttribute("searchQuery", query.trim());
            } else {
                patients = patientService.getAllPatients();
            }
            req.setAttribute("patients", patients);
            req.getRequestDispatcher("/patient_list.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        if ("/patients/new".equals(path)) {
            handleCreatePost(req, resp);
        } else if ("/patients/edit".equals(path)) {
            handleUpdatePost(req, resp);
        } else {
            resp.sendRedirect(req.getContextPath() + "/patients");
        }
    }

    private void handleViewGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idStr = req.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/patients");
            return;
        }
        try {
            int patientId = Integer.parseInt(idStr);
            Patient patient = patientService.getPatientById(patientId);
            if (patient == null) {
                resp.sendRedirect(req.getContextPath() + "/patients?error=Patient+not+found");
                return;
            }
            req.setAttribute("patient", patient);
            req.setAttribute("appointmentHistory", ServiceFactory.getAppointmentService().getPatientAppointmentHistory(patientId));
            req.getRequestDispatcher("/patient_view.jsp").forward(req, resp);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/patients");
        }
    }

    private void handleEditGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idStr = req.getParameter("id");
        if (idStr != null && !idStr.isEmpty()) {
            try {
                Patient patient = patientService.getPatientById(Integer.parseInt(idStr));
                req.setAttribute("patient", patient);
                req.setAttribute("isEdit", true);
                req.getRequestDispatcher("/patient_register.jsp").forward(req, resp);
                return;
            } catch (Exception ignored) {}
        }
        resp.sendRedirect(req.getContextPath() + "/patients");
    }

    private void handleCreatePost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Patient p = new Patient();
        p.setFullName(req.getParameter("fullName"));
        p.setPhone(req.getParameter("phone"));
        p.setEmail(req.getParameter("email"));
        p.setNicPassport(req.getParameter("nicPassport"));
        p.setGender(req.getParameter("gender"));
        p.setDob(DateUtil.parseSqlDate(req.getParameter("dob")));
        p.setAddress(req.getParameter("address"));
        p.setEmergencyContact(req.getParameter("emergencyContact"));
        p.setMedicalHistory(req.getParameter("medicalHistory"));
        p.setAllergies(req.getParameter("allergies"));

        boolean success = patientService.registerPatient(p);
        if (success) {
            resp.sendRedirect(req.getContextPath() + "/patients?success=Patient+registered.+Welcome+email+sent+to+" + java.net.URLEncoder.encode(p.getEmail(), java.nio.charset.StandardCharsets.UTF_8));
        } else {
            req.setAttribute("errorMessage", "Failed to register patient. Please check required fields.");
            req.setAttribute("patient", p);
            req.getRequestDispatcher("/patient_register.jsp").forward(req, resp);
        }
    }

    private void handleUpdatePost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idStr = req.getParameter("patientId");
        if (idStr == null || idStr.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/patients");
            return;
        }
        try {
            Patient p = patientService.getPatientById(Integer.parseInt(idStr));
            if (p != null) {
                p.setFullName(req.getParameter("fullName"));
                p.setPhone(req.getParameter("phone"));
                p.setEmail(req.getParameter("email"));
                p.setNicPassport(req.getParameter("nicPassport"));
                p.setGender(req.getParameter("gender"));
                p.setDob(DateUtil.parseSqlDate(req.getParameter("dob")));
                p.setAddress(req.getParameter("address"));
                p.setEmergencyContact(req.getParameter("emergencyContact"));
                p.setMedicalHistory(req.getParameter("medicalHistory"));
                p.setAllergies(req.getParameter("allergies"));

                patientService.updatePatient(p);
                resp.sendRedirect(req.getContextPath() + "/patients/view?id=" + p.getPatientId() + "&success=Patient+updated+successfully");
                return;
            }
        } catch (Exception ignored) {}
        resp.sendRedirect(req.getContextPath() + "/patients");
    }
}
