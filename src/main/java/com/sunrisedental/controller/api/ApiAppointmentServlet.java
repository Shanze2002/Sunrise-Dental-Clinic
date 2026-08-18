package com.sunrisedental.controller.api;

import com.sunrisedental.model.Appointment;
import com.sunrisedental.service.AppointmentService;
import com.sunrisedental.service.factory.ServiceFactory;
import com.sunrisedental.util.JsonUtil;
import com.sunrisedental.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * REST Web Service: ApiAppointmentServlet
 * Endpoint: /api/appointments
 * Provides appointment search and quick status querying for distributed integration.
 */
@WebServlet(name = "ApiAppointmentServlet", urlPatterns = {"/api/appointments"})
public class ApiAppointmentServlet extends HttpServlet {

    private final AppointmentService appointmentService = ServiceFactory.getAppointmentService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String query = req.getParameter("q");
        String appNum = req.getParameter("number");

        if (ValidationUtil.isNotEmpty(appNum)) {
            Appointment app = appointmentService.getAppointmentByNumber(appNum);
            if (app != null) {
                String json = appointmentToJson(app);
                resp.getWriter().write(JsonUtil.toJsonSuccess("Appointment found", json));
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write(JsonUtil.toJsonError("Appointment not found"));
            }
            return;
        }

        List<Appointment> list;
        if (ValidationUtil.isNotEmpty(query)) {
            list = appointmentService.searchAppointments(query, null, null, null, null);
        } else {
            list = appointmentService.getTodayAppointments();
        }

        List<String> items = new ArrayList<>();
        for (Appointment a : list) {
            items.add(appointmentToJson(a));
        }

        resp.getWriter().write(JsonUtil.toJsonSuccess("Appointments retrieved", JsonUtil.listToJson(items)));
    }

    private String appointmentToJson(Appointment a) {
        return "{" +
            "\"id\":" + a.getAppointmentId() + "," +
            "\"number\":\"" + JsonUtil.escape(a.getAppointmentNumber()) + "\"," +
            "\"patientName\":\"" + JsonUtil.escape(a.getPatientName()) + "\"," +
            "\"patientPhone\":\"" + JsonUtil.escape(a.getPatientPhone()) + "\"," +
            "\"doctorName\":\"" + JsonUtil.escape(a.getDoctorName()) + "\"," +
            "\"treatmentName\":\"" + JsonUtil.escape(a.getTreatmentName()) + "\"," +
            "\"date\":\"" + (a.getAppointmentDate() != null ? a.getAppointmentDate().toString() : "") + "\"," +
            "\"time\":\"" + (a.getAppointmentTime() != null ? a.getAppointmentTime().toString() : "") + "\"," +
            "\"status\":\"" + JsonUtil.escape(a.getStatus()) + "\"," +
            "\"billingStatus\":\"" + JsonUtil.escape(a.getBillingStatus()) + "\"" +
            "}";
    }
}
