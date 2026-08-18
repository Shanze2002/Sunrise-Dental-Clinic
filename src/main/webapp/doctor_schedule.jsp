<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.sunrisedental.model.*" %>
<%@ page import="com.sunrisedental.config.AppConfig" %>
<%@ page import="com.sunrisedental.service.factory.ServiceFactory" %>
<%@ page import="com.sunrisedental.util.DateUtil" %>

<%
    User logged = (User) session.getAttribute(AppConfig.SESSION_USER);
    Doctor doc = null;
    if (logged != null) {
        doc = ServiceFactory.getDoctorService().getDoctorByUserId(logged.getUserId());
    }
    if (doc == null) {
        List<Doctor> allDocs = ServiceFactory.getDoctorService().getActiveDoctors();
        if (allDocs != null && !allDocs.isEmpty()) {
            doc = allDocs.get(0);
        }
    }

    String dateStr = request.getParameter("date");
    java.sql.Date scheduleDate = DateUtil.parseSqlDate(dateStr);
    if (scheduleDate == null) {
        scheduleDate = DateUtil.parseSqlDate(DateUtil.getCurrentSqlDate());
    }

    List<Appointment> schedule = null;
    if (doc != null) {
        schedule = ServiceFactory.getAppointmentService().getDoctorDailySchedule(doc.getDoctorId(), scheduleDate);
    }
    request.setAttribute("pageTitle", "Dentist Clinical Schedule");
%>
<jsp:include page="header.jsp" />
<jsp:include page="sidebar.jsp" />

<div class="app-main">
    <jsp:include page="topbar.jsp" />

    <main class="content-body">
        <jsp:include page="alerts.jsp" />

        <!-- Doctor Profile Card Banner -->
        <% if (doc != null) { %>
            <div style="background: linear-gradient(135deg, var(--primary-900), var(--primary-800)); border-radius: var(--radius-lg); padding: 24px 30px; color: #fff; margin-bottom: 28px; display: flex; justify-content: space-between; align-items: center; box-shadow: var(--shadow-md);">
                <div>
                    <span style="background: rgba(20, 184, 166, 0.25); color: var(--teal-400); padding: 4px 10px; border-radius: 9999px; font-size: 0.75rem; font-weight: 700; text-transform: uppercase;">
                        <%= doc.getSpecialization() %>
                    </span>
                    <h2 style="font-size: 1.4rem; font-weight: 800; margin-top: 6px;"><%= doc.getDoctorName() %></h2>
                    <p style="color: #94a3b8; font-size: 0.85rem; margin-top: 2px;">
                        License: <%= doc.getLicenseNumber() %> • <%= doc.getRoomNumber() %> • Consultation Fee: LKR <%= String.format("%,.2f", doc.getConsultationFee()) %>
                    </p>
                </div>
                <div>
                    <form action="<%= request.getContextPath() %>/doctor_schedule.jsp" method="GET" style="display: flex; gap: 8px; align-items: center;">
                        <input type="date" name="date" class="form-control" value="<%= scheduleDate.toString() %>" style="width: 170px;">
                        <button type="submit" class="btn btn-primary btn-sm">Filter</button>
                    </form>
                </div>
            </div>
        <% } %>

        <!-- Schedule Table -->
        <div class="card">
            <div class="card-header">
                <div class="card-title">
                    <span>🩺 Patient Queue for <%= DateUtil.formatDisplayDate(scheduleDate) %></span>
                </div>
                <input type="text" id="tableSearchInput" class="form-control" placeholder="Search patient..." style="width: 220px; padding: 6px 12px;">
            </div>

            <div class="card-body" style="padding: 0;">
                <div class="table-responsive">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Slot Time</th>
                                <th>Appointment #</th>
                                <th>Patient Name</th>
                                <th>Treatment</th>
                                <th>Tooth #</th>
                                <th>Status</th>
                                <th style="text-align: right;">Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                if (schedule != null && !schedule.isEmpty()) {
                                    for (Appointment a : schedule) {
                                        String badgeClass = "badge-scheduled";
                                        if ("Confirmed".equalsIgnoreCase(a.getStatus())) badgeClass = "badge-confirmed";
                                        else if ("In-Treatment".equalsIgnoreCase(a.getStatus())) badgeClass = "badge-in-treatment";
                                        else if ("Completed".equalsIgnoreCase(a.getStatus())) badgeClass = "badge-completed";
                                        else if ("Cancelled".equalsIgnoreCase(a.getStatus())) badgeClass = "badge-cancelled";
                            %>
                                <tr>
                                    <td><strong>🕒 <%= DateUtil.formatDisplayTime(a.getAppointmentTime()) %></strong></td>
                                    <td>
                                        <a href="<%= request.getContextPath() %>/doctor_treatment.jsp?appointmentId=<%= a.getAppointmentId() %>" style="font-weight: 700; color: var(--teal-700); text-decoration: none;">
                                            <%= a.getAppointmentNumber() %>
                                        </a>
                                    </td>
                                    <td>
                                        <strong><%= a.getPatientName() %></strong>
                                        <div style="font-size: 0.75rem; color: var(--text-muted);"><%= a.getPatientCode() %></div>
                                    </td>
                                    <td><%= a.getTreatmentName() %></td>
                                    <td><%= (a.getToothNumbers() != null && !a.getToothNumbers().isEmpty()) ? a.getToothNumbers() : "-" %></td>
                                    <td><span class="badge <%= badgeClass %>"><%= a.getStatus() %></span></td>
                                    <td style="text-align: right;">
                                        <a href="<%= request.getContextPath() %>/doctor_treatment.jsp?appointmentId=<%= a.getAppointmentId() %>" class="btn btn-primary btn-sm">
                                            Treat Patient ➔
                                        </a>
                                    </td>
                                </tr>
                            <%
                                    }
                                } else {
                            %>
                                <tr>
                                    <td colspan="7" style="text-align: center; padding: 32px; color: var(--text-muted);">
                                        No patients scheduled for this date.
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
