package com.sunrisedental.controller;

import java.io.IOException;
import java.util.List;
import com.sunrisedental.model.Doctor;
import com.sunrisedental.service.DoctorService;
import com.sunrisedental.service.factory.ServiceFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "DoctorManagementServlet", urlPatterns = {"/admin/doctors", "/admin/doctors/edit"})
public class DoctorManagementServlet extends HttpServlet {

    private final DoctorService doctorService = ServiceFactory.getDoctorService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Doctor> doctors = doctorService.getAllDoctors();
        req.setAttribute("doctors", doctors);
        req.getRequestDispatcher("/admin_doctors.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        if ("/admin/doctors/edit".equals(path)) {
            try {
                int docId = Integer.parseInt(req.getParameter("doctorId"));
                Doctor doc = doctorService.getDoctorById(docId);
                if (doc != null) {
                    doc.setSpecialization(req.getParameter("specialization"));
                    doc.setLicenseNumber(req.getParameter("licenseNumber"));
                    doc.setConsultationFee(Double.parseDouble(req.getParameter("consultationFee")));
                    doc.setRoomNumber(req.getParameter("roomNumber"));
                    doc.setAvailableDays(req.getParameter("availableDays"));
                    doc.setActive(req.getParameter("active") != null);

                    doctorService.updateDoctor(doc);
                    resp.sendRedirect(req.getContextPath() + "/admin/doctors?success=Doctor+updated+successfully");
                    return;
                }
            } catch (Exception e) {
                resp.sendRedirect(req.getContextPath() + "/admin/doctors?error=" + e.getMessage());
                return;
            }
        }
        resp.sendRedirect(req.getContextPath() + "/admin/doctors");
    }
}
