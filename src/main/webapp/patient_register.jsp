<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.sunrisedental.model.Patient" %>
<%@ page import="com.sunrisedental.service.factory.ServiceFactory" %>

<%
    String idStr = request.getParameter("id");
    Patient patient = null;
    if (idStr != null && !idStr.isEmpty()) {
        try {
            patient = ServiceFactory.getPatientService().getPatientById(Integer.parseInt(idStr));
        } catch (Exception ignored) {}
    }
    if (patient == null) patient = new Patient();
    boolean isEdit = (patient.getPatientId() > 0);
    request.setAttribute("pageTitle", isEdit ? "Edit Patient Details" : "Register New Patient");
%>
<jsp:include page="header.jsp" />
<jsp:include page="sidebar.jsp" />

<div class="app-main">
    <jsp:include page="topbar.jsp" />

    <main class="content-body">
        <jsp:include page="alerts.jsp" />

        <div style="margin-bottom: 20px;">
            <a href="<%= request.getContextPath() %>/patient_list.jsp" class="btn btn-outline btn-sm">
                ← Back to Patients Directory
            </a>
        </div>

        <div class="card" style="max-width: 800px; margin: 0 auto;">
            <div class="card-header">
                <div class="card-title">
                    <span><%= isEdit ? "✏️ Edit Patient Record" : "👤 New Patient Registration" %></span>
                </div>
            </div>

            <div class="card-body">
                <form action="<%= request.getContextPath() %>/patients/<%= isEdit ? "edit" : "new" %>" method="POST">
                    <% if (isEdit) { %>
                        <input type="hidden" name="patientId" value="<%= patient.getPatientId() %>">
                    <% } %>

                    <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 16px;">
                        <div class="form-group" style="grid-column: span 2;">
                            <label class="form-label" for="fullName">Full Name <span class="required">*</span></label>
                            <input type="text" id="fullName" name="fullName" class="form-control" 
                                   placeholder="e.g. Kasun Chamara Bandara" 
                                   value="<%= patient.getFullName() != null ? patient.getFullName() : "" %>" required>
                        </div>

                        <div class="form-group">
                            <label class="form-label" for="phone">Contact Number <span class="required">*</span></label>
                            <input type="tel" id="phone" name="phone" class="form-control" 
                                   placeholder="e.g. 0771234567" 
                                   value="<%= patient.getPhone() != null ? patient.getPhone() : "" %>" required>
                        </div>

                        <div class="form-group">
                            <label class="form-label" for="email">Email Address</label>
                            <input type="email" id="email" name="email" class="form-control" 
                                   placeholder="patient@example.com" 
                                   value="<%= patient.getEmail() != null ? patient.getEmail() : "" %>">
                        </div>

                        <div class="form-group">
                            <label class="form-label" for="nicPassport">NIC / Passport Number</label>
                            <input type="text" id="nicPassport" name="nicPassport" class="form-control" 
                                   placeholder="e.g. 199512345678 or V" 
                                   value="<%= patient.getNicPassport() != null ? patient.getNicPassport() : "" %>">
                        </div>

                        <div class="form-group">
                            <label class="form-label" for="gender">Gender <span class="required">*</span></label>
                            <select id="gender" name="gender" class="form-select" required>
                                <option value="Male" <%= "Male".equalsIgnoreCase(patient.getGender()) ? "selected" : "" %>>Male</option>
                                <option value="Female" <%= "Female".equalsIgnoreCase(patient.getGender()) ? "selected" : "" %>>Female</option>
                                <option value="Other" <%= "Other".equalsIgnoreCase(patient.getGender()) ? "selected" : "" %>>Other</option>
                            </select>
                        </div>

                        <div class="form-group">
                            <label class="form-label" for="dob">Date of Birth</label>
                            <input type="date" id="dob" name="dob" class="form-control" 
                                   value="<%= patient.getDob() != null ? patient.getDob().toString() : "" %>">
                        </div>

                        <div class="form-group">
                            <label class="form-label" for="emergencyContact">Emergency Contact (Name & Phone)</label>
                            <input type="text" id="emergencyContact" name="emergencyContact" class="form-control" 
                                   placeholder="e.g. Sunimal (Brother) - 0719876543" 
                                   value="<%= patient.getEmergencyContact() != null ? patient.getEmergencyContact() : "" %>">
                        </div>

                        <div class="form-group" style="grid-column: span 2;">
                            <label class="form-label" for="address">Residential Address <span class="required">*</span></label>
                            <textarea id="address" name="address" class="form-control" rows="2" 
                                      placeholder="e.g. No. 45, Galle Road, Colombo 03" required><%= patient.getAddress() != null ? patient.getAddress() : "" %></textarea>
                        </div>

                        <div class="form-group">
                            <label class="form-label" for="allergies">Known Medical Allergies</label>
                            <input type="text" id="allergies" name="allergies" class="form-control" 
                                   placeholder="e.g. Penicillin, Latex, Local Anesthetics (or None)" 
                                   value="<%= patient.getAllergies() != null ? patient.getAllergies() : "None" %>">
                        </div>

                        <div class="form-group">
                            <label class="form-label" for="medicalHistory">Medical Conditions / History</label>
                            <input type="text" id="medicalHistory" name="medicalHistory" class="form-control" 
                                   placeholder="e.g. Diabetes, Hypertension, Bleeding disorders" 
                                   value="<%= patient.getMedicalHistory() != null ? patient.getMedicalHistory() : "None" %>">
                        </div>
                    </div>

                    <div style="display: flex; justify-content: flex-end; gap: 12px; margin-top: 24px;">
                        <a href="<%= request.getContextPath() %>/patient_list.jsp" class="btn btn-outline">Cancel</a>
                        <button type="submit" class="btn btn-primary">
                            <%= isEdit ? "Save Patient Updates" : "Register Patient & Continue ➔" %>
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </main>

<jsp:include page="footer.jsp" />
