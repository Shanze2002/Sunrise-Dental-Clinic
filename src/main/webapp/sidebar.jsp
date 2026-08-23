<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.sunrisedental.config.AppConfig" %>
<%@ page import="com.sunrisedental.model.Role" %>
<%@ page import="com.sunrisedental.model.User" %>
<%
    User currentUser = (User) session.getAttribute(AppConfig.SESSION_USER);
    String role = (currentUser != null && currentUser.getRoleName() != null) ? currentUser.getRoleName().toUpperCase() : "GUEST";
    boolean isAdmin = Role.ADMIN.equalsIgnoreCase(role);
    boolean isReceptionist = Role.RECEPTIONIST.equalsIgnoreCase(role);
    boolean isDoctor = Role.DOCTOR.equalsIgnoreCase(role);
    boolean isCashier = Role.CASHIER.equalsIgnoreCase(role);
%>
<aside class="app-sidebar no-print">
    <div class="sidebar-brand">
        <div class="brand-icon">🦷</div>
        <div class="brand-text">
            <h2><%= AppConfig.CLINIC_NAME %></h2>
            <span>Colombo Dental Center</span>
        </div>
    </div>

    <div class="sidebar-user">
        <div class="user-avatar"><%= (currentUser != null && currentUser.getFullName() != null && !currentUser.getFullName().isEmpty()) ? currentUser.getFullName().substring(0, 1) : "U" %></div>
        <div class="user-info">
            <h4><%= (currentUser != null) ? currentUser.getFullName() : "Staff Member" %></h4>
            <span><%= role %></span>
        </div>
    </div>

    <nav class="sidebar-nav">

        <% if (isAdmin) { %>
            <div class="nav-section-title">ADMINISTRATION</div>
            <a href="<%= request.getContextPath() %>/dashboard" class="nav-link">
                <span class="nav-icon">⚙️</span>
                <span>Admin Dashboard</span>
            </a>
            <a href="<%= request.getContextPath() %>/admin/users" class="nav-link">
                <span class="nav-icon">👥</span>
                <span>Staff Users</span>
            </a>
            <a href="<%= request.getContextPath() %>/admin/doctors" class="nav-link">
                <span class="nav-icon">👨‍⚕️</span>
                <span>Dentists & Rooms</span>
            </a>
            <a href="<%= request.getContextPath() %>/admin/treatments" class="nav-link">
                <span class="nav-icon">💊</span>
                <span>Treatments Catalog</span>
            </a>
            <a href="<%= request.getContextPath() %>/admin/mail" class="nav-link">
                <span class="nav-icon">📧</span>
                <span>Email SMTP Setup</span>
            </a>
            <a href="<%= request.getContextPath() %>/reports/monthly" class="nav-link">
                <span class="nav-icon">📈</span>
                <span>Financial Reports</span>
            </a>
        <% } %>

        <% if (isAdmin || isReceptionist) { %>
            <div class="nav-section-title">RECEPTION & PATIENTS</div>
            <% if (isReceptionist) { %>
            <a href="<%= request.getContextPath() %>/dashboard" class="nav-link">
                <span class="nav-icon">📊</span>
                <span>Reception Desk</span>
            </a>
            <% } else { %>
            <a href="<%= request.getContextPath() %>/receptionist_dashboard.jsp" class="nav-link">
                <span class="nav-icon">📊</span>
                <span>Reception Desk</span>
            </a>
            <% } %>
            <a href="<%= request.getContextPath() %>/patients/new" class="nav-link">
                <span class="nav-icon">👤</span>
                <span>New Patient Reg.</span>
            </a>
            <a href="<%= request.getContextPath() %>/patients" class="nav-link">
                <span class="nav-icon">👥</span>
                <span>Patients Directory</span>
            </a>
            <a href="<%= request.getContextPath() %>/appointments/book" class="nav-link">
                <span class="nav-icon">📅</span>
                <span>Book Appointment</span>
            </a>
            <a href="<%= request.getContextPath() %>/appointments" class="nav-link">
                <span class="nav-icon">📋</span>
                <span>Appointments List</span>
            </a>
            <a href="<%= request.getContextPath() %>/appointments/search" class="nav-link">
                <span class="nav-icon">🔍</span>
                <span>Search Appointment</span>
            </a>
        <% } %>

        <% if (isAdmin || isDoctor) { %>
            <div class="nav-section-title">CLINICAL DENTISTRY</div>
            <a href="<%= request.getContextPath() %>/<%= isDoctor ? "dashboard" : "doctor/schedule" %>" class="nav-link">
                <span class="nav-icon">🩺</span>
                <span>Doctor's Queue</span>
            </a>
            <a href="<%= request.getContextPath() %>/appointments/search" class="nav-link">
                <span class="nav-icon">🔍</span>
                <span>Search Clinical Visit</span>
            </a>
        <% } %>

        <% if (isAdmin || isCashier) { %>
            <div class="nav-section-title">BILLING & INVOICES</div>
            <a href="<%= request.getContextPath() %>/<%= isCashier ? "dashboard" : "billing/queue" %>" class="nav-link">
                <span class="nav-icon">💳</span>
                <span>Billing Desk</span>
            </a>
            <a href="<%= request.getContextPath() %>/appointments/search" class="nav-link">
                <span class="nav-icon">🔍</span>
                <span>Search Invoices</span>
            </a>
        <% } %>

        <div class="nav-section-title">SYSTEM</div>
        <a href="<%= request.getContextPath() %>/profile.jsp" class="nav-link">
            <span class="nav-icon">👤</span>
            <span>My Profile</span>
        </a>
        <% if (isAdmin || isReceptionist) { %>
        <a href="<%= request.getContextPath() %>/notifications/email" class="nav-link">
            <span class="nav-icon">📧</span>
            <span>Email Outbox</span>
        </a>
        <% } %>
        <a href="<%= request.getContextPath() %>/help.jsp" class="nav-link">
            <span class="nav-icon">❓</span>
            <span>User Guide / Help</span>
        </a>
        <a href="<%= request.getContextPath() %>/auth/logout" class="nav-link text-danger">
            <span class="nav-icon">🚪</span>
            <span>Exit / Logout</span>
        </a>
    </nav>
</aside>
