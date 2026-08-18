package com.sunrisedental.controller;

import java.io.IOException;
import java.sql.Date;
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

@WebServlet(name = "DoctorServlet", urlPatterns = {"/doctor/schedule", "/doctor/treatment", "/doctor/update-status"})
public class DoctorServlet extends HttpServlet {

    private final DoctorService doctorService = ServiceFactory.getDoctorService();
    private final AppointmentService appointmentService = ServiceFactory.getAppointmentService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute(AppConfig.SESSION_USER) : null;
        Doctor doctor = null;

        if (user != null) {
            doctor = doctorService.getDoctorByUserId(user.getUserId());
        }

        if (doctor == null) {
            List<Doctor> docs = doctorService.getActiveDoctors();
            if (!docs.isEmpty()) doctor = docs.get(0);
        }

        if ("/doctor/treatment".equals(path)) {
            handleTreatmentGet(req, resp);
        } else {
            // Schedule view
            String dateStr = req.getParameter("date");
            Date scheduleDate = DateUtil.parseSqlDate(dateStr);
            if (scheduleDate == null) scheduleDate = new Date(System.currentTimeMillis());

            if (doctor != null) {
                List<Appointment> schedule = appointmentService.getDoctorDailySchedule(doctor.getDoctorId(), scheduleDate);
                req.setAttribute("schedule", schedule);
                req.setAttribute("doctor", doctor);
            }
            req.setAttribute("selectedDate", scheduleDate);
            req.getRequestDispatcher("/doctor_schedule.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        if ("/doctor/update-status".equals(path)) {
            handleUpdateStatusPost(req, resp);
        } else {
            resp.sendRedirect(req.getContextPath() + "/doctor/schedule");
        }
    }

    private void handleTreatmentGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String apptIdStr = req.getParameter("appointmentId");
        if (apptIdStr != null && !apptIdStr.isEmpty()) {
            try {
                int apptId = Integer.parseInt(apptIdStr);
                Appointment app = appointmentService.getAppointmentById(apptId);
                req.setAttribute("appointment", app);
                req.getRequestDispatcher("/doctor_treatment.jsp").forward(req, resp);
                return;
            } catch (Exception ignored) {}
        }
        resp.sendRedirect(req.getContextPath() + "/doctor/schedule");
    }

    private void handleUpdateStatusPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int apptId = Integer.parseInt(req.getParameter("appointmentId"));
            String status = req.getParameter("status");
            String toothNumbers = req.getParameter("toothNumbers");
            String notes = req.getParameter("clinicalNotes");
            String prescriptions = req.getParameter("prescriptions");

            appointmentService.updateTreatmentDetails(apptId, status, toothNumbers, notes, prescriptions);
            resp.sendRedirect(req.getContextPath() + "/appointments/view?id=" + apptId + "&success=Clinical+notes+and+status+updated+successfully");
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/doctor/schedule?error=Failed+to+update+treatment");
        }
    }
}
