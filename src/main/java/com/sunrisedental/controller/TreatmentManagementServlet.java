package com.sunrisedental.controller;

import java.io.IOException;
import java.util.List;
import com.sunrisedental.model.Treatment;
import com.sunrisedental.service.TreatmentService;
import com.sunrisedental.service.factory.ServiceFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "TreatmentManagementServlet", urlPatterns = {
    "/admin/treatments", "/admin/treatments/create", "/admin/treatments/edit"
})
public class TreatmentManagementServlet extends HttpServlet {

    private final TreatmentService treatmentService = ServiceFactory.getTreatmentService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Treatment> treatments = treatmentService.getAllTreatments();
        req.setAttribute("treatments", treatments);
        req.getRequestDispatcher("/admin_treatments.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        if ("/admin/treatments/create".equals(path)) {
            try {
                Treatment t = new Treatment();
                t.setTreatmentName(req.getParameter("treatmentName"));
                t.setCategory(req.getParameter("category"));
                t.setStandardCost(Double.parseDouble(req.getParameter("standardCost")));
                t.setEstimatedDurationMins(Integer.parseInt(req.getParameter("estimatedDurationMins")));
                t.setDescription(req.getParameter("description"));
                t.setActive(true);

                treatmentService.createTreatment(t);
                resp.sendRedirect(req.getContextPath() + "/admin/treatments?success=Treatment+created+successfully");
                return;
            } catch (Exception e) {
                resp.sendRedirect(req.getContextPath() + "/admin/treatments?error=" + e.getMessage());
                return;
            }
        } else if ("/admin/treatments/edit".equals(path)) {
            try {
                int tid = Integer.parseInt(req.getParameter("treatmentId"));
                Treatment t = treatmentService.getTreatmentById(tid);
                if (t != null) {
                    t.setTreatmentName(req.getParameter("treatmentName"));
                    t.setCategory(req.getParameter("category"));
                    t.setStandardCost(Double.parseDouble(req.getParameter("standardCost")));
                    t.setEstimatedDurationMins(Integer.parseInt(req.getParameter("estimatedDurationMins")));
                    t.setDescription(req.getParameter("description"));
                    t.setActive(req.getParameter("active") != null);

                    treatmentService.updateTreatment(t);
                    resp.sendRedirect(req.getContextPath() + "/admin/treatments?success=Treatment+updated+successfully");
                    return;
                }
            } catch (Exception e) {
                resp.sendRedirect(req.getContextPath() + "/admin/treatments?error=" + e.getMessage());
                return;
            }
        }
        resp.sendRedirect(req.getContextPath() + "/admin/treatments");
    }
}
