<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.sunrisedental.model.*" %>
<%@ page import="com.sunrisedental.service.factory.ServiceFactory" %>
<%@ page import="com.sunrisedental.util.DateUtil" %>

<%
    List<Bill> unpaid = ServiceFactory.getBillingService().getUnpaidBills();
    List<Bill> recent = ServiceFactory.getBillingService().getRecentBills();
    List<Appointment> todayApps = ServiceFactory.getAppointmentService().getTodayAppointments();
    double revenue = ServiceFactory.getBillingService().getTodayRevenue();
    request.setAttribute("pageTitle", "Cashier Billing Desk");
%>
<jsp:include page="header.jsp" />
<jsp:include page="sidebar.jsp" />

<div class="app-main">
    <jsp:include page="topbar.jsp" />

    <main class="content-body">
        <jsp:include page="alerts.jsp" />

        <!-- Metrics Grid -->
        <div class="stats-grid">
            <div class="stat-card">
                <div class="stat-icon emerald">💵</div>
                <div class="stat-details">
                    <div class="stat-value">LKR <%= String.format("%,.2f", revenue) %></div>
                    <div class="stat-label">Today's Collected Revenue</div>
                </div>
            </div>

            <div class="stat-card">
                <div class="stat-icon amber">⏳</div>
                <div class="stat-details">
                    <div class="stat-value"><%= unpaid != null ? unpaid.size() : 0 %></div>
                    <div class="stat-label">Unpaid Invoices</div>
                </div>
            </div>

            <div class="stat-card">
                <div class="stat-icon blue">🧾</div>
                <div class="stat-details">
                    <div class="stat-value"><%= recent != null ? recent.size() : 0 %></div>
                    <div class="stat-label">Total Invoices Issued</div>
                </div>
            </div>
        </div>

        <!-- Today's Patient Billing Queue -->
        <div class="card">
            <div class="card-header">
                <div class="card-title">
                    <span>💳 Patient Billing & Invoicing Queue</span>
                </div>
            </div>

            <div class="card-body" style="padding: 0;">
                <div class="table-responsive">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Appointment #</th>
                                <th>Patient Name</th>
                                <th>Dentist</th>
                                <th>Treatment Type</th>
                                <th>Clinical Status</th>
                                <th>Estimated Total</th>
                                <th>Billing Status</th>
                                <th style="text-align: right;">Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                if (todayApps != null && !todayApps.isEmpty()) {
                                    for (Appointment a : todayApps) {
                            %>
                                <tr>
                                    <td><strong><%= a.getAppointmentNumber() %></strong></td>
                                    <td><%= a.getPatientName() %> (<%= a.getPatientPhone() %>)</td>
                                    <td><%= a.getDoctorName() %></td>
                                    <td><%= a.getTreatmentName() %></td>
                                    <td><span class="badge badge-confirmed"><%= a.getStatus() %></span></td>
                                    <td><strong>LKR <%= String.format("%,.2f", a.getEstimatedTotal()) %></strong></td>
                                    <td>
                                        <% if ("Paid".equalsIgnoreCase(a.getBillingStatus())) { %>
                                            <span class="badge badge-paid">Paid</span>
                                        <% } else if (a.getBillId() != null) { %>
                                            <span class="badge badge-unpaid">Unpaid</span>
                                        <% } else { %>
                                            <span class="badge" style="background:#f1f5f9; color:#64748b;">Unbilled</span>
                                        <% } %>
                                    </td>
                                    <td style="text-align: right;">
                                        <% if (a.getBillId() != null && a.getBillId() > 0) { %>
                                            <a href="<%= request.getContextPath() %>/invoice_print.jsp?billId=<%= a.getBillId() %>" class="btn btn-primary btn-sm">
                                                Invoice / Receipt
                                            </a>
                                        <% } else { %>
                                            <a href="<%= request.getContextPath() %>/generate_bill.jsp?appointmentId=<%= a.getAppointmentId() %>" class="btn btn-primary btn-sm">
                                                Generate Bill ➔
                                            </a>
                                        <% } %>
                                    </td>
                                </tr>
                            <%
                                    }
                                } else {
                            %>
                                <tr>
                                    <td colspan="8" style="text-align: center; padding: 32px; color: var(--text-muted);">
                                        No patients waiting for billing today.
                                    </td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <!-- Outstanding Bills -->
        <div class="card">
            <div class="card-header">
                <div class="card-title">
                    <span>⚠️ Unpaid / Partial Invoices List</span>
                </div>
            </div>

            <div class="card-body" style="padding: 0;">
                <div class="table-responsive">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Invoice #</th>
                                <th>Appointment #</th>
                                <th>Patient Name</th>
                                <th>Total Fee</th>
                                <th>Paid</th>
                                <th>Balance Due</th>
                                <th>Status</th>
                                <th style="text-align: right;">Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                if (unpaid != null && !unpaid.isEmpty()) {
                                    for (Bill b : unpaid) {
                            %>
                                <tr>
                                    <td>
                                        <a href="<%= request.getContextPath() %>/invoice_print.jsp?billId=<%= b.getBillId() %>" style="font-weight: 700; color: var(--teal-700); text-decoration: none;">
                                            <%= b.getInvoiceNumber() %>
                                        </a>
                                    </td>
                                    <td><%= b.getAppointmentNumber() %></td>
                                    <td><strong><%= b.getPatientName() %></strong></td>
                                    <td>LKR <%= String.format("%,.2f", b.getTotalAmount()) %></td>
                                    <td style="color: var(--teal-700);">LKR <%= String.format("%,.2f", b.getPaidAmount()) %></td>
                                    <td style="color: #b91c1c; font-weight: 700;">LKR <%= String.format("%,.2f", b.getBalanceAmount()) %></td>
                                    <td><span class="badge badge-unpaid"><%= b.getPaymentStatus() %></span></td>
                                    <td style="text-align: right;">
                                        <a href="<%= request.getContextPath() %>/invoice_print.jsp?billId=<%= b.getBillId() %>" class="btn btn-primary btn-sm">
                                            Receive Payment ➔
                                        </a>
                                    </td>
                                </tr>
                            <%
                                    }
                                } else {
                            %>
                                <tr>
                                    <td colspan="8" style="text-align: center; padding: 24px; color: var(--text-muted);">
                                        No outstanding unpaid bills.
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
