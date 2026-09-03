<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.sunrisedental.config.AppConfig" %>
<%@ page import="jakarta.servlet.http.Cookie" %>
<%
    String errorMsg = (String) request.getAttribute("errorMessage");
    if (errorMsg == null) errorMsg = request.getParameter("error");
    String msg = request.getParameter("msg");
    String savedUser = (String) request.getAttribute("rememberedUsername");
    if (savedUser == null) savedUser = "";
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
        for (Cookie c : cookies) {
            if (AppConfig.COOKIE_USERNAME.equals(c.getName()) && (savedUser == null || savedUser.isEmpty())) {
                savedUser = c.getValue();
            }
        }
    }
    String uiTheme = (String) request.getAttribute(AppConfig.REQUEST_UI_THEME);
    if (uiTheme == null) uiTheme = "light";
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
<body class="auth-wrapper theme-<%= uiTheme %>">
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

          

        <div class="auth-footer">
            <p><%= AppConfig.CLINIC_ADDRESS %></p>
            <p style="margin-top: 4px;">Hotline: <%= AppConfig.CLINIC_PHONE %></p>
        </div>
    </div>

    <div id="cookieConsentBanner" class="cookie-banner" hidden>
        <div>
            <strong>Cookie notice.</strong>
            Remember-me stores your username in a cookie. Session cookies keep staff signed in.
        </div>
        <button type="button" class="btn-login" id="acceptCookiesBtn" style="width:auto;padding:8px 14px;margin:0;">Accept</button>
    </div>
    <script>
        window.SDC_COOKIE_THEME = "<%= AppConfig.COOKIE_THEME %>";
        window.SDC_COOKIE_CONSENT = "<%= AppConfig.COOKIE_CONSENT %>";
    </script>
    <script src="<%= request.getContextPath() %>/assets/js/main.js"></script>
</body>
</html>

