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

        <%-- 1. ADMIN SECTION (Visible to ADMIN only) --%>
        <% if (isAdmin) { %>
            <div class="nav-section-title">ADMINISTRATION</div>
            <a href="<%= request.getContextPath() %>/admin_dashboard.jsp" class="nav-link">
                <span class="nav-icon">⚙️</span>
                <span>Admin Dashboard</span>
            </a>
            <a href="<%= request.getContextPath() %>/admin_users.jsp" class="nav-link">
                <span class="nav-icon">👥</span>
                <span>Staff Users</span>
            </a>
            <a href="<%= request.getContextPath() %>/admin_doctors.jsp" class="nav-link">
                <span class="nav-icon">👨‍⚕️</span>
                <span>Dentists & Rooms</span>
            </a>
            <a href="<%= request.getContextPath() %>/admin_treatments.jsp" class="nav-link">
                <span class="nav-icon">💊</span>
                <span>Treatments Catalog</span>
            </a>
            <a href="<%= request.getContextPath() %>/admin_reports.jsp" class="nav-link">
                <span class="nav-icon">📈</span>
                <span>Financial Reports</span>
            </a>
        <% } %>

        <%-- 2. RECEPTION & PATIENTS SECTION (Visible to ADMIN and RECEPTIONIST) --%>
        <% if (isAdmin || isReceptionist) { %>
            <div class="nav-section-title">RECEPTION & PATIENTS</div>
            <a href="<%= request.getContextPath() %>/receptionist_dashboard.jsp" class="nav-link">
                <span class="nav-icon">📊</span>
                <span>Reception Desk</span>
            </a>
            <a href="<%= request.getContextPath() %>/patient_register.jsp" class="nav-link">
                <span class="nav-icon">👤</span>
                <span>New Patient Reg.</span>
            </a>
            <a href="<%= request.getContextPath() %>/patient_list.jsp" class="nav-link">
                <span class="nav-icon">👥</span>
                <span>Patients Directory</span>
            </a>
            <a href="<%= request.getContextPath() %>/book_appointment.jsp" class="nav-link">
                <span class="nav-icon">📅</span>
                <span>Book Appointment</span>
            </a>
            <a href="<%= request.getContextPath() %>/appointments_list.jsp" class="nav-link">
                <span class="nav-icon">📋</span>
                <span>Appointments List</span>
            </a>
            <a href="<%= request.getContextPath() %>/search_appointment.jsp" class="nav-link">
                <span class="nav-icon">🔍</span>
                <span>Search Appointment</span>
            </a>
        <% } %>

        <%-- 3. CLINICAL DENTISTRY SECTION (Visible to ADMIN and DOCTOR) --%>
        <% if (isAdmin || isDoctor) { %>
            <div class="nav-section-title">CLINICAL DENTISTRY</div>
            <a href="<%= request.getContextPath() %>/doctor_schedule.jsp" class="nav-link">
                <span class="nav-icon">🩺</span>
                <span>Doctor's Queue</span>
            </a>
            <% if (isDoctor && !isAdmin) { %>
                <a href="<%= request.getContextPath() %>/search_appointment.jsp" class="nav-link">
                    <span class="nav-icon">🔍</span>
                    <span>Search Clinical Visit</span>
                </a>
            <% } %>
        <% } %>

        <%-- 4. BILLING & CASHIER SECTION (Visible to ADMIN and CASHIER) --%>
        <% if (isAdmin || isCashier) { %>
            <div class="nav-section-title">BILLING & INVOICES</div>
            <a href="<%= request.getContextPath() %>/cashier_billing.jsp" class="nav-link">
                <span class="nav-icon">💳</span>
                <span>Billing Desk</span>
            </a>
            <% if (isCashier && !isAdmin) { %>
                <a href="<%= request.getContextPath() %>/search_appointment.jsp" class="nav-link">
                    <span class="nav-icon">🔍</span>
                    <span>Search Invoices</span>
                </a>
            <% } %>
        <% } %>

        <%-- 5. COMMON SYSTEM SECTION (Visible to all logged in users) --%>
        <div class="nav-section-title">SYSTEM</div>
        <a href="<%= request.getContextPath() %>/profile.jsp" class="nav-link">
            <span class="nav-icon">👤</span>
            <span>My Profile</span>
        </a>
        <a href="<%= request.getContextPath() %>/help.jsp" class="nav-link">
            <span class="nav-icon">❓</span>
            <span>User Guide / Help</span>
        </a>
        <a href="<%= request.getContextPath() %>/login.jsp?msg=logged_out" class="nav-link text-danger">
            <span class="nav-icon">🚪</span>
            <span>Exit / Logout</span>
        </a>
    </nav>
</aside>
