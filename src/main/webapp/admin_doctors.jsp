<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.sunrisedental.model.Doctor" %>
<%@ page import="com.sunrisedental.service.factory.ServiceFactory" %>

<%
    List<Doctor> docs = ServiceFactory.getDoctorService().getAllDoctors();
    request.setAttribute("pageTitle", "Dentists & Room Allocation");
%>
<jsp:include page="header.jsp" />
<jsp:include page="sidebar.jsp" />

<div class="app-main">
    <jsp:include page="topbar.jsp" />

    <main class="content-body">
        <jsp:include page="alerts.jsp" />

        <div style="margin-bottom: 24px;">
            <h2 style="font-size: 1.25rem; font-weight: 700; color: var(--primary-900);">Dentists & Surgery Rooms</h2>
            <p style="font-size: 0.85rem; color: var(--text-muted);">Configure consultation fees, surgery rooms, and active practicing status.</p>
        </div>

        <div class="card">
            <div class="card-header">
                <div class="card-title">
                    <span>👨‍⚕️ Medical Dental Staff</span>
                </div>
            </div>

            <div class="card-body" style="padding: 0;">
                <div class="table-responsive">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Doctor ID</th>
                                <th>Dentist Name</th>
                                <th>Specialization</th>
                                <th>SLMC License</th>
                                <th>Consultation Fee</th>
                                <th>Room Number</th>
                                <th>Available Days</th>
                                <th>Status</th>
                                <th style="text-align: right;">Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                if (docs != null && !docs.isEmpty()) {
                                    for (Doctor d : docs) {
                            %>
                                <tr>
                                    <td>#<%= d.getDoctorId() %></td>
                                    <td><strong><%= d.getDoctorName() %></strong></td>
                                    <td><%= d.getSpecialization() %></td>
                                    <td><code><%= d.getLicenseNumber() %></code></td>
                                    <td><strong>LKR <%= String.format("%,.2f", d.getConsultationFee()) %></strong></td>
                                    <td><span class="badge badge-confirmed"><%= d.getRoomNumber() %></span></td>
                                    <td style="font-size: 0.8rem;"><%= d.getAvailableDays() %></td>
                                    <td>
                                        <% if (d.isActive()) { %>
                                            <span class="badge badge-paid">Active</span>
                                        <% } else { %>
                                            <span class="badge badge-unpaid">Inactive</span>
                                        <% } %>
                                    </td>
                                    <td style="text-align: right;">
                                        <button type="button" class="btn btn-outline btn-sm"
                                                onclick="openEditDocModal(<%= d.getDoctorId() %>, '<%= d.getDoctorName() %>', '<%= d.getSpecialization() %>', '<%= d.getLicenseNumber() %>', <%= d.getConsultationFee() %>, '<%= d.getRoomNumber() %>', '<%= d.getAvailableDays() %>', <%= d.isActive() %>)">
                                            Edit Configuration
                                        </button>
                                    </td>
                                </tr>
                            <%
                                    }
                                } else {
                            %>
                                <tr>
                                    <td colspan="9" style="text-align: center; padding: 32px; color: var(--text-muted);">
                                        No doctors configured.
                                    </td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <!-- Edit Doctor Modal -->
        <div id="editDoctorModal" class="modal-overlay">
            <div class="modal-box">
                <div class="modal-header">
                    <div class="card-title">
                        <span>👨‍⚕️ Edit Doctor: <span id="docNameModal" style="color: var(--teal-700);"></span></span>
                    </div>
                    <button type="button" class="btn btn-outline btn-sm" onclick="closeModal('editDoctorModal')">✕</button>
                </div>

                <form action="<%= request.getContextPath() %>/admin/doctors/edit" method="POST">
                    <input type="hidden" id="editDoctorId" name="doctorId">

                    <div class="modal-body">
                        <div class="form-group">
                            <label class="form-label" for="editSpec">Specialization <span class="required">*</span></label>
                            <input type="text" id="editSpec" name="specialization" class="form-control" required>
                        </div>

                        <div class="form-group">
                            <label class="form-label" for="editLic">License Number <span class="required">*</span></label>
                            <input type="text" id="editLic" name="licenseNumber" class="form-control" required>
                        </div>

                        <div class="form-group">
                            <label class="form-label" for="editFee">Consultation Fee (LKR) <span class="required">*</span></label>
                            <input type="number" step="50" id="editFee" name="consultationFee" class="form-control" required>
                        </div>

                        <div class="form-group">
                            <label class="form-label" for="editRoom">Surgery Room <span class="required">*</span></label>
                            <input type="text" id="editRoom" name="roomNumber" class="form-control" required>
                        </div>

                        <div class="form-group">
                            <label class="form-label" for="editDays">Available Days</label>
                            <input type="text" id="editDays" name="availableDays" class="form-control">
                        </div>

                        <div class="form-group">
                            <label style="display: flex; align-items: center; gap: 8px; font-weight: 600; cursor: pointer;">
                                <input type="checkbox" id="editActive" name="active" value="true">
                                Active for appointment scheduling
                            </label>
                        </div>
                    </div>

                    <div class="modal-footer">
                        <button type="button" class="btn btn-outline" onclick="closeModal('editDoctorModal')">Cancel</button>
                        <button type="submit" class="btn btn-primary">Save Changes</button>
                    </div>
                </form>
            </div>
    </main>

<script>
function openEditDocModal(id, name, spec, lic, fee, room, days, active) {
    document.getElementById('editDoctorId').value = id;
    document.getElementById('docNameModal').textContent = name;
    document.getElementById('editSpec').value = spec;
    document.getElementById('editLic').value = lic;
    document.getElementById('editFee').value = fee;
    document.getElementById('editRoom').value = room;
    document.getElementById('editDays').value = days;
    document.getElementById('editActive').checked = active;
    openModal('editDoctorModal');
}
</script>
<jsp:include page="footer.jsp" />
