<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.sunrisedental.model.Appointment" %>
<%@ page import="com.sunrisedental.service.factory.ServiceFactory" %>
<%@ page import="com.sunrisedental.util.DateUtil" %>

<%
    String q = request.getParameter("q");
    List<Appointment> results = null;
    if (q != null && !q.trim().isEmpty()) {
        results = ServiceFactory.getAppointmentService().searchAppointments(q);
    }
    request.setAttribute("pageTitle", "Search Appointment Details");
%>
<jsp:include page="header.jsp" />
<jsp:include page="sidebar.jsp" />

<div class="app-main">
    <jsp:include page="topbar.jsp" />

    <main class="content-body">
        <jsp:include page="alerts.jsp" />

        <div class="card" style="max-width: 800px; margin: 0 auto 24px;">
            <div class="card-header">
                <div class="card-title">
                    <span>🔍 Display & Search Appointment Details</span>
                </div>
            </div>

            <div class="card-body">
                <p style="font-size: 0.88rem; color: var(--text-muted); margin-bottom: 16px;">
                    Enter the unique <strong>Appointment Number</strong> (e.g. <code>APT-20260817-0001</code>), Patient Name, Phone Number, or NIC to locate patient visit records:
                </p>

                <form action="<%= request.getContextPath() %>/search_appointment.jsp" method="GET">
                    <div style="display: flex; gap: 10px;">
                        <input type="text" name="q" class="form-control" 
                               placeholder="e.g. APT-20260817-0001 or Kasun Chamara or 0771234567" 
                               value="<%= (q != null) ? q : "" %>" required autofocus style="font-size: 1rem; padding: 10px 14px;">
                        <button type="submit" class="btn btn-primary" style="padding: 10px 24px; font-weight: 700;">
                            Search ➔
                        </button>
                    </div>
                </form>
            </div>
        </div>

        <% if (q != null && !q.trim().isEmpty()) { %>
            <div class="card">
                <div class="card-header">
                    <div class="card-title">
                        <span>Search Results for "<%= q %>" (<%= results != null ? results.size() : 0 %> matches)</span>
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
                                    <th>Patient Name</th>
                                    <th>Contact</th>
                                    <th>Dentist</th>
                                    <th>Treatment</th>
                                    <th>Status</th>
                                    <th>Billing</th>
                                    <th style="text-align: right;">Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <%
                                    if (results != null && !results.isEmpty()) {
                                        for (Appointment a : results) {
                                %>
                                    <tr>
                                        <td><strong><%= a.getAppointmentNumber() %></strong></td>
                                        <td><%= DateUtil.formatDisplayDate(a.getAppointmentDate()) %></td>
                                        <td><strong><%= DateUtil.formatDisplayTime(a.getAppointmentTime()) %></strong></td>
                                        <td><%= a.getPatientName() %></td>
                                        <td><%= a.getPatientPhone() %></td>
                                        <td><%= a.getDoctorName() %> (<%= a.getDoctorRoom() %>)</td>
                                        <td><%= a.getTreatmentName() %></td>
                                        <td><span class="badge badge-confirmed"><%= a.getStatus() %></span></td>
                                        <td>
                                            <% if ("Paid".equalsIgnoreCase(a.getBillingStatus())) { %>
                                                <span class="badge badge-paid">Paid</span>
                                            <% } else { %>
                                                <span class="badge badge-unpaid">Unpaid</span>
                                            <% } %>
                                        </td>
                                        <td style="text-align: right;">
                                            <a href="<%= request.getContextPath() %>/appointment_view.jsp?id=<%= a.getAppointmentId() %>" class="btn btn-outline btn-sm">
                                                Display Details
                                            </a>
                                        </td>
                                    </tr>
                                <%
                                        }
                                    } else {
                                %>
                                    <tr>
                                        <td colspan="10" style="text-align: center; padding: 32px; color: var(--text-muted);">
                                            No appointment found matching query "<%= q %>".
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
