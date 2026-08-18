<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ page import="com.sunrisedental.config.AppConfig" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>404 - Page Not Found | <%= AppConfig.CLINIC_NAME %></title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/main.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/auth.css">
</head>
<body class="auth-wrapper">
    <div class="auth-card" style="text-align: center; padding: 40px 30px;">
        <div style="font-size: 54px; margin-bottom: 12px;">🔍</div>
        <h1 style="font-size: 1.8rem; font-weight: 800; color: var(--primary-900);">404 - Page Not Found</h1>
        <p style="color: var(--text-muted); margin: 12px 0 24px; font-size: 0.95rem;">
            The clinic page or record you are trying to access does not exist or has been moved.
        </p>
        <a href="<%= request.getContextPath() %>/login.jsp" class="btn btn-primary" style="padding: 10px 24px;">
            ➔ Return to Main Dashboard
        </a>
    </div>
</body>
</html>
