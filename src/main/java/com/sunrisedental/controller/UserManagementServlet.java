package com.sunrisedental.controller;

import java.io.IOException;
import java.util.List;
import com.sunrisedental.model.User;
import com.sunrisedental.service.UserService;
import com.sunrisedental.service.factory.ServiceFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "UserManagementServlet", urlPatterns = {
    "/admin/users", "/admin/users/create", "/admin/users/toggle", "/admin/users/reset-password"
})
public class UserManagementServlet extends HttpServlet {

    private final UserService userService = ServiceFactory.getUserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        if ("/admin/users/toggle".equals(path)) {
            handleToggleGet(req, resp);
        } else {
            List<User> users = userService.getAllUsers();
            req.setAttribute("users", users);
            req.getRequestDispatcher("/admin_users.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        if ("/admin/users/create".equals(path)) {
            handleCreatePost(req, resp);
        } else if ("/admin/users/reset-password".equals(path)) {
            handleResetPasswordPost(req, resp);
        } else {
            resp.sendRedirect(req.getContextPath() + "/admin/users");
        }
    }

    private void handleCreatePost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String u = req.getParameter("username");
            String p = req.getParameter("password");
            String f = req.getParameter("fullName");
            int roleId = Integer.parseInt(req.getParameter("roleId"));
            String e = req.getParameter("email");
            String ph = req.getParameter("phone");

            boolean success = userService.createUser(u, p, f, roleId, e, ph);
            if (success) {
                resp.sendRedirect(req.getContextPath() + "/admin/users?success=User+account+created+successfully");
            } else {
                resp.sendRedirect(req.getContextPath() + "/admin/users?error=Username+or+email+already+exists");
            }
        } catch (Exception ex) {
            resp.sendRedirect(req.getContextPath() + "/admin/users?error=" + ex.getMessage());
        }
    }

    private void handleToggleGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int uid = Integer.parseInt(req.getParameter("id"));
            boolean active = Boolean.parseBoolean(req.getParameter("active"));
            userService.toggleUserActive(uid, active);
            resp.sendRedirect(req.getContextPath() + "/admin/users?success=User+status+updated");
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/admin/users");
        }
    }

    private void handleResetPasswordPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int uid = Integer.parseInt(req.getParameter("userId"));
            String newPass = req.getParameter("newPassword");
            userService.resetPassword(uid, newPass);
            resp.sendRedirect(req.getContextPath() + "/admin/users?success=Password+reset+successfully");
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/admin/users?error=" + e.getMessage());
        }
    }
}
