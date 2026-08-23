<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.sunrisedental.model.*" %>
<%@ page import="com.sunrisedental.service.factory.ServiceFactory" %>
<%@ page import="com.sunrisedental.util.DateUtil" %>

<%
    String idStr = request.getParameter("appointmentId");
    if (idStr == null) idStr = request.getParameter("id");
    Appointment app = null;
    if (idStr != null && !idStr.isEmpty()) {
        try {
            app = ServiceFactory.getAppointmentService().getAppointmentById(Integer.parseInt(idStr));
        } catch (Exception ignored) {}
    }
    request.setAttribute("pageTitle", "Clinical Dental Treatment Desk");
%>
<jsp:include page="header.jsp" />
<jsp:include page="sidebar.jsp" />

<div class="app-main">
    <jsp:include page="topbar.jsp" />

    <main class="content-body">
        <jsp:include page="alerts.jsp" />

        <div style="margin-bottom: 20px;">
            <a href="<%= request.getContextPath() %>/doctor/schedule" class="btn btn-outline btn-sm">
                ← Back to Doctor Schedule
            </a>
        </div>

        <% if (app != null) { %>
            <div class="card" style="max-width: 900px; margin: 0 auto;">
                <div class="card-header">
                    <div>
                        <div class="card-title">
                            <span>🩺 Patient Clinical Encounter: <%= app.getPatientName() %></span>
                        </div>
                        <div style="font-size: 0.85rem; color: var(--text-muted); margin-top: 2px;">
                            Appt: <strong><%= app.getAppointmentNumber() %></strong> • <%= DateUtil.formatDisplayDate(app.getAppointmentDate()) %> at <%= DateUtil.formatDisplayTime(app.getAppointmentTime()) %>
                        </div>
                    </div>
                    <span class="badge badge-confirmed"><%= app.getStatus() %></span>
                </div>

                <div class="card-body">
                    
                    <!-- Patient Summary Strip -->
                    <div style="background-color: var(--bg-subtle); padding: 16px; border-radius: var(--radius-md); margin-bottom: 24px; display: grid; grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); gap: 12px; font-size: 0.88rem;">
                        <div><strong>Code:</strong> <%= app.getPatientCode() %></div>
                        <div><strong>Phone:</strong> <%= app.getPatientPhone() %></div>
                        <div><strong>Procedure:</strong> <%= app.getTreatmentName() %></div>
                        <div><strong>Surgery Room:</strong> <%= app.getDoctorRoom() %></div>
                    </div>

                    <form action="<%= request.getContextPath() %>/doctor/update-status" method="POST">
                        <input type="hidden" name="appointmentId" value="<%= app.getAppointmentId() %>">

                        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 20px;">
                            
                            <!-- Tooth Chart Numbers -->
                            <div class="form-group" style="grid-column: span 2;">
                                <label class="form-label" for="toothNumbers">Tooth Chart Numbers (FDI System / Quadrant) <span class="required">*</span></label>
                                <input type="text" id="toothNumbers" name="toothNumbers" class="form-control" 
                                       placeholder="e.g. UR1 (11), UL2 (22), LL6 (36) or All Upper Arch" 
                                       value="<%= app.getToothNumbers() != null ? app.getToothNumbers() : "" %>" required>
                                <div style="font-size: 0.8rem; color: var(--text-muted); margin-top: 4px;">
                                    Tooth Quadrants: UR (Upper Right: 11-18), UL (Upper Left: 21-28), LL (Lower Left: 31-38), LR (Lower Right: 41-48)
                                </div>
                            </div>

                            <!-- Clinical Diagnosis Notes -->
                            <div class="form-group" style="grid-column: span 2;">
                                <label class="form-label" for="clinicalNotes">Clinical Diagnosis & Procedure Findings <span class="required">*</span></label>
                                <textarea id="clinicalNotes" name="clinicalNotes" class="form-control" rows="4" 
                                          placeholder="Enter clinical examination observations, cavities, probing depth, anesthesia administered, procedure completed..." required><%= app.getClinicalNotes() != null ? app.getClinicalNotes() : "" %></textarea>
                            </div>

                            <!-- Prescriptions -->
                            <div class="form-group" style="grid-column: span 2;">
                                <label class="form-label" for="prescriptions">Medication Prescriptions & Home Care Instructions</label>
                                <textarea id="prescriptions" name="prescriptions" class="form-control" rows="3" 
                                          placeholder="e.g. Amoxicillin 500mg TDS x 5 days, Paracetamol 500mg PRN for pain, Warm salt water mouthwash"><%= app.getPrescriptions() != null ? app.getPrescriptions() : "" %></textarea>
                            </div>

                            <!-- Status Selection -->
                            <div class="form-group" style="grid-column: span 2;">
                                <label class="form-label" for="status">Update Appointment Status <span class="required">*</span></label>
                                <select id="status" name="status" class="form-select" required style="font-weight: 700;">
                                    <option value="In-Treatment" <%= "In-Treatment".equalsIgnoreCase(app.getStatus()) ? "selected" : "" %>>⏳ In-Treatment (Under Procedure)</option>
                                    <option value="Completed" <%= "Completed".equalsIgnoreCase(app.getStatus()) ? "selected" : "" %>>✅ Completed (Push to Cashier for Billing)</option>
                                    <option value="Confirmed" <%= "Confirmed".equalsIgnoreCase(app.getStatus()) ? "selected" : "" %>>📅 Confirmed (Waiting in Waiting Room)</option>
                                    <option value="Cancelled" <%= "Cancelled".equalsIgnoreCase(app.getStatus()) ? "selected" : "" %>>❌ Cancelled / No-Show</option>
                                </select>
                            </div>

                        </div>

                        <div style="display: flex; justify-content: flex-end; gap: 12px; margin-top: 24px;">
                            <a href="<%= request.getContextPath() %>/doctor/schedule" class="btn btn-outline">Cancel</a>
                            <button type="submit" class="btn btn-primary">
                                Save Clinical Record & Update Status ➔
                            </button>
                        </div>

                    </form>

                </div>
            </div>
        <% } %>
    </main>

<jsp:include page="footer.jsp" />
