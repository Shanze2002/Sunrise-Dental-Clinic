<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.sunrisedental.model.*" %>
<%@ page import="com.sunrisedental.service.factory.ServiceFactory" %>
<%@ page import="com.sunrisedental.util.DateUtil" %>

<%
    List<Appointment> todayApps = (List<Appointment>) request.getAttribute("todayAppointments");
    if (todayApps == null) {
        todayApps = ServiceFactory.getAppointmentService().getTodayAppointments();
    }

    int totalPatients = ServiceFactory.getPatientService().getTotalPatientsCount();
    List<Doctor> doctors = ServiceFactory.getDoctorService().getActiveDoctors();
    request.setAttribute("pageTitle", "Reception Desk & Appointments");
%>
<jsp:include page="header.jsp" />
<jsp:include page="sidebar.jsp" />

<div class="app-main">
    <jsp:include page="topbar.jsp" />

    <main class="content-body">
        <jsp:include page="alerts.jsp" />

        <!-- Quick Stats -->
        <div class="stats-grid">
            <div class="stat-card">
                <div class="stat-icon blue">📅</div>
                <div class="stat-details">
                    <div class="stat-value"><%= todayApps != null ? todayApps.size() : 0 %></div>
                    <div class="stat-label">Today's Appointments</div>
                </div>
            </div>

            <div class="stat-card">
                <div class="stat-icon teal">👥</div>
                <div class="stat-details">
                    <div class="stat-value"><%= totalPatients %></div>
                    <div class="stat-label">Registered Patients</div>
                </div>
            </div>

            <div class="stat-card">
                <div class="stat-icon emerald">👨‍⚕️</div>
                <div class="stat-details">
                    <div class="stat-value"><%= doctors != null ? doctors.size() : 0 %></div>
                    <div class="stat-label">Dentists on Duty</div>
                </div>
            </div>
        </div>

        <!-- Action Bar -->
        <div style="display: flex; gap: 12px; margin-bottom: 24px; flex-wrap: wrap;">
            <a href="<%= request.getContextPath() %>/book_appointment.jsp" class="btn btn-primary">
                <span>➕ Book New Appointment</span>
            </a>
            <a href="<%= request.getContextPath() %>/patient_register.jsp" class="btn btn-navy">
                <span>👤 Register New Patient</span>
            </a>
            <a href="<%= request.getContextPath() %>/search_appointment.jsp" class="btn btn-outline">
                <span>🔍 Search Appointment / Patient</span>
            </a>
        </div>

        <!-- Today's Queue Table -->
        <div class="card">
            <div class="card-header">
                <div class="card-title">
                    <span>🕒 Today's Appointment Queue (<%= DateUtil.formatDisplayDate(new java.sql.Date(System.currentTimeMillis())) %>)</span>
                </div>
                <input type="text" id="tableSearchInput" class="form-control" placeholder="Quick filter queue..." style="width: 250px; padding: 6px 12px;">
            </div>
            <div class="card-body" style="padding: 0;">
                <div class="table-responsive">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Appt #</th>
                                <th>Time</th>
                                <th>Patient Name</th>
                                <th>Contact</th>
                                <th>Dentist</th>
                                <th>Treatment Type</th>
                                <th>Status</th>
                                <th>Billing</th>
                                <th style="text-align: right;">Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                if (todayApps != null && !todayApps.isEmpty()) {
                                    for (Appointment a : todayApps) {
                                        String badgeClass = "badge-scheduled";
                                        if ("Confirmed".equalsIgnoreCase(a.getStatus())) badgeClass = "badge-confirmed";
                                        else if ("In-Treatment".equalsIgnoreCase(a.getStatus())) badgeClass = "badge-in-treatment";
                                        else if ("Completed".equalsIgnoreCase(a.getStatus())) badgeClass = "badge-completed";
                                        else if ("Cancelled".equalsIgnoreCase(a.getStatus())) badgeClass = "badge-cancelled";
                            %>
                                <tr>
                                    <td>
                                        <a href="<%= request.getContextPath() %>/appointment_view.jsp?id=<%= a.getAppointmentId() %>" style="font-weight: 700; color: var(--teal-700); text-decoration: none;">
                                            <%= a.getAppointmentNumber() %>
                                        </a>
                                    </td>
                                    <td><strong><%= DateUtil.formatDisplayTime(a.getAppointmentTime()) %></strong></td>
                                    <td>
                                        <strong><%= a.getPatientName() %></strong>
                                        <div style="font-size: 0.75rem; color: var(--text-muted);"><%= a.getPatientCode() %></div>
                                    </td>
                                    <td><%= a.getPatientPhone() %></td>
                                    <td>
                                        <%= a.getDoctorName() %>
                                        <div style="font-size: 0.75rem; color: var(--text-muted);"><%= a.getDoctorRoom() %></div>
                                    </td>
                                    <td><%= a.getTreatmentName() %></td>
                                    <td>
                                        <span class="badge <%= badgeClass %>"><%= a.getStatus() %></span>
                                    </td>
                                    <td>
                                        <% if ("Paid".equalsIgnoreCase(a.getBillingStatus())) { %>
                                            <span class="badge badge-paid">Paid</span>
                                        <% } else if ("Unpaid".equalsIgnoreCase(a.getBillingStatus())) { %>
                                            <span class="badge badge-unpaid">Unpaid</span>
                                        <% } else { %>
                                            <span class="badge" style="background:#f1f5f9; color:#64748b;">Unbilled</span>
                                        <% } %>
                                    </td>
                                    <td style="text-align: right;">
                                        <a href="<%= request.getContextPath() %>/appointment_view.jsp?id=<%= a.getAppointmentId() %>" class="btn btn-outline btn-sm">
                                            View
                                        </a>
                                    </td>
                                </tr>
                            <%
                                    }
                                } else {
                            %>
                                <tr>
                                    <td colspan="9" style="text-align: center; padding: 32px; color: var(--text-muted);">
                                        No appointments scheduled for today yet. Click "Book New Appointment" to schedule one.
                                    </td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

    </main>

<jsp:include page="footer.jsp" />
