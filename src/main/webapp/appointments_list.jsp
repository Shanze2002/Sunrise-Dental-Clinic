<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.sunrisedental.model.Appointment" %>
<%@ page import="com.sunrisedental.service.factory.ServiceFactory" %>
<%@ page import="com.sunrisedental.util.DateUtil" %>

<%
    String status = request.getParameter("status");
    List<Appointment> apps;
    if (status != null && !status.isEmpty()) {
        apps = ServiceFactory.getAppointmentService().getAppointmentsByStatus(status);
    } else {
        apps = ServiceFactory.getAppointmentService().getAllAppointments();
    }
    request.setAttribute("pageTitle", "Appointments Master Schedule");
%>
<jsp:include page="header.jsp" />
<jsp:include page="sidebar.jsp" />

<div class="app-main">
    <jsp:include page="topbar.jsp" />

    <main class="content-body">
        <jsp:include page="alerts.jsp" />

        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; flex-wrap: wrap; gap: 12px;">
            <div>
                <h2 style="font-size: 1.25rem; font-weight: 700; color: var(--primary-900);">Clinical Appointments Master List</h2>
                <p style="font-size: 0.85rem; color: var(--text-muted);">Manage scheduled appointments, statuses, and clinical queues.</p>
            </div>
            <div style="display: flex; gap: 8px;">
                <a href="<%= request.getContextPath() %>/book_appointment.jsp" class="btn btn-primary">
                    <span>➕ Book New Appointment</span>
                </a>
                <a href="<%= request.getContextPath() %>/search_appointment.jsp" class="btn btn-outline">
                    <span>🔍 Search Appointment</span>
                </a>
            </div>
        </div>

        <div class="card">
            <div class="card-header">
                <div class="card-title">
                    <span>📋 All Scheduled Appointments</span>
                </div>
                <input type="text" id="tableSearchInput" class="form-control" placeholder="Search table..." style="width: 220px; padding: 6px 12px;">
            </div>

            <div class="card-body" style="padding: 0;">
                <div class="table-responsive">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Appt Number</th>
                                <th>Date & Time</th>
                                <th>Patient Name</th>
                                <th>Dentist</th>
                                <th>Treatment</th>
                                <th>Status</th>
                                <th>Billing</th>
                                <th style="text-align: right;">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                if (apps != null && !apps.isEmpty()) {
                                    for (Appointment a : apps) {
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
                                    <td>
                                        <strong><%= DateUtil.formatDisplayDate(a.getAppointmentDate()) %></strong>
                                        <div style="font-size: 0.78rem; color: var(--text-muted);"><%= DateUtil.formatDisplayTime(a.getAppointmentTime()) %></div>
                                    </td>
                                    <td>
                                        <strong><%= a.getPatientName() %></strong>
                                        <div style="font-size: 0.75rem; color: var(--text-muted);"><%= a.getPatientPhone() %></div>
                                    </td>
                                    <td><%= a.getDoctorName() %></td>
                                    <td><%= a.getTreatmentName() %></td>
                                    <td><span class="badge <%= badgeClass %>"><%= a.getStatus() %></span></td>
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
                                            View Details
                                        </a>
                                    </td>
                                </tr>
                            <%
                                    }
                                } else {
                            %>
                                <tr>
                                    <td colspan="8" style="text-align: center; padding: 32px; color: var(--text-muted);">
                                        No appointments scheduled yet.
                                    </td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            </div>
    </main>

<jsp:include page="footer.jsp" />
