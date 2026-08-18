<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="com.sunrisedental.model.MonthlyReportDTO" %>
<%@ page import="com.sunrisedental.service.factory.ServiceFactory" %>
<%@ page import="com.sunrisedental.config.AppConfig" %>
<%@ page import="com.sunrisedental.util.DateUtil" %>

<%
    String selectedMonth = request.getParameter("month");
    if (selectedMonth == null || selectedMonth.trim().isEmpty()) {
        selectedMonth = DateUtil.getCurrentYearMonth();
    }
    MonthlyReportDTO report = ServiceFactory.getReportService().getMonthlyReport(selectedMonth);
    request.setAttribute("pageTitle", "Executive Decision & Financial Report");
%>
<jsp:include page="header.jsp" />
<jsp:include page="sidebar.jsp" />

<div class="app-main">
    <jsp:include page="topbar.jsp" />

    <main class="content-body">
        <jsp:include page="alerts.jsp" />

        <!-- Month Filter & Print Bar -->
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; flex-wrap: wrap; gap: 12px;" class="no-print">
            <form action="<%= request.getContextPath() %>/admin_reports.jsp" method="GET" style="display: flex; gap: 8px; align-items: center;">
                <label class="form-label" style="margin-bottom: 0;">Report Month:</label>
                <input type="month" name="month" class="form-control" value="<%= selectedMonth %>" style="width: 170px;">
                <button type="submit" class="btn btn-navy">Generate Report</button>
            </form>

            <button type="button" class="btn btn-primary" onclick="window.print()">
                🖨️ Print / Download PDF
            </button>
        </div>

        <% if (report != null) { %>
            <div class="card">
                <div class="card-header">
                    <div>
                        <div class="card-title">
                            <span>📈 Monthly Executive Decision & Financial Report</span>
                        </div>
                        <div style="font-size: 0.85rem; color: var(--text-muted); margin-top: 2px;">
                            Period: <strong><%= report.getPeriod() %></strong> • Generated for <%= AppConfig.CLINIC_NAME %>
                        </div>
                    </div>
                    <span class="badge badge-confirmed">Official Report</span>
                </div>

                <div class="card-body">
                    <!-- Financial Metrics Grid -->
                    <h3 style="font-size: 1rem; font-weight: 700; color: var(--primary-900); margin-bottom: 16px; border-bottom: 2px solid var(--border-color); padding-bottom: 6px;">
                        💰 Financial Revenue & Collections Breakdown
                    </h3>

                    <div class="stats-grid" style="margin-bottom: 24px;">
                        <div class="stat-card">
                            <div class="stat-icon emerald">💵</div>
                            <div class="stat-details">
                                <div class="stat-value" style="color: #047857;">LKR <%= String.format("%,.2f", report.getTotalNetRevenue()) %></div>
                                <div class="stat-label">Total Invoiced Revenue</div>
                            </div>
                        </div>

                        <div class="stat-card">
                            <div class="stat-icon teal">💳</div>
                            <div class="stat-details">
                                <div class="stat-value" style="color: var(--teal-700);">LKR <%= String.format("%,.2f", report.getTotalCashCollected()) %></div>
                                <div class="stat-label">Actual Cash Collected</div>
                            </div>
                        </div>

                        <div class="stat-card">
                            <div class="stat-icon amber">⏳</div>
                            <div class="stat-details">
                                <div class="stat-value" style="color: #b91c1c;">LKR <%= String.format("%,.2f", report.getOutstandingBalance()) %></div>
                                <div class="stat-label">Outstanding Receivables</div>
                            </div>
                        </div>

                        <div class="stat-card">
                            <div class="stat-icon blue">🏷️</div>
                            <div class="stat-details">
                                <div class="stat-value">LKR <%= String.format("%,.2f", report.getTotalDiscountsGranted()) %></div>
                                <div class="stat-label">Total Discounts Granted</div>
                            </div>
                        </div>
                    </div>

                    <!-- Revenue Stream Breakdown Table -->
                    <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 24px; margin-bottom: 28px;">
                        <div>
                            <h4 style="font-size: 0.9rem; font-weight: 700; color: var(--text-secondary); margin-bottom: 12px;">Income Source Itemization</h4>
                            <table class="data-table">
                                <tbody>
                                    <tr>
                                        <td>Doctor Consultation Fees</td>
                                        <td style="text-align: right; font-weight: 600;">LKR <%= String.format("%,.2f", report.getGrossConsultationIncome()) %></td>
                                    </tr>
                                    <tr>
                                        <td>Treatment & Clinical Procedures</td>
                                        <td style="text-align: right; font-weight: 600;">LKR <%= String.format("%,.2f", report.getGrossTreatmentIncome()) %></td>
                                    </tr>
                                    <tr>
                                        <td>Additional Items & Medicines</td>
                                        <td style="text-align: right; font-weight: 600;">LKR <%= String.format("%,.2f", report.getGrossAdditionalCharges()) %></td>
                                    </tr>
                                    <tr style="color: #047857;">
                                        <td>Discounts Subsidized (Strategy Policies)</td>
                                        <td style="text-align: right; font-weight: 600;">- LKR <%= String.format("%,.2f", report.getTotalDiscountsGranted()) %></td>
                                    </tr>
                                    <tr>
                                        <td>Taxes Collected</td>
                                        <td style="text-align: right; font-weight: 600;">+ LKR <%= String.format("%,.2f", report.getTotalTaxCollected()) %></td>
                                    </tr>
                                    <tr style="font-weight: 800; font-size: 1rem; background-color: var(--bg-subtle);">
                                        <td>Net Invoiced Turnover</td>
                                        <td style="text-align: right; color: var(--primary-900);">LKR <%= String.format("%,.2f", report.getTotalNetRevenue()) %></td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>

                        <div>
                            <h4 style="font-size: 0.9rem; font-weight: 700; color: var(--text-secondary); margin-bottom: 12px;">Operational Volume Metrics</h4>
                            <table class="data-table">
                                <tbody>
                                    <tr>
                                        <td>Total Appointments Scheduled</td>
                                        <td style="text-align: right; font-weight: 700;"><%= report.getTotalAppointments() %></td>
                                    </tr>
                                    <tr>
                                        <td>Successfully Completed Visits</td>
                                        <td style="text-align: right; font-weight: 700; color: #047857;"><%= report.getCompletedAppointments() %></td>
                                    </tr>
                                    <tr>
                                        <td>Cancelled / No-Shows</td>
                                        <td style="text-align: right; font-weight: 700; color: #b91c1c;"><%= report.getCancelledAppointments() %></td>
                                    </tr>
                                    <tr>
                                        <td>New Patient Registrations</td>
                                        <td style="text-align: right; font-weight: 700; color: var(--teal-700);"><%= report.getNewPatientsRegistered() %></td>
                                    </tr>
                                    <tr>
                                        <td>Total Invoices Issued</td>
                                        <td style="text-align: right; font-weight: 700;"><%= report.getTotalInvoicesIssued() %></td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>

                    <!-- Top Treatments Performed -->
                    <h3 style="font-size: 1rem; font-weight: 700; color: var(--primary-900); margin-bottom: 16px; border-bottom: 2px solid var(--border-color); padding-bottom: 6px;">
                        🩺 Top Dental Procedures Performed in Period
                    </h3>
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Procedure / Treatment</th>
                                <th style="text-align: right;">Total Cases Performed</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                Map<String, Integer> tMap = report.getTreatmentsDistribution();
                                if (tMap != null && !tMap.isEmpty()) {
                                    for (Map.Entry<String, Integer> entry : tMap.entrySet()) {
                            %>
                                <tr>
                                    <td><strong><%= entry.getKey() %></strong></td>
                                    <td style="text-align: right; font-weight: 700;"><%= entry.getValue() %> visits</td>
                                </tr>
                            <%
                                    }
                                } else {
                            %>
                                <tr>
                                    <td colspan="2" style="text-align: center; padding: 18px; color: var(--text-muted);">
                                        No treatments completed in this month.
                                    </td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>

                </div>
            </div>
        <% } %>
    </main>

<jsp:include page="footer.jsp" />
