<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.sunrisedental.model.*" %>
<%@ page import="com.sunrisedental.service.factory.ServiceFactory" %>
<%@ page import="com.sunrisedental.config.AppConfig" %>
<%@ page import="com.sunrisedental.util.DateUtil" %>

<%
    String billIdStr = request.getParameter("billId");
    if (billIdStr == null) billIdStr = request.getParameter("id");
    Bill bill = null;
    if (billIdStr != null && !billIdStr.isEmpty()) {
        try {
            bill = ServiceFactory.getBillingService().getBillById(Integer.parseInt(billIdStr));
        } catch (Exception ignored) {}
    }
    request.setAttribute("pageTitle", "Official Invoice - " + (bill != null ? bill.getInvoiceNumber() : ""));
%>
<jsp:include page="header.jsp" />
<jsp:include page="sidebar.jsp" />

<div class="app-main">
    <jsp:include page="topbar.jsp" />

    <main class="content-body">
        <jsp:include page="alerts.jsp" />

        <!-- Screen Action Bar -->
        <div style="max-width: 800px; margin: 0 auto 20px; display: flex; justify-content: space-between; align-items: center;" class="no-print">
            <a href="<%= request.getContextPath() %>/cashier_billing.jsp" class="btn btn-outline btn-sm">
                ← Back to Billing Queue
            </a>

            <div style="display: flex; gap: 8px;">
                <button type="button" class="btn btn-navy" onclick="window.print()">
                    🖨️ Print / Download PDF
                </button>

                <% if (bill != null && bill.getBalanceAmount() > 0) { %>
                    <button class="btn btn-primary" onclick="openModal('paymentModal')">
                        💵 Receive Payment
                    </button>
                <% } %>
            </div>
        </div>

        <% if (bill != null) { %>
            <!-- Print-Ready Invoice Container -->
            <div class="invoice-container">
                <!-- Header -->
                <div class="invoice-header">
                    <div class="clinic-info">
                        <h2><%= AppConfig.CLINIC_NAME %></h2>
                        <p style="font-weight: 600; color: var(--teal-700);"><%= AppConfig.CLINIC_TAGLINE %></p>
                        <p><%= AppConfig.CLINIC_ADDRESS %></p>
                        <p>Tel: <%= AppConfig.CLINIC_PHONE %> • Email: <%= AppConfig.CLINIC_EMAIL %></p>
                        <p style="font-size: 0.75rem; color: var(--text-light); margin-top: 4px;">Reg No: <%= AppConfig.CLINIC_REG_NO %></p>
                    </div>

                    <div class="invoice-meta">
                        <div class="invoice-title">INVOICE</div>
                        <div class="meta-row"><strong>Invoice No:</strong> <%= bill.getInvoiceNumber() %></div>
                        <div class="meta-row"><strong>Appt No:</strong> <%= bill.getAppointmentNumber() %></div>
                        <div class="meta-row"><strong>Date:</strong> <%= DateUtil.formatDisplayDateTime(bill.getCreatedAt()) %></div>
                        <div class="meta-row">
                            <strong>Status:</strong> 
                            <span class="badge <%= "Paid".equalsIgnoreCase(bill.getPaymentStatus()) ? "badge-paid" : "badge-unpaid" %>">
                                <%= bill.getPaymentStatus() %>
                            </span>
                        </div>
                    </div>
                </div>

                <!-- Patient & Doctor Info Grid -->
                <div class="patient-doc-grid">
                    <div class="info-block">
                        <h4>Patient Information</h4>
                        <p><strong><%= bill.getPatientName() %></strong> (<%= bill.getPatientCode() %>)</p>
                        <p>Phone: <%= bill.getPatientPhone() %></p>
                        <p>Address: <%= bill.getPatientAddress() %></p>
                    </div>

                    <div class="info-block">
                        <h4>Consulting Dentist</h4>
                        <p><strong><%= bill.getDoctorName() %></strong></p>
                        <p>Treatment: <strong><%= bill.getTreatmentName() %></strong></p>
                    </div>
                </div>

                <!-- Itemized Cost Table -->
                <div class="table-responsive">
                    <table class="data-table" style="border: 1px solid var(--border-color);">
                        <thead>
                            <tr>
                                <th>#</th>
                                <th>Service / Item Description</th>
                                <th style="text-align: right;">Standard Rate (LKR)</th>
                                <th style="text-align: right;">Amount (LKR)</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td>1</td>
                                <td>
                                    <strong>Doctor Consultation Fee</strong>
                                    <div style="font-size: 0.8rem; color: var(--text-muted);"><%= bill.getDoctorName() %></div>
                                </td>
                                <td style="text-align: right;"><%= String.format("%,.2f", bill.getConsultationFee()) %></td>
                                <td style="text-align: right; font-weight: 600;"><%= String.format("%,.2f", bill.getConsultationFee()) %></td>
                            </tr>
                            <tr>
                                <td>2</td>
                                <td>
                                    <strong>Dental Treatment / Procedure</strong>
                                    <div style="font-size: 0.8rem; color: var(--text-muted);"><%= bill.getTreatmentName() %></div>
                                </td>
                                <td style="text-align: right;"><%= String.format("%,.2f", bill.getTreatmentCost()) %></td>
                                <td style="text-align: right; font-weight: 600;"><%= String.format("%,.2f", bill.getTreatmentCost()) %></td>
                            </tr>
                            <% if (bill.getAdditionalCharges() > 0) { %>
                                <tr>
                                    <td>3</td>
                                    <td>
                                        <strong>Additional Consumables / Medicines</strong>
                                    </td>
                                    <td style="text-align: right;"><%= String.format("%,.2f", bill.getAdditionalCharges()) %></td>
                                    <td style="text-align: right; font-weight: 600;"><%= String.format("%,.2f", bill.getAdditionalCharges()) %></td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>

                <!-- Financial Summary -->
                <div class="invoice-summary">
                    <table class="summary-table">
                        <tr>
                            <td>Subtotal:</td>
                            <td style="text-align: right; font-weight: 600;">LKR <%= String.format("%,.2f", bill.getSubtotal()) %></td>
                        </tr>
                        <% if (bill.getDiscountAmount() > 0) { %>
                            <tr style="color: #047857;">
                                <td>Discount (<%= bill.getDiscountType() %>):</td>
                                <td style="text-align: right; font-weight: 600;">- LKR <%= String.format("%,.2f", bill.getDiscountAmount()) %></td>
                            </tr>
                        <% } %>
                        <% if (bill.getTaxAmount() > 0) { %>
                            <tr>
                                <td>Tax (<%= bill.getTaxPercentage() %>%):</td>
                                <td style="text-align: right; font-weight: 600;">+ LKR <%= String.format("%,.2f", bill.getTaxAmount()) %></td>
                            </tr>
                        <% } %>
                        <tr class="total-row">
                            <td>Total Amount Due:</td>
                            <td style="text-align: right;">LKR <%= String.format("%,.2f", bill.getTotalAmount()) %></td>
                        </tr>
                        <tr>
                            <td>Amount Paid:</td>
                            <td style="text-align: right; color: var(--teal-700); font-weight: 700;">LKR <%= String.format("%,.2f", bill.getPaidAmount()) %></td>
                        </tr>
                        <tr>
                            <td>Balance Outstanding:</td>
                            <td style="text-align: right; font-weight: 800; color: <%= bill.getBalanceAmount() > 0 ? "#b91c1c" : "#047857" %>;">
                                LKR <%= String.format("%,.2f", bill.getBalanceAmount()) %>
                            </td>
                        </tr>
                    </table>
                </div>

                <!-- Payment Receipts History -->
                <% if (bill.getPaymentHistory() != null && !bill.getPaymentHistory().isEmpty()) { %>
                    <div style="margin-top: 30px;">
                        <h4 style="font-size: 0.85rem; text-transform: uppercase; color: var(--text-muted); margin-bottom: 8px;">
                            Payment Receipts Issued
                        </h4>
                        <table class="data-table" style="font-size: 0.82rem;">
                            <thead>
                                <tr>
                                    <th>Receipt #</th>
                                    <th>Date/Time</th>
                                    <th>Method</th>
                                    <th>Cashier</th>
                                    <th style="text-align: right;">Amount Paid</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% for (Payment p : bill.getPaymentHistory()) { %>
                                    <tr>
                                        <td><strong><%= p.getReceiptNumber() %></strong></td>
                                        <td><%= DateUtil.formatDisplayDateTime(p.getPaymentDate()) %></td>
                                        <td><%= p.getPaymentMethod() %></td>
                                        <td><%= p.getCashierName() != null ? p.getCashierName() : "Cashier Desk" %></td>
                                        <td style="text-align: right; font-weight: 700; color: var(--teal-700);">
                                            LKR <%= String.format("%,.2f", p.getAmount()) %>
                                        </td>
                                    </tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                <% } %>

                <!-- Footer / Signatures -->
                <div class="invoice-footer">
                    <div>
                        <p style="font-size: 0.8rem; color: var(--text-muted);">Thank you for choosing Sunrise Dental Clinic Colombo!</p>
                        <p style="font-size: 0.75rem; color: var(--text-light); margin-top: 2px;">This is a computer-generated computerized invoice.</p>
                    </div>

                    <div class="signature-line">
                        Authorized Cashier / Stamp
                    </div>
                </div>
            </div>

            <!-- Receive Payment Modal Dialog -->
            <div id="paymentModal" class="modal-overlay no-print">
                <div class="modal-box">
                    <div class="modal-header">
                        <div class="card-title">
                            <span>💵 Record Payment for <%= bill.getInvoiceNumber() %></span>
                        </div>
                        <button type="button" class="btn btn-outline btn-sm" onclick="closeModal('paymentModal')">✕</button>
                    </div>

                    <form action="<%= request.getContextPath() %>/billing/pay" method="POST">
                        <input type="hidden" name="billId" value="<%= bill.getBillId() %>">

                        <div class="modal-body">
                            <div class="form-group">
                                <label class="form-label" for="payAmount">Payment Amount (LKR) <span class="required">*</span></label>
                                <input type="number" step="0.01" id="payAmount" name="amount" class="form-control" 
                                       value="<%= bill.getBalanceAmount() %>" max="<%= bill.getBalanceAmount() %>" required style="font-weight: 700; font-size: 1.1rem;">
                                <div style="font-size: 0.8rem; color: var(--text-muted); margin-top: 4px;">
                                    Outstanding Balance: LKR <%= String.format("%,.2f", bill.getBalanceAmount()) %>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="form-label" for="paymentMethod">Payment Method <span class="required">*</span></label>
                                <select id="paymentMethod" name="paymentMethod" class="form-select" required>
                                    <option value="Cash">Cash</option>
                                    <option value="Credit Card">Credit Card</option>
                                    <option value="Debit Card">Debit Card</option>
                                    <option value="Bank Transfer">Bank Transfer / Online</option>
                                    <option value="Insurance">Insurance Settlement</option>
                                </select>
                            </div>

                            <div class="form-group">
                                <label class="form-label" for="transactionReference">Transaction Ref / Slip No.</label>
                                <input type="text" id="transactionReference" name="transactionReference" class="form-control" 
                                       placeholder="e.g. POS-AUTH-98124 or SLIP-4412">
                            </div>

                            <div class="form-group">
                                <label class="form-label" for="remarks">Payment Remarks</label>
                                <input type="text" id="remarks" name="remarks" class="form-control" 
                                       placeholder="e.g. Paid at front cashier desk" value="Settled at front cashier desk">
                            </div>
                        </div>

                        <div class="modal-footer">
                            <button type="button" class="btn btn-outline" onclick="closeModal('paymentModal')">Cancel</button>
                            <button type="submit" class="btn btn-primary">
                                Complete Payment & Issue Receipt ➔
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        <% } %>
    </main>

<jsp:include page="footer.jsp" />
