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
 * Strict RBAC — receptionist, doctor, and cashier stay in their own modules; admin has full access.
 */
@WebFilter(filterName = "RoleFilter", urlPatterns = {"/*"})
public class RoleFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());
        if (path.length() > 1 && path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        if (isPublicPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = httpRequest.getSession(false);
        User user = (session != null) ? (User) session.getAttribute(AppConfig.SESSION_USER) : null;
        if (user == null) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/auth/login?msg=session_expired");
            return;
        }

        String role = user.getRoleName() != null ? user.getRoleName().toUpperCase() : "";

        if (Role.ADMIN.equalsIgnoreCase(role) || isAllowedForRole(role, path)) {
            chain.doFilter(request, response);
            return;
        }

        deny(httpRequest, httpResponse, role, path);
    }

    private boolean isPublicPath(String path) {
        return path.startsWith("/assets/")
                || path.endsWith(".css") || path.endsWith(".js") || path.endsWith(".png")
                || path.endsWith(".jpg") || path.endsWith(".ico")
                || path.startsWith("/auth/")
                || path.equals("/login.jsp") || path.equals("/index.jsp") || path.equals("/")
                || path.startsWith("/help")
                || path.equals("/error404.jsp") || path.equals("/error500.jsp");
    }

    private boolean isCommonStaffPath(String path) {
        return path.equals("/dashboard")
                || path.equals("/profile.jsp") || path.equals("/auth/profile")
                || path.equals("/admin/users/reset-password")
                || path.equals("/header.jsp") || path.equals("/footer.jsp")
                || path.equals("/sidebar.jsp") || path.equals("/topbar.jsp")
                || path.equals("/alerts.jsp");
    }

    private boolean isAllowedForRole(String role, String path) {
        if (isCommonStaffPath(path)) {
            return true;
        }

        if (Role.RECEPTIONIST.equalsIgnoreCase(role)) {
            return isReceptionPath(path);
        }
        if (Role.DOCTOR.equalsIgnoreCase(role)) {
            return isDoctorPath(path);
        }
        if (Role.CASHIER.equalsIgnoreCase(role)) {
            return isCashierPath(path);
        }
        return false;
    }

    private boolean isReceptionPath(String path) {
        return path.startsWith("/patients")
                || path.startsWith("/appointments")
                || path.startsWith("/notifications/")
                || path.startsWith("/api/patients")
                || path.startsWith("/api/doctors")
                || path.startsWith("/api/appointments")
                || path.equals("/receptionist_dashboard.jsp")
                || path.equals("/patient_register.jsp")
                || path.equals("/patient_list.jsp")
                || path.equals("/patient_view.jsp")
                || path.equals("/book_appointment.jsp")
                || path.equals("/appointments_list.jsp")
                || path.equals("/appointment_view.jsp")
                || path.equals("/search_appointment.jsp")
                || path.equals("/email_notifications.jsp");
    }

    private boolean isDoctorPath(String path) {
        return path.startsWith("/doctor")
                || path.equals("/appointments/view")
                || path.equals("/appointments/search")
                || path.equals("/doctor_schedule.jsp")
                || path.equals("/doctor_treatment.jsp")
                || path.equals("/appointment_view.jsp")
                || path.equals("/search_appointment.jsp");
    }

    private boolean isCashierPath(String path) {
        return path.startsWith("/billing")
                || path.equals("/appointments/view")
                || path.equals("/appointments/search")
                || path.equals("/cashier_billing.jsp")
                || path.equals("/generate_bill.jsp")
                || path.equals("/invoice_print.jsp")
                || path.equals("/appointment_view.jsp")
                || path.equals("/search_appointment.jsp");
    }

    private void deny(HttpServletRequest req, HttpServletResponse resp, String role, String path)
            throws IOException {
        if (path.startsWith("/api/")) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"success\":false,\"message\":\"Access denied for this role.\"}");
            return;
        }
        resp.sendRedirect(req.getContextPath() + "/dashboard?error=access_denied");
    }

    @Override
    public void destroy() {}
}
