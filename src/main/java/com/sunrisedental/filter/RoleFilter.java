package com.sunrisedental.filter;

import com.sunrisedental.config.AppConfig;
import com.sunrisedental.model.Role;
import com.sunrisedental.model.User;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Filter / Interceptor Pattern: RoleFilter
 * Enforces strict Role-Based Access Control (RBAC) across Administrative, Doctor, Cashier, and Receptionist modules and JSP pages.
 */
@WebFilter(filterName = "RoleFilter", urlPatterns = {
    "/admin/*", "/admin_dashboard.jsp", "/admin_users.jsp", "/admin_doctors.jsp", "/admin_treatments.jsp", "/admin_reports.jsp",
    "/doctor/*", "/doctor_schedule.jsp", "/doctor_treatment.jsp",
    "/billing/*", "/cashier_billing.jsp", "/generate_bill.jsp", "/invoice_print.jsp",
    "/reports/*",
    "/receptionist_dashboard.jsp", "/patient_register.jsp", "/book_appointment.jsp"
})
public class RoleFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        HttpSession session = httpRequest.getSession(false);
        User user = (session != null) ? (User) session.getAttribute(AppConfig.SESSION_USER) : null;

        if (user == null) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.jsp");
            return;
        }

        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());
        String role = (user.getRoleName() != null) ? user.getRoleName().toUpperCase() : "";

        // 1. ADMIN has full unrestricted access to everything
        if (Role.ADMIN.equalsIgnoreCase(role)) {
            chain.doFilter(request, response);
            return;
        }

        // 2. ADMIN ONLY Pages / Endpoints
        boolean isAdminEndpoint = path.startsWith("/admin") || path.startsWith("/reports")
                || path.contains("admin_dashboard.jsp") || path.contains("admin_users.jsp")
                || path.contains("admin_doctors.jsp") || path.contains("admin_treatments.jsp")
                || path.contains("admin_reports.jsp");

        if (isAdminEndpoint) {
            redirectToDefaultDashboard(httpRequest, httpResponse, role);
            return;
        }

        // 3. DOCTOR Specific Pages / Endpoints
        boolean isDoctorEndpoint = path.startsWith("/doctor")
                || path.contains("doctor_schedule.jsp") || path.contains("doctor_treatment.jsp");

        if (isDoctorEndpoint && !Role.DOCTOR.equalsIgnoreCase(role)) {
            redirectToDefaultDashboard(httpRequest, httpResponse, role);
            return;
        }

        // 4. CASHIER Specific Pages / Endpoints
        boolean isCashierEndpoint = path.startsWith("/billing")
                || path.contains("cashier_billing.jsp") || path.contains("generate_bill.jsp")
                || path.contains("invoice_print.jsp");

        if (isCashierEndpoint && !Role.CASHIER.equalsIgnoreCase(role)) {
            redirectToDefaultDashboard(httpRequest, httpResponse, role);
            return;
        }

        // 5. RECEPTIONIST Specific Pages / Endpoints
        boolean isReceptionistOnlyEndpoint = path.contains("patient_register.jsp")
                || path.contains("book_appointment.jsp") || path.contains("receptionist_dashboard.jsp");

        if (isReceptionistOnlyEndpoint && !Role.RECEPTIONIST.equalsIgnoreCase(role)) {
            redirectToDefaultDashboard(httpRequest, httpResponse, role);
            return;
        }

        chain.doFilter(request, response);
    }

    private void redirectToDefaultDashboard(HttpServletRequest req, HttpServletResponse resp, String role) throws IOException {
        if (Role.DOCTOR.equalsIgnoreCase(role)) {
            resp.sendRedirect(req.getContextPath() + "/doctor_schedule.jsp?error=access_denied");
        } else if (Role.CASHIER.equalsIgnoreCase(role)) {
            resp.sendRedirect(req.getContextPath() + "/cashier_billing.jsp?error=access_denied");
        } else {
            resp.sendRedirect(req.getContextPath() + "/receptionist_dashboard.jsp?error=access_denied");
        }
    }

    @Override
    public void destroy() {}
}
