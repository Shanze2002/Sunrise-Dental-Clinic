<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.sunrisedental.model.*" %>
<%@ page import="com.sunrisedental.service.factory.ServiceFactory" %>
<%@ page import="com.sunrisedental.util.DateUtil" %>

<%
    String idStr = request.getParameter("id");
    Patient patient = null;
    List<Appointment> history = null;
    if (idStr != null && !idStr.isEmpty()) {
        try {
            int pid = Integer.parseInt(idStr);
            patient = ServiceFactory.getPatientService().getPatientById(pid);
            history = ServiceFactory.getAppointmentService().getPatientAppointmentHistory(pid);
        } catch (Exception ignored) {}
    }
    request.setAttribute("pageTitle", (patient != null ? patient.getFullName() : "Patient Profile"));
%>
<jsp:include page="header.jsp" />
<jsp:include page="sidebar.jsp" />

<div class="app-main">
    <jsp:include page="topbar.jsp" />

    <main class="content-body">
        <jsp:include page="alerts.jsp" />

        <div style="margin-bottom: 20px; display: flex; justify-content: space-between; align-items: center;">
            <a href="<%= request.getContextPath() %>/patient_list.jsp" class="btn btn-outline btn-sm">
                ← Back to Patients
            </a>

            <% if (patient != null) { %>
                <div style="display: flex; gap: 8px;">
                    <a href="<%= request.getContextPath() %>/book_appointment.jsp?patientId=<%= patient.getPatientId() %>" class="btn btn-primary">
                        📅 Book New Appointment
                    </a>
                    <a href="<%= request.getContextPath() %>/patient_register.jsp?id=<%= patient.getPatientId() %>" class="btn btn-outline">
                        ✏️ Edit Details
                    </a>
                </div>
            <% } %>
        </div>

        <% if (patient != null) { %>
            <!-- Patient Profile Card -->
            <div class="card" style="margin-bottom: 24px;">
                <div class="card-header">
                    <div>
                        <span class="badge badge-confirmed"><%= patient.getPatientCode() %></span>
                        <h2 style="font-size: 1.4rem; font-weight: 800; color: var(--primary-900); margin-top: 6px;">
                            <%= patient.getFullName() %>
                        </h2>
                    </div>
                </div>

                <div class="card-body">
                    <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 16px; font-size: 0.9rem;">
                        <div>
                            <div class="form-label">Phone</div>
                            <div style="font-weight: 600;"><%= patient.getPhone() %></div>
                        </div>
                        <div>
                            <div class="form-label">NIC / Passport</div>
                            <div><%= patient.getNicPassport() != null ? patient.getNicPassport() : "-" %></div>
                        </div>
                        <div>
                            <div class="form-label">Gender</div>
                            <div><%= patient.getGender() %></div>
                        </div>
                        <div>
                            <div class="form-label">Date of Birth</div>
                            <div><%= patient.getDob() != null ? patient.getDob().toString() : "-" %></div>
                        </div>
                        <div style="grid-column: span 2;">
                            <div class="form-label">Address</div>
                            <div><%= patient.getAddress() %></div>
                        </div>
                        <div>
                            <div class="form-label">Emergency Contact</div>
                            <div><%= patient.getEmergencyContact() != null ? patient.getEmergencyContact() : "-" %></div>
                        </div>
                        <div>
                            <div class="form-label">Known Allergies</div>
                            <div style="color: #b91c1c; font-weight: 700;"><%= patient.getAllergies() != null ? patient.getAllergies() : "None" %></div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Treatment & Appointment History -->
            <div class="card">
                <div class="card-header">
                    <div class="card-title">
                        <span>📜 Clinical Visit History</span>
                    </div>
                </div>

                <div class="card-body" style="padding: 0;">
                    <div class="table-responsive">
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th>Appt #</th>
                                    <th>Date</th>
                                    <th>Time</th>
                                    <th>Dentist</th>
                                    <th>Treatment</th>
                                    <th>Status</th>
                                    <th>Fee</th>
                                    <th style="text-align: right;">Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <%
                                    if (history != null && !history.isEmpty()) {
                                        for (Appointment a : history) {
                                %>
                                    <tr>
                                        <td><strong><%= a.getAppointmentNumber() %></strong></td>
                                        <td><%= DateUtil.formatDisplayDate(a.getAppointmentDate()) %></td>
                                        <td><%= DateUtil.formatDisplayTime(a.getAppointmentTime()) %></td>
                                        <td><%= a.getDoctorName() %></td>
                                        <td><%= a.getTreatmentName() %></td>
                                        <td><span class="badge badge-confirmed"><%= a.getStatus() %></span></td>
                                        <td>LKR <%= String.format("%,.2f", a.getEstimatedTotal()) %></td>
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
                                        <td colspan="8" style="text-align: center; padding: 24px; color: var(--text-muted);">
                                            No past appointments for this patient.
                                        </td>
                                    </tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        <% } %>
    </main>

<jsp:include page="footer.jsp" />