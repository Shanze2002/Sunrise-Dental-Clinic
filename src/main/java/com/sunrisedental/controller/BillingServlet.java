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

@WebServlet(name = "BillingServlet", urlPatterns = {
    "/billing/queue", "/billing/generate", "/billing/pay", "/billing/invoice"
})
public class BillingServlet extends HttpServlet {

    private final BillingService billingService = ServiceFactory.getBillingService();
    private final AppointmentService appointmentService = ServiceFactory.getAppointmentService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        if ("/billing/generate".equals(path)) {
            handleGenerateGet(req, resp);
        } else if ("/billing/invoice".equals(path)) {
            handleInvoiceGet(req, resp);
        } else {
            // Billing queue
            List<Bill> unpaid = billingService.getUnpaidBills();
            List<Bill> recent = billingService.getRecentBills();
            List<Appointment> todayApps = appointmentService.getTodayAppointments();
            double todayRevenue = billingService.getTodayRevenue();

            req.setAttribute("unpaidBills", unpaid);
            req.setAttribute("recentBills", recent);
            req.setAttribute("todayAppointments", todayApps);
            req.setAttribute("todayRevenue", todayRevenue);
            req.getRequestDispatcher("/cashier_billing.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        if ("/billing/generate".equals(path)) {
            handleGeneratePost(req, resp);
        } else if ("/billing/pay".equals(path)) {
            handlePayPost(req, resp);
        } else {
            resp.sendRedirect(req.getContextPath() + "/billing/queue");
        }
    }

    private void handleGenerateGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String apptIdStr = req.getParameter("appointmentId");
        if (apptIdStr != null && !apptIdStr.isEmpty()) {
            try {
                int apptId = Integer.parseInt(apptIdStr);
                Appointment app = appointmentService.getAppointmentById(apptId);
                req.setAttribute("appointment", app);
                req.getRequestDispatcher("/generate_bill.jsp").forward(req, resp);
                return;
            } catch (Exception ignored) {}
        }
        resp.sendRedirect(req.getContextPath() + "/billing/queue");
    }

    private void handleGeneratePost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            int apptId = Integer.parseInt(req.getParameter("appointmentId"));
            double consultFee = Double.parseDouble(req.getParameter("consultationFee"));
            double treatCost = Double.parseDouble(req.getParameter("treatmentCost"));
            double addCharges = Double.parseDouble(req.getParameter("additionalCharges"));
            String discountType = req.getParameter("discountType");
            double taxPct = Double.parseDouble(req.getParameter("taxPercentage"));
            String remarks = req.getParameter("remarks");

            HttpSession session = req.getSession(false);
            User user = (session != null) ? (User) session.getAttribute(AppConfig.SESSION_USER) : null;
            Integer cashierId = (user != null) ? user.getUserId() : null;

            Bill bill = billingService.generateBill(apptId, consultFee, treatCost, addCharges, discountType, taxPct, cashierId, remarks);
            if (bill != null) {
                resp.sendRedirect(req.getContextPath() + "/billing/invoice?billId=" + bill.getBillId() + "&success=Bill+generated+successfully.+Invoice+No:+" + bill.getInvoiceNumber());
            } else {
                resp.sendRedirect(req.getContextPath() + "/billing/queue?error=Failed+to+generate+bill");
            }
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/billing/queue?error=" + e.getMessage());
        }
    }

    private void handleInvoiceGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String billIdStr = req.getParameter("billId");
        if (billIdStr == null || billIdStr.isEmpty()) {
            billIdStr = req.getParameter("id");
        }

        if (billIdStr != null && !billIdStr.isEmpty()) {
            try {
                int billId = Integer.parseInt(billIdStr);
                Bill bill = billingService.getBillById(billId);
                if (bill != null) {
                    req.setAttribute("bill", bill);
                    req.getRequestDispatcher("/invoice_print.jsp").forward(req, resp);
                    return;
                }
            } catch (Exception ignored) {}
        }
        resp.sendRedirect(req.getContextPath() + "/billing/queue");
    }

    private void handlePayPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int billId = Integer.parseInt(req.getParameter("billId"));
            double amount = Double.parseDouble(req.getParameter("amount"));
            String method = req.getParameter("paymentMethod");
            String ref = req.getParameter("transactionReference");
            String remarks = req.getParameter("remarks");

            HttpSession session = req.getSession(false);
            User user = (session != null) ? (User) session.getAttribute(AppConfig.SESSION_USER) : null;
            Integer cashierId = (user != null) ? user.getUserId() : null;

            Payment p = billingService.processPayment(billId, amount, method, ref, cashierId, remarks);
            if (p != null) {
                resp.sendRedirect(req.getContextPath() + "/billing/invoice?billId=" + billId + "&success=Payment+recorded+successfully.+Receipt+No:+" + p.getReceiptNumber());
            } else {
                resp.sendRedirect(req.getContextPath() + "/billing/invoice?billId=" + billId + "&error=Failed+to+record+payment");
            }
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/billing/queue?error=" + e.getMessage());
        }
    }
}
