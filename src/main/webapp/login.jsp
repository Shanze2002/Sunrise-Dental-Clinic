<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.sunrisedental.config.AppConfig" %>
<%
    String errorMsg = (String) request.getAttribute("errorMessage");
    if (errorMsg == null) errorMsg = request.getParameter("error");
    String msg = request.getParameter("msg");
    String savedUser = "";
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
        for (Cookie c : cookies) {
            if ("sdc_username".equals(c.getName())) {
                savedUser = c.getValue();
            }
        }
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Staff Portal Login | <%= AppConfig.CLINIC_NAME %></title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/main.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/auth.css">
</head>
<body class="auth-wrapper">
    <div class="auth-card">
        <div class="auth-header">
            <div class="auth-logo">🦷</div>
            <h2><%= AppConfig.CLINIC_NAME %></h2>
            <p>Clinical Appointment & Management Portal - Colombo</p>
        </div>

        <div class="auth-body">
            <% if (errorMsg != null && !errorMsg.trim().isEmpty()) { %>
                <div class="alert alert-danger" style="margin-bottom: 20px;">
                    <span>⚠️</span>
                    <div><%= errorMsg %></div>
                </div>
            <% } %>

            <% if ("logged_out".equalsIgnoreCase(msg)) { %>
                <div class="alert alert-success" style="margin-bottom: 20px;">
                    <span>✅</span>
                    <div>You have been securely logged out.</div>
                </div>
            <% } else if ("session_expired".equalsIgnoreCase(msg)) { %>
                <div class="alert alert-warning" style="margin-bottom: 20px;">
                    <span>⏱️</span>
                    <div>Your session has expired. Please log in again.</div>
                </div>
            <% } %>

            <form action="<%= request.getContextPath() %>/auth/login" method="POST">
                <div class="form-group">
                    <label class="form-label" for="username">Username <span class="required">*</span></label>
                    <input type="text" id="username" name="username" class="form-control" 
                           placeholder="Enter your username" 
                           value="<%= (savedUser != null && !savedUser.isEmpty()) ? savedUser : "" %>" required autofocus>
                </div>

                <div class="form-group">
                    <label class="form-label" for="password">Password <span class="required">*</span></label>
                    <input type="password" id="password" name="password" class="form-control" 
                           placeholder="Enter your password" required>
                </div>

                <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 20px;">
                    <label style="display: flex; align-items: center; gap: 8px; font-size: 0.85rem; color: var(--text-secondary); cursor: pointer;">
                        <input type="checkbox" name="rememberMe" value="true" <%= !savedUser.isEmpty() ? "checked" : "" %>>
                        Remember my username
                    </label>
                    <a href="<%= request.getContextPath() %>/help.jsp" style="font-size: 0.82rem; color: var(--teal-600); text-decoration: none; font-weight: 600;">
                        Need Help?
                    </a>
                </div>

                <button type="submit" class="btn-login">
                    Secure Login ➔
                </button>
            </form>

            <div class="demo-credentials">
                <strong>🔑 Demo System Credentials (Password: <code>Admin@123</code>)</strong>
                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 6px; margin-top: 6px;">
                    <div>• <strong>Admin:</strong> <code>admin</code></div>
                    <div>• <strong>Reception:</strong> <code>reception</code></div>
                    <div>• <strong>Doctor:</strong> <code>dr_kamal</code></div>
                    <div>• <strong>Cashier:</strong> <code>cashier</code></div>
                </div>
            </div>
        </div>

        <div class="auth-footer">
            <p><%= AppConfig.CLINIC_ADDRESS %></p>
            <p style="margin-top: 4px;">Hotline: <%= AppConfig.CLINIC_PHONE %></p>
        </div>
    </div>
</body>
</html>

