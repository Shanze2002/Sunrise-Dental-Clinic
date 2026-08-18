package com.sunrisedental.controller.api;

import com.sunrisedental.model.Appointment;
import com.sunrisedental.model.Doctor;
import com.sunrisedental.service.AppointmentService;
import com.sunrisedental.service.DoctorService;
import com.sunrisedental.service.factory.ServiceFactory;
import com.sunrisedental.util.DateUtil;
import com.sunrisedental.util.JsonUtil;
import com.sunrisedental.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * REST Web Service: ApiDoctorAvailabilityServlet
 * Endpoint: /api/doctors/availability
 * Returns doctor information and booked time slots for a specified date.
 */
@WebServlet(name = "ApiDoctorAvailabilityServlet", urlPatterns = {"/api/doctors/availability"})
public class ApiDoctorAvailabilityServlet extends HttpServlet {

    private final DoctorService doctorService = ServiceFactory.getDoctorService();
    private final AppointmentService appointmentService = ServiceFactory.getAppointmentService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        int doctorId = ValidationUtil.parseIntOrDefault(req.getParameter("doctorId"), 0);
        String dateStr = req.getParameter("date");
        Date date = DateUtil.parseSqlDate(dateStr);

        if (doctorId <= 0) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(JsonUtil.toJsonError("doctorId parameter is required"));
            return;
        }

        Doctor doc = doctorService.getDoctorById(doctorId);
        if (doc == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(JsonUtil.toJsonError("Doctor not found"));
            return;
        }

        if (date == null) {
            date = DateUtil.parseSqlDate(DateUtil.getCurrentSqlDate());
        }

        List<Appointment> bookedAppointments = appointmentService.getDoctorDailySchedule(doctorId, date);
        List<String> bookedTimes = new ArrayList<>();
        for (Appointment a : bookedAppointments) {
            if (!Appointment.STATUS_CANCELLED.equalsIgnoreCase(a.getStatus())) {
                bookedTimes.add("\"" + (a.getAppointmentTime() != null ? a.getAppointmentTime().toString().substring(0, 5) : "") + "\"");
            }
        }

        String dataJson = "{" +
            "\"doctorId\":" + doc.getDoctorId() + "," +
            "\"doctorName\":\"" + JsonUtil.escape(doc.getDoctorName()) + "\"," +
            "\"consultationFee\":" + doc.getConsultationFee() + "," +
            "\"room\":\"" + JsonUtil.escape(doc.getRoomNumber()) + "\"," +
            "\"availableDays\":\"" + JsonUtil.escape(doc.getAvailableDays()) + "\"," +
            "\"bookedSlots\":[" + String.join(",", bookedTimes) + "]" +
            "}";

        resp.getWriter().write(JsonUtil.toJsonSuccess("Availability data retrieved", dataJson));
    }
}
