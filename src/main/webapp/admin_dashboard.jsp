<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.sunrisedental.model.*" %>
<%@ page import="com.sunrisedental.service.factory.ServiceFactory" %>
<%@ page import="com.sunrisedental.util.DateUtil" %>

<%
    int totalPatients = ServiceFactory.getPatientService().getTotalPatientsCount();
    int todayAppsCount = ServiceFactory.getAppointmentService().getTodayAppointmentsCount();
    int totalAppsCount = ServiceFactory.getAppointmentService().getTotalAppointmentsCount();
    double todayRev = ServiceFactory.getBillingService().getTodayRevenue();
    List<Appointment> todayApps = ServiceFactory.getAppointmentService().getTodayAppointments();
    List<AuditLog> auditLogs = ServiceFactory.getAuditService().getRecentLogs(8);
    List<Doctor> doctors = ServiceFactory.getDoctorService().getActiveDoctors();
    request.setAttribute("pageTitle", "Clinic Director & Admin Dashboard");
%>
<jsp:include page="header.jsp" />
<jsp:include page="sidebar.jsp" />

<div class="app-main">
    <jsp:include page="topbar.jsp" />

    <main class="content-body">
        <jsp:include page="alerts.jsp" />

        <!-- Executive Metrics Grid -->
        <div class="stats-grid">
            <div class="stat-card">
                <div class="stat-icon emerald">💵</div>
                <div class="stat-details">
                    <div class="stat-value">LKR <%= String.format("%,.2f", todayRev) %></div>
                    <div class="stat-label">Today's Revenue</div>
                </div>
            </div>

            <div class="stat-card">
                <div class="stat-icon blue">📅</div>
                <div class="stat-details">
                    <div class="stat-value"><%= todayAppsCount %></div>
                    <div class="stat-label">Today's Appointments</div>
                </div>
            </div>

            <div class="stat-card">
                <div class="stat-icon teal">👥</div>
                <div class="stat-details">
                    <div class="stat-value"><%= totalPatients %></div>
                    <div class="stat-label">Total Patients</div>
                </div>
            </div>

            <div class="stat-card">
                <div class="stat-icon amber">🩺</div>
                <div class="stat-details">
                    <div class="stat-value"><%= doctors != null ? doctors.size() : 0 %></div>
                    <div class="stat-label">Active Dentists</div>
                </div>
            </div>
        </div>

        <!-- Navigation Action Bar -->
        <div style="display: flex; gap: 12px; margin-bottom: 24px; flex-wrap: wrap;">
            <a href="<%= request.getContextPath() %>/admin_users.jsp" class="btn btn-navy">
                <span>👤 Manage Staff Users</span>
            </a>
            <a href="<%= request.getContextPath() %>/admin_reports.jsp" class="btn btn-primary">
                <span>📈 Monthly Financial Report</span>
            </a>
            <a href="<%= request.getContextPath() %>/admin_doctors.jsp" class="btn btn-outline">
                <span>👨‍⚕️ Dentists & Room Allocation</span>
            </a>
            <a href="<%= request.getContextPath() %>/admin_treatments.jsp" class="btn btn-outline">
                <span>💊 Treatments Catalog</span>
            </a>
        </div>

        <div style="display: grid; grid-template-columns: 2fr 1fr; gap: 24px;">
            <!-- Today's Appointments -->
            <div class="card">
                <div class="card-header">
                    <div class="card-title">
                        <span>🕒 Today's Appointments Queue</span>
                    </div>
                    <a href="<%= request.getContextPath() %>/appointments_list.jsp" class="btn btn-outline btn-sm">View All</a>
                </div>
                <div class="card-body" style="padding: 0;">
                    <div class="table-responsive">
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th>Appt #</th>
                                    <th>Time</th>
                                    <th>Patient</th>
                                    <th>Dentist</th>
                                    <th>Status</th>
                                    <th>Est. Fee</th>
                                </tr>
                            </thead>
                            <tbody>
                                <%
                                    if (todayApps != null && !todayApps.isEmpty()) {
                                        for (Appointment a : todayApps) {
                                %>
                                    <tr>
                                        <td><strong><%= a.getAppointmentNumber() %></strong></td>
                                        <td><%= DateUtil.formatDisplayTime(a.getAppointmentTime()) %></td>
                                        <td><%= a.getPatientName() %></td>
                                        <td><%= a.getDoctorName() %></td>
                                        <td><span class="badge badge-confirmed"><%= a.getStatus() %></span></td>
                                        <td>LKR <%= String.format("%,.2f", a.getEstimatedTotal()) %></td>
                                    </tr>
                                <%
                                        }
                                    } else {
                                %>
                                    <tr>
                                        <td colspan="6" style="text-align: center; padding: 24px; color: var(--text-muted);">
                                            No appointments scheduled for today yet.
                                        </td>
                                    </tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>

            <!-- Audit Trail -->
            <div class="card">
                <div class="card-header">
                    <div class="card-title">
                        <span>🛡️ System Audit Trail</span>
                    </div>
                </div>
                <div class="card-body" style="padding: 0;">
                    <div class="table-responsive">
                        <table class="data-table" style="font-size: 0.8rem;">
                            <thead>
                                <tr>
                                    <th>Action</th>
                                    <th>Details</th>
                                    <th>Timestamp</th>
                                </tr>
                            </thead>
                            <tbody>
                                <%
                                    if (auditLogs != null && !auditLogs.isEmpty()) {
                                        for (AuditLog l : auditLogs) {
                                %>
                                    <tr>
                                        <td><strong style="color: var(--teal-700);"><%= l.getAction() %></strong></td>
                                        <td><%= l.getDetails() != null ? l.getDetails() : "-" %></td>
                                        <td style="color: var(--text-muted);"><%= DateUtil.formatDisplayDateTime(l.getCreatedAt()) %></td>
                                    </tr>
                                <%
                                        }
                                    } else {
                                %>
                                    <tr>
                                        <td colspan="3" style="text-align: center; padding: 20px; color: var(--text-muted);">
                                            No recent logs.
                                        </td>
                                    </tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>

    </main>

<jsp:include page="footer.jsp" />
