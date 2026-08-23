package com.sunrisedental.controller;

import com.sunrisedental.config.MailConfig;
import com.sunrisedental.util.SmtpMailSender;
import com.sunrisedental.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "MailSettingsServlet", urlPatterns = {"/admin/mail"})
public class MailSettingsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("pageTitle", "Email SMTP Setup");
        req.setAttribute("mailConfig", MailConfig.getInstance());
        req.getRequestDispatcher("/admin_mail.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("mailUsername");
        String password = req.getParameter("mailPassword");
        boolean enabled = "true".equalsIgnoreCase(req.getParameter("mailEnabled"));

        boolean saved = MailConfig.getInstance().save(username, password, enabled);
        if (!saved) {
            resp.sendRedirect(req.getContextPath() + "/admin/mail?error=Could+not+save+SMTP+settings");
            return;
        }

        String testTo = req.getParameter("testTo");
        if (testTo != null && ValidationUtil.isValidEmail(testTo) && MailConfig.getInstance().isEnabled()) {
            boolean sent = SmtpMailSender.send(testTo.trim(),
                    "Sunrise Dental Clinic - test email",
                    "SMTP is working. Booking, registration, and bill remainder emails will now be sent to patients.");
            if (sent) {
                resp.sendRedirect(req.getContextPath() + "/admin/mail?success=SMTP+saved.+Test+email+sent+to+" + testTo);
            } else {
                resp.sendRedirect(req.getContextPath() + "/admin/mail?error=Saved+but+test+send+failed.+Use+a+Gmail+App+Password+(not+the+normal+password).");
            }
            return;
        }

        resp.sendRedirect(req.getContextPath() + "/admin/mail?success=SMTP+settings+saved");
    }
}
