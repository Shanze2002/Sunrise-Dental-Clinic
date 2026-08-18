<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ page import="com.sunrisedental.config.AppConfig" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>500 - System Exception | <%= AppConfig.CLINIC_NAME %></title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/main.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/auth.css">
</head>
<body class="auth-wrapper">
    <div class="auth-card" style="text-align: center; padding: 40px 30px;">
        <div style="font-size: 54px; margin-bottom: 12px;">⚠️</div>
        <h1 style="font-size: 1.8rem; font-weight: 800; color: #b91c1c;">500 - Application Error</h1>
        <p style="color: var(--text-muted); margin: 12px 0 24px; font-size: 0.95rem;">
            An unexpected server error occurred. Please contact the clinic technical administrator.
        </p>
        <a href="<%= request.getContextPath() %>/login.jsp" class="btn btn-primary" style="padding: 10px 24px;">
            ➔ Return to Safe Portal
        </a>
    </div>
</body>
</html>
