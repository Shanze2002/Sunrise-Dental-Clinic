<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.sunrisedental.model.*" %>
<%@ page import="com.sunrisedental.service.factory.ServiceFactory" %>
<%@ page import="com.sunrisedental.config.AppConfig" %>

<%
    String apptIdStr = request.getParameter("appointmentId");
    Appointment app = null;
    if (apptIdStr != null && !apptIdStr.isEmpty()) {
        try {
            app = ServiceFactory.getAppointmentService().getAppointmentById(Integer.parseInt(apptIdStr));
        } catch (Exception ignored) {}
    }
    request.setAttribute("pageTitle", "Calculate & Generate Patient Bill");
%>
<jsp:include page="header.jsp" />
<jsp:include page="sidebar.jsp" />

<div class="app-main">
    <jsp:include page="topbar.jsp" />

    <main class="content-body">
        <jsp:include page="alerts.jsp" />

        <div style="margin-bottom: 20px;">
            <a href="<%= request.getContextPath() %>/cashier_billing.jsp" class="btn btn-outline btn-sm">
                ← Back to Billing Desk
            </a>
        </div>

        <% if (app != null) { %>
            <div class="card" style="max-width: 850px; margin: 0 auto;">
                <div class="card-header">
                    <div>
                        <div class="card-title">
                            <span>🧾 Generate Official Dental Bill</span>
                        </div>
                        <div style="font-size: 0.85rem; color: var(--text-muted); margin-top: 2px;">
                            Appt: <strong><%= app.getAppointmentNumber() %></strong> • Patient: <strong><%= app.getPatientName() %></strong>
                        </div>
                    </div>
                </div>

                <div class="card-body">
                    <form action="<%= request.getContextPath() %>/billing/generate" method="POST" id="billingForm">
                        <input type="hidden" name="appointmentId" value="<%= app.getAppointmentId() %>">

                        <!-- Itemized Financial Grid -->
                        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 20px;">
                            
                            <div class="form-group">
                                <label class="form-label" for="consultationFee">Doctor Consultation Fee (LKR) <span class="required">*</span></label>
                                <input type="number" step="0.01" id="consultationFee" name="consultationFee" class="form-control" 
                                       value="<%= app.getConsultationFee() %>" required style="font-weight: 700;">
                                <div style="font-size: 0.8rem; color: var(--text-muted); margin-top: 4px;">
                                    Consultant: <%= app.getDoctorName() %>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="form-label" for="treatmentCost">Treatment Procedure Cost (LKR) <span class="required">*</span></label>
                                <input type="number" step="0.01" id="treatmentCost" name="treatmentCost" class="form-control" 
                                       value="<%= app.getTreatmentCost() %>" required style="font-weight: 700;">
                                <div style="font-size: 0.8rem; color: var(--text-muted); margin-top: 4px;">
                                    Procedure: <%= app.getTreatmentName() %>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="form-label" for="additionalCharges">Additional Consumables / Meds (LKR)</label>
                                <input type="number" step="0.01" id="additionalCharges" name="additionalCharges" class="form-control" 
                                       value="0.00">
                            </div>

                            <!-- Discount Strategy Pattern Selection -->
                            <div class="form-group">
                                <label class="form-label" for="discountType">Discount Policy (Strategy Pattern) <span class="required">*</span></label>
                                <select id="discountType" name="discountType" class="form-select" required style="font-weight: 600;">
                                    <option value="Standard" data-rate="0">Standard Rate (0% Discount)</option>
                                    <option value="Senior Citizen" data-rate="0.10">Senior Citizen Subsidy (10% Discount)</option>
                                    <option value="Corporate Insurance" data-rate="0.15">Insurance Partner (15% Coverage)</option>
                                    <option value="Loyalty Member" data-rate="0.05">Loyalty Member (5% Discount)</option>
                                </select>
                            </div>

                            <div class="form-group">
                                <label class="form-label" for="taxPercentage">VAT / Tax Rate (%)</label>
                                <input type="number" step="0.1" id="taxPercentage" name="taxPercentage" class="form-control" 
                                       value="<%= AppConfig.DEFAULT_TAX_PERCENTAGE %>">
                            </div>

                            <div class="form-group">
                                <label class="form-label" for="remarks">Billing Remarks</label>
                                <input type="text" id="remarks" name="remarks" class="form-control" 
                                       placeholder="e.g. Standard consultation and procedure settlement">
                            </div>

                        </div>

                        <!-- Live Calculation Summary Card -->
                        <div style="background-color: var(--bg-subtle); border-radius: var(--radius-md); padding: 20px; margin-top: 24px; border: 1px solid var(--border-color);">
                            <h4 style="font-size: 0.95rem; font-weight: 700; color: var(--primary-900); margin-bottom: 12px;">
                                💳 Real-Time Billing Breakdown
                            </h4>

                            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 12px; font-size: 0.92rem;">
                                <div>Subtotal:</div>
                                <div style="text-align: right; font-weight: 600;">LKR <span id="billSubtotal">0.00</span></div>

                                <div style="color: #047857;">Discount Applied:</div>
                                <div style="text-align: right; font-weight: 600; color: #047857;">- LKR <span id="billDiscount">0.00</span></div>

                                <div>Tax Amount:</div>
                                <div style="text-align: right; font-weight: 600;">+ LKR <span id="billTax">0.00</span></div>

                                <div style="font-size: 1.15rem; font-weight: 800; color: var(--primary-900); border-top: 2px solid var(--border-color); padding-top: 8px;">
                                    Net Total Payable:
                                </div>
                                <div style="text-align: right; font-size: 1.25rem; font-weight: 800; color: var(--teal-700); border-top: 2px solid var(--border-color); padding-top: 8px;">
                                    LKR <span id="billTotal">0.00</span>
                                </div>
                            </div>
                        </div>

                        <div style="display: flex; justify-content: flex-end; gap: 12px; margin-top: 24px;">
                            <a href="<%= request.getContextPath() %>/cashier_billing.jsp" class="btn btn-outline">Cancel</a>
                            <button type="submit" class="btn btn-primary" style="font-size: 1rem; padding: 10px 24px;">
                                Confirm & Issue Invoice ➔
                            </button>
                        </div>

                    </form>
                </div>
            </div>
        <% } %>
    </main>

<script src="<%= request.getContextPath() %>/assets/js/billing.js"></script>
<jsp:include page="footer.jsp" />
