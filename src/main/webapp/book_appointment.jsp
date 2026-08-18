<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.sunrisedental.model.*" %>
<%@ page import="com.sunrisedental.service.factory.ServiceFactory" %>
<%@ page import="com.sunrisedental.util.DateUtil" %>

<%
    List<Patient> patients = ServiceFactory.getPatientService().getAllPatients();
    List<Doctor> doctors = ServiceFactory.getDoctorService().getActiveDoctors();
    List<Treatment> treatments = ServiceFactory.getTreatmentService().getActiveTreatments();

    String preSelectPatientId = request.getParameter("patientId");
    request.setAttribute("pageTitle", "Book Dental Appointment");
%>
<jsp:include page="header.jsp" />
<jsp:include page="sidebar.jsp" />

<div class="app-main">
    <jsp:include page="topbar.jsp" />

    <main class="content-body">
        <jsp:include page="alerts.jsp" />

        <div style="margin-bottom: 20px;">
            <a href="<%= request.getContextPath() %>/receptionist_dashboard.jsp" class="btn btn-outline btn-sm">
                ← Back to Reception Desk
            </a>
        </div>

        <div class="card" style="max-width: 850px; margin: 0 auto;">
            <div class="card-header">
                <div>
                    <div class="card-title">
                        <span>📅 Register New Dental Appointment</span>
                    </div>
                    <p style="font-size: 0.85rem; color: var(--text-muted); margin-top: 4px;">
                        Each booking generates a unique Appointment Number with built-in <strong>Double-Booking Conflict Prevention</strong>.
                    </p>
                </div>
            </div>

            <div class="card-body">
                <form action="<%= request.getContextPath() %>/appointments/book" method="POST" id="appointmentBookingForm">
                    
                    <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 20px;">
                        
                        <!-- Patient Selection -->
                        <div class="form-group" style="grid-column: span 2;">
                            <label class="form-label" for="patientSelect">Select Patient <span class="required">*</span></label>
                            <div style="display: flex; gap: 8px;">
                                <select id="patientSelect" name="patientId" class="form-select" required style="flex-grow: 1;">
                                    <option value="">-- Choose Registered Patient --</option>
                                    <%
                                        if (patients != null) {
                                            for (Patient p : patients) {
                                                boolean selected = (preSelectPatientId != null && preSelectPatientId.equals(String.valueOf(p.getPatientId())));
                                    %>
                                        <option value="<%= p.getPatientId() %>" <%= selected ? "selected" : "" %>>
                                            <%= p.getFullName() %> (<%= p.getPatientCode() %>) - Tel: <%= p.getPhone() %>
                                        </option>
                                    <%
                                            }
                                        }
                                    %>
                                </select>
                                <a href="<%= request.getContextPath() %>/patient_register.jsp" class="btn btn-navy" title="Register New Patient">
                                    ➕ New Patient
                                </a>
                            </div>
                        </div>

                        <!-- Dentist Selection -->
                        <div class="form-group">
                            <label class="form-label" for="doctorSelect">Consulting Dentist <span class="required">*</span></label>
                            <select id="doctorSelect" name="doctorId" class="form-select" required>
                                <option value="">-- Choose Dentist --</option>
                                <%
                                    if (doctors != null) {
                                        for (Doctor d : doctors) {
                                %>
                                    <option value="<%= d.getDoctorId() %>" data-fee="<%= d.getConsultationFee() %>" data-room="<%= d.getRoomNumber() %>">
                                        <%= d.getDoctorName() %> (<%= d.getSpecialization() %>) - <%= d.getRoomNumber() %>
                                    </option>
                                <%
                                        }
                                    }
                                %>
                            </select>
                        </div>

                        <!-- Treatment Selection -->
                        <div class="form-group">
                            <label class="form-label" for="treatmentSelect">Treatment / Procedure <span class="required">*</span></label>
                            <select id="treatmentSelect" name="treatmentId" class="form-select" required>
                                <option value="">-- Choose Treatment --</option>
                                <%
                                    if (treatments != null) {
                                        for (Treatment t : treatments) {
                                %>
                                    <option value="<%= t.getTreatmentId() %>" data-cost="<%= t.getStandardCost() %>" data-duration="<%= t.getEstimatedDurationMins() %>">
                                        <%= t.getTreatmentName() %> (LKR <%= String.format("%,.2f", t.getStandardCost()) %>)
                                    </option>
                                <%
                                        }
                                    }
                                %>
                            </select>
                        </div>

                        <!-- Date Selection -->
                        <div class="form-group">
                            <label class="form-label" for="appointmentDate">Appointment Date <span class="required">*</span></label>
                            <input type="date" id="appointmentDate" name="appointmentDate" class="form-control" 
                                   value="<%= DateUtil.getCurrentSqlDate() %>" min="<%= DateUtil.getCurrentSqlDate() %>" required>
                        </div>

                        <!-- Time Slot Selection with Conflict Prevention -->
                        <div class="form-group">
                            <label class="form-label" for="appointmentTime">Available Time Slot <span class="required">*</span></label>
                            <select id="appointmentTime" name="appointmentTime" class="form-select" required>
                                <option value="">-- Select Date & Dentist First --</option>
                                <option value="09:00:00">09:00 AM</option>
                                <option value="09:30:00">09:30 AM</option>
                                <option value="10:00:00">10:00 AM</option>
                                <option value="10:30:00">10:30 AM</option>
                                <option value="11:00:00">11:00 AM</option>
                                <option value="11:30:00">11:30 AM</option>
                                <option value="12:00:00">12:00 PM</option>
                                <option value="14:00:00">02:00 PM</option>
                                <option value="14:30:00">02:30 PM</option>
                                <option value="15:00:00">03:00 PM</option>
                                <option value="15:30:00">03:30 PM</option>
                                <option value="16:00:00">04:00 PM</option>
                                <option value="16:30:00">04:30 PM</option>
                                <option value="17:00:00">05:00 PM</option>
                                <option value="17:30:00">05:30 PM</option>
                                <option value="18:00:00">06:00 PM</option>
                            </select>
                            <div id="slotConflictAlert" style="display:none; font-size: 0.8rem; color: #b91c1c; margin-top: 4px; font-weight: 600;">
                                ⚠️ Slot collision! Please select another time.
                            </div>
                        </div>

                        <!-- Reason / Symptoms -->
                        <div class="form-group" style="grid-column: span 2;">
                            <label class="form-label" for="reason">Patient Reason / Symptoms / Notes</label>
                            <textarea id="reason" name="reason" class="form-control" rows="2" 
                                      placeholder="e.g. Severe toothache on lower left molar, routine check-up, sensitivity to cold"></textarea>
                        </div>

                        <!-- Live Cost Preview -->
                        <div style="grid-column: span 2; background-color: var(--bg-subtle); border-radius: var(--radius-md); padding: 16px; border: 1px dashed var(--border-color);">
                            <div style="display: flex; justify-content: space-between; align-items: center;">
                                <div>
                                    <div style="font-size: 0.85rem; color: var(--text-muted);">Estimated Total Bill:</div>
                                    <div style="font-size: 1.25rem; font-weight: 800; color: var(--teal-700);">
                                        LKR <span id="previewTotalCost">0.00</span>
                                    </div>
                                </div>
                                <div style="font-size: 0.82rem; color: var(--text-secondary); text-align: right;">
                                    <div>Consultation: LKR <span id="previewDocFee">0.00</span></div>
                                    <div>Treatment: LKR <span id="previewTreatCost">0.00</span></div>
                                </div>
                            </div>
                        </div>

                    </div>

                    <div style="display: flex; justify-content: flex-end; gap: 12px; margin-top: 24px;">
                        <a href="<%= request.getContextPath() %>/receptionist_dashboard.jsp" class="btn btn-outline">Cancel</a>
                        <button type="submit" class="btn btn-primary">
                            Confirm & Schedule Appointment ➔
                        </button>
                    </div>

                </form>
            </div>
        </div>

    </main>

<script src="<%= request.getContextPath() %>/assets/js/appointment-booking.js"></script>
<jsp:include page="footer.jsp" />
