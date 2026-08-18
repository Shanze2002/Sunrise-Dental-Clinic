package com.sunrisedental.controller.api;

import com.sunrisedental.model.Patient;
import com.sunrisedental.service.PatientService;
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
 * REST Web Service: ApiPatientSearchServlet
 * Endpoint: /api/patients/search
 * Fast JSON auto-suggest endpoint for patient searching by Name, Phone, or NIC.
 */
@WebServlet(name = "ApiPatientSearchServlet", urlPatterns = {"/api/patients/search"})
public class ApiPatientSearchServlet extends HttpServlet {

    private final PatientService patientService = ServiceFactory.getPatientService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String query = req.getParameter("q");
        List<Patient> patients;

        if (ValidationUtil.isNotEmpty(query)) {
            patients = patientService.searchPatients(query);
        } else {
            patients = patientService.getAllPatients();
        }

        List<String> items = new ArrayList<>();
        for (Patient p : patients) {
            items.add("{" +
                "\"id\":" + p.getPatientId() + "," +
                "\"code\":\"" + JsonUtil.escape(p.getPatientCode()) + "\"," +
                "\"name\":\"" + JsonUtil.escape(p.getFullName()) + "\"," +
                "\"phone\":\"" + JsonUtil.escape(p.getPhone()) + "\"," +
                "\"nic\":\"" + JsonUtil.escape(p.getNicPassport()) + "\"," +
                "\"gender\":\"" + JsonUtil.escape(p.getGender()) + "\"" +
                "}");
        }

        resp.getWriter().write(JsonUtil.toJsonSuccess("Patients found", JsonUtil.listToJson(items)));
    }
}
