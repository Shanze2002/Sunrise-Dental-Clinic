package com.sunrisedental.controller;

import java.io.IOException;
import com.sunrisedental.config.AppConfig;
import com.sunrisedental.model.User;
import com.sunrisedental.service.UserService;
import com.sunrisedental.service.factory.ServiceFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "AuthServlet", urlPatterns = {"/auth/login", "/auth/logout", "/auth/profile"})
public class AuthServlet extends HttpServlet {

    private final UserService userService = ServiceFactory.getUserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        if ("/auth/logout".equals(path)) {
            handleLogout(req, resp);
        } else if ("/auth/profile".equals(path)) {
            req.getRequestDispatcher("/profile.jsp").forward(req, resp);
        } else {
            // Login GET
            HttpSession session = req.getSession(false);
            if (session != null && session.getAttribute(AppConfig.SESSION_USER) != null) {
                resp.sendRedirect(req.getContextPath() + "/dashboard");
                return;
            }
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        if ("/auth/login".equals(path)) {
            handleLogin(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String pass = req.getParameter("password");
        String rememberMe = req.getParameter("rememberMe");

        if (username == null || username.trim().isEmpty() || pass == null || pass.trim().isEmpty()) {
            req.setAttribute("errorMessage", "Please provide both username and password.");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
            return;
        }

        User user = userService.authenticate(username.trim(), pass.trim());
        if (user != null) {
            HttpSession session = req.getSession(true);
            session.setAttribute(AppConfig.SESSION_USER, user);
            session.setAttribute(AppConfig.SESSION_USER_ROLE, user.getRoleName());
            session.setMaxInactiveInterval(AppConfig.SESSION_TIMEOUT_SECONDS);

            if ("true".equalsIgnoreCase(rememberMe)) {
                Cookie cookie = new Cookie("sdc_username", user.getUsername());
                cookie.setMaxAge(30 * 24 * 60 * 60);
                cookie.setPath(req.getContextPath());
                cookie.setHttpOnly(true);
                resp.addCookie(cookie);
            }

            resp.sendRedirect(req.getContextPath() + "/dashboard");
        } else {
            req.setAttribute("errorMessage", "Invalid username or password. Please try again.");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        }
    }

    private void handleLogout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        resp.sendRedirect(req.getContextPath() + "/login.jsp?msg=logged_out");
    }
}
