<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.sunrisedental.model.Patient" %>
<%@ page import="com.sunrisedental.service.factory.ServiceFactory" %>
<%@ page import="com.sunrisedental.util.DateUtil" %>

<%
    String q = request.getParameter("q");
    List<Patient> patients;
    if (q != null && !q.trim().isEmpty()) {
        patients = ServiceFactory.getPatientService().searchPatients(q);
    } else {
        patients = ServiceFactory.getPatientService().getAllPatients();
    }
    request.setAttribute("pageTitle", "Patients Directory");
%>
<jsp:include page="header.jsp" />
<jsp:include page="sidebar.jsp" />

<div class="app-main">
    <jsp:include page="topbar.jsp" />

    <main class="content-body">
        <jsp:include page="alerts.jsp" />

        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; flex-wrap: wrap; gap: 12px;">
            <div>
                <h2 style="font-size: 1.25rem; font-weight: 700; color: var(--primary-900);">Patient Medical Records Directory</h2>
                <p style="font-size: 0.85rem; color: var(--text-muted);">Manage patient master files, contact details, and dental histories.</p>
            </div>
            <a href="<%= request.getContextPath() %>/patient_register.jsp" class="btn btn-primary">
                <span>➕ Register New Patient</span>
            </a>
        </div>

        <div class="card">
            <div class="card-header">
                <div class="card-title">
                    <span>👥 Registered Patients (<%= (patients != null) ? patients.size() : 0 %>)</span>
                </div>
                <form action="<%= request.getContextPath() %>/patient_list.jsp" method="GET" style="display: flex; gap: 8px;">
                    <input type="text" name="q" class="form-control" placeholder="Search by name, phone, NIC..." value="<%= (q != null) ? q : "" %>" style="width: 250px; padding: 6px 12px;">
                    <button type="submit" class="btn btn-outline btn-sm">Search</button>
                </form>
            </div>

            <div class="card-body" style="padding: 0;">
                <div class="table-responsive">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Patient Code</th>
                                <th>Full Name</th>
                                <th>Contact Phone</th>
                                <th>NIC / Passport</th>
                                <th>Gender</th>
                                <th>Allergies</th>
                                <th>Registered Date</th>
                                <th style="text-align: right;">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                if (patients != null && !patients.isEmpty()) {
                                    for (Patient p : patients) {
                            %>
                                <tr>
                                    <td><strong><%= p.getPatientCode() %></strong></td>
                                    <td>
                                        <a href="<%= request.getContextPath() %>/patient_view.jsp?id=<%= p.getPatientId() %>" style="font-weight: 700; color: var(--teal-700); text-decoration: none;">
                                            <%= p.getFullName() %>
                                        </a>
                                    </td>
                                    <td><%= p.getPhone() %></td>
                                    <td><%= (p.getNicPassport() != null && !p.getNicPassport().isEmpty()) ? p.getNicPassport() : "-" %></td>
                                    <td><%= p.getGender() %></td>
                                    <td>
                                        <% if (p.getAllergies() != null && !p.getAllergies().equalsIgnoreCase("None")) { %>
                                            <span class="badge badge-unpaid">⚠️ <%= p.getAllergies() %></span>
                                        <% } else { %>
                                            <span style="color: var(--text-muted); font-size: 0.8rem;">None</span>
                                        <% } %>
                                    </td>
                                    <td><%= DateUtil.formatDisplayDate(p.getCreatedAt()) %></td>
                                    <td style="text-align: right;">
                                        <div style="display: inline-flex; gap: 6px;">
                                            <a href="<%= request.getContextPath() %>/book_appointment.jsp?patientId=<%= p.getPatientId() %>" class="btn btn-primary btn-sm" title="Book Appointment">
                                                📅 Book
                                            </a>
                                            <a href="<%= request.getContextPath() %>/patient_register.jsp?id=<%= p.getPatientId() %>" class="btn btn-outline btn-sm" title="Edit Patient">
                                                ✏️ Edit
                                            </a>
                                        </div>
                                    </td>
                                </tr>
                            <%
                                    }
                                } else {
                            %>
                                <tr>
                                    <td colspan="8" style="text-align: center; padding: 32px; color: var(--text-muted);">
                                        No patient records found.
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
