package com.sunrisedental.controller;

import com.sunrisedental.dao.EmailNotificationDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "NotificationServlet", urlPatterns = {"/notifications/email"})
public class NotificationServlet extends HttpServlet {

    private final EmailNotificationDAO emailDAO = new EmailNotificationDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("pageTitle", "Email Notifications");
        req.setAttribute("emailNotifications", emailDAO.findRecent(50));
        req.getRequestDispatcher("/email_notifications.jsp").forward(req, resp);
    }
}
