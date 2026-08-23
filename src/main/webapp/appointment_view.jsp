<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.sunrisedental.model.*" %>
<%@ page import="com.sunrisedental.config.AppConfig" %>
<%@ page import="com.sunrisedental.service.factory.ServiceFactory" %>
<%@ page import="com.sunrisedental.util.DateUtil" %>

<%
    String idStr = request.getParameter("id");
    Appointment app = null;
    if (idStr != null && !idStr.isEmpty()) {
        try {
            app = ServiceFactory.getAppointmentService().getAppointmentById(Integer.parseInt(idStr));
        } catch (Exception ignored) {}
    }
    request.setAttribute("pageTitle", (app != null ? "Appointment " + app.getAppointmentNumber() : "Appointment Details"));
    User viewer = (User) session.getAttribute(AppConfig.SESSION_USER);
    String viewerRole = (viewer != null && viewer.getRoleName() != null) ? viewer.getRoleName().toUpperCase() : "";
    boolean canTreat = Role.ADMIN.equalsIgnoreCase(viewerRole) || Role.DOCTOR.equalsIgnoreCase(viewerRole);
    boolean canBill = Role.ADMIN.equalsIgnoreCase(viewerRole) || Role.CASHIER.equalsIgnoreCase(viewerRole);
    boolean canCancel = Role.ADMIN.equalsIgnoreCase(viewerRole) || Role.RECEPTIONIST.equalsIgnoreCase(viewerRole);
    String backHref = Role.DOCTOR.equalsIgnoreCase(viewerRole) ? "/doctor/schedule"
            : Role.CASHIER.equalsIgnoreCase(viewerRole) ? "/billing/queue"
            : "/appointments";
%>
<jsp:include page="header.jsp" />
<jsp:include page="sidebar.jsp" />

<div class="app-main">
    <jsp:include page="topbar.jsp" />

    <main class="content-body">
        <jsp:include page="alerts.jsp" />

        <div style="margin-bottom: 20px; display: flex; justify-content: space-between; align-items: center;" class="no-print">
            <a href="<%= request.getContextPath() %><%= backHref %>" class="btn btn-outline btn-sm">
                ← Back
            </a>

            <% if (app != null) { %>
                <div style="display: flex; gap: 8px;">
                    <% if (canTreat) { %>
                    <a href="<%= request.getContextPath() %>/doctor/treatment?appointmentId=<%= app.getAppointmentId() %>" class="btn btn-primary">
                        🩺 Treat Patient
                    </a>
                    <% } %>
                    <% if (canBill) { %>
                    <% if (app.getBillId() != null && app.getBillId() > 0) { %>
                        <a href="<%= request.getContextPath() %>/billing/invoice?billId=<%= app.getBillId() %>" class="btn btn-navy">
                            🧾 View Bill
                        </a>
                    <% } else { %>
                        <a href="<%= request.getContextPath() %>/billing/generate?appointmentId=<%= app.getAppointmentId() %>" class="btn btn-navy">
                            💳 Generate Bill
                        </a>
                    <% } %>
                    <% } %>
                    <% if (canCancel && !"Cancelled".equalsIgnoreCase(app.getStatus()) && !"Completed".equalsIgnoreCase(app.getStatus())) { %>
                        <a href="<%= request.getContextPath() %>/appointments/cancel?id=<%= app.getAppointmentId() %>" 
                           class="btn btn-danger" onclick="return confirm('Are you sure you want to cancel this appointment?');">
                            Cancel Appointment
                        </a>
                    <% } %>
                </div>
            <% } %>
        </div>

        <% if (app != null) { %>
            <div class="card" style="max-width: 850px; margin: 0 auto;">
                <div class="card-header">
                    <div>
                        <div class="card-title">
                            <span>Appointment: <%= app.getAppointmentNumber() %></span>
                        </div>
                        <div style="font-size: 0.85rem; color: var(--text-muted); margin-top: 2px;">
                            Created on <%= DateUtil.formatDisplayDateTime(app.getCreatedAt()) %>
                        </div>
                    </div>
                    <div>
                        <span class="badge badge-confirmed" style="font-size: 0.9rem; padding: 6px 14px;"><%= app.getStatus() %></span>
                    </div>
                </div>

                <div class="card-body">
                    <!-- 2 Column Overview Grid -->
                    <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 24px; margin-bottom: 24px;">
                        
                        <!-- Patient Info -->
                        <div style="background-color: var(--bg-subtle); padding: 18px; border-radius: var(--radius-md);">
                            <h4 style="font-size: 0.95rem; font-weight: 700; color: var(--primary-900); margin-bottom: 12px; border-bottom: 1px solid var(--border-color); padding-bottom: 6px;">
                                👤 Patient Details
                            </h4>
                            <p style="margin-bottom: 6px;"><strong>Name:</strong> <%= app.getPatientName() %></p>
                            <p style="margin-bottom: 6px;"><strong>Code:</strong> <code><%= app.getPatientCode() %></code></p>
                            <p style="margin-bottom: 6px;"><strong>Phone:</strong> <%= app.getPatientPhone() %></p>
                            <p style="margin-bottom: 6px;"><strong>Address:</strong> <%= app.getPatientAddress() %></p>
                        </div>

                        <!-- Schedule & Doctor Info -->
                        <div style="background-color: var(--bg-subtle); padding: 18px; border-radius: var(--radius-md);">
                            <h4 style="font-size: 0.95rem; font-weight: 700; color: var(--primary-900); margin-bottom: 12px; border-bottom: 1px solid var(--border-color); padding-bottom: 6px;">
                                🩺 Consultation Details
                            </h4>
                            <p style="margin-bottom: 6px;"><strong>Dentist:</strong> <%= app.getDoctorName() %></p>
                            <p style="margin-bottom: 6px;"><strong>Surgery Room:</strong> <%= app.getDoctorRoom() %></p>
                            <p style="margin-bottom: 6px;"><strong>Date:</strong> <%= DateUtil.formatDisplayDate(app.getAppointmentDate()) %></p>
                            <p style="margin-bottom: 6px;"><strong>Time Slot:</strong> <%= DateUtil.formatDisplayTime(app.getAppointmentTime()) %></p>
                        </div>

                    </div>

                    <!-- Treatment & Clinical Information -->
                    <div style="margin-bottom: 24px;">
                        <h4 style="font-size: 0.95rem; font-weight: 700; color: var(--primary-900); margin-bottom: 12px;">
                            💊 Treatment & Clinical Notes
                        </h4>
                        <table class="data-table">
                            <tbody>
                                <tr>
                                    <td style="width: 25%;"><strong>Treatment Procedure:</strong></td>
                                    <td><%= app.getTreatmentName() %> (<%= app.getTreatmentCode() %>)</td>
                                </tr>
                                <tr>
                                    <td><strong>Tooth Chart Numbers:</strong></td>
                                    <td><strong style="color: var(--teal-700);"><%= (app.getToothNumbers() != null && !app.getToothNumbers().isEmpty()) ? app.getToothNumbers() : "Not specified yet" %></strong></td>
                                </tr>
                                <tr>
                                    <td><strong>Clinical Diagnosis:</strong></td>
                                    <td><%= (app.getClinicalNotes() != null && !app.getClinicalNotes().isEmpty()) ? app.getClinicalNotes() : "No notes recorded yet." %></td>
                                </tr>
                                <tr>
                                    <td><strong>Prescriptions:</strong></td>
                                    <td><%= (app.getPrescriptions() != null && !app.getPrescriptions().isEmpty()) ? app.getPrescriptions() : "No prescriptions." %></td>
                                </tr>
                                <tr>
                                    <td><strong>Estimated Total:</strong></td>
                                    <td style="font-weight: 800; font-size: 1.05rem; color: var(--teal-700);">LKR <%= String.format("%,.2f", app.getEstimatedTotal()) %></td>
                                </tr>
                            </tbody>
                        </table>
                    </div>

                </div>
            </div>
        <% } %>

    </main>

<jsp:include page="footer.jsp" />
