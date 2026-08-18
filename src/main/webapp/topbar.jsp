<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.sunrisedental.config.AppConfig" %>
<%@ page import="com.sunrisedental.model.User" %>
<%@ page import="com.sunrisedental.util.DateUtil" %>
<%
    User topUser = (User) session.getAttribute(AppConfig.SESSION_USER);
%>
<header class="app-topbar no-print">
    <div class="topbar-left">
        <h1 class="page-heading"><%= request.getAttribute("pageTitle") != null ? request.getAttribute("pageTitle") : AppConfig.CLINIC_NAME %></h1>
    </div>

    <div class="topbar-right">
        <div class="topbar-time">
            📅 <%= DateUtil.formatDisplayDate(new java.sql.Date(System.currentTimeMillis())) %>
        </div>

        <div class="topbar-badge">
            Role: <strong><%= (topUser != null) ? topUser.getRoleName() : "Staff" %></strong>
        </div>

        <a href="<%= request.getContextPath() %>/profile.jsp" class="btn btn-outline btn-sm">
            👤 Profile
        </a>

        <a href="<%= request.getContextPath() %>/login.jsp?msg=logged_out" class="btn btn-danger btn-sm">
            🚪 Exit
        </a>
    </div>
</header>
