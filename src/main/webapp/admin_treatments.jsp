<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.sunrisedental.model.Treatment" %>
<%@ page import="com.sunrisedental.service.factory.ServiceFactory" %>

<%
    List<Treatment> treatments = ServiceFactory.getTreatmentService().getAllTreatments();
    request.setAttribute("pageTitle", "Dental Treatments & Pricing");
%>
<jsp:include page="header.jsp" />
<jsp:include page="sidebar.jsp" />

<div class="app-main">
    <jsp:include page="topbar.jsp" />

    <main class="content-body">
        <jsp:include page="alerts.jsp" />

        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px;">
            <div>
                <h2 style="font-size: 1.25rem; font-weight: 700; color: var(--primary-900);">Dental Services & Treatment Catalog</h2>
                <p style="font-size: 0.85rem; color: var(--text-muted);">Manage standard treatment costs, categories, and estimated chair durations.</p>
            </div>
            <button class="btn btn-primary" onclick="openModal('addTreatmentModal')">
                <span>➕ Add New Treatment</span>
            </button>
        </div>

        <div class="card">
            <div class="card-header">
                <div class="card-title">
                    <span>💊 Active Treatment Catalog</span>
                </div>
                <input type="text" id="tableSearchInput" class="form-control" placeholder="Search treatments..." style="width: 220px; padding: 6px 12px;">
            </div>

            <div class="card-body" style="padding: 0;">
                <div class="table-responsive">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Code</th>
                                <th>Treatment Name</th>
                                <th>Category</th>
                                <th>Standard Fee</th>
                                <th>Est. Duration</th>
                                <th>Description</th>
                                <th>Status</th>
                                <th style="text-align: right;">Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                if (treatments != null && !treatments.isEmpty()) {
                                    for (Treatment t : treatments) {
                            %>
                                <tr>
                                    <td><code><%= t.getTreatmentCode() %></code></td>
                                    <td><strong><%= t.getTreatmentName() %></strong></td>
                                    <td><span class="badge badge-confirmed"><%= t.getCategory() %></span></td>
                                    <td><strong>LKR <%= String.format("%,.2f", t.getStandardCost()) %></strong></td>
                                    <td><%= t.getEstimatedDurationMins() %> mins</td>
                                    <td style="font-size: 0.82rem; color: var(--text-secondary); max-width: 250px;"><%= t.getDescription() != null ? t.getDescription() : "-" %></td>
                                    <td>
                                        <% if (t.isActive()) { %>
                                            <span class="badge badge-paid">Active</span>
                                        <% } else { %>
                                            <span class="badge badge-unpaid">Disabled</span>
                                        <% } %>
                                    </td>
                                    <td style="text-align: right;">
                                        <button type="button" class="btn btn-outline btn-sm"
                                                onclick="openEditTreatModal(<%= t.getTreatmentId() %>, '<%= t.getTreatmentName() %>', '<%= t.getCategory() %>', <%= t.getStandardCost() %>, <%= t.getEstimatedDurationMins() %>, '<%= t.getDescription() != null ? t.getDescription().replace("'", "\\'") : "" %>', <%= t.isActive() %>)">
                                            Edit
                                        </button>
                                    </td>
                                </tr>
                            <%
                                    }
                                } else {
                            %>
                                <tr>
                                    <td colspan="8" style="text-align: center; padding: 32px; color: var(--text-muted);">
                                        No treatments configured.
                                    </td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <!-- Add Treatment Modal -->
        <div id="addTreatmentModal" class="modal-overlay">
            <div class="modal-box">
                <div class="modal-header">
                    <div class="card-title">
                        <span>💊 Add Dental Treatment</span>
                    </div>
                    <button type="button" class="btn btn-outline btn-sm" onclick="closeModal('addTreatmentModal')">✕</button>
                </div>

                <form action="<%= request.getContextPath() %>/admin/treatments/create" method="POST">
                    <div class="modal-body">
                        <div class="form-group">
                            <label class="form-label" for="treatmentName">Treatment Name <span class="required">*</span></label>
                            <input type="text" id="treatmentName" name="treatmentName" class="form-control" placeholder="e.g. Laser Teeth Whitening" required>
                        </div>

                        <div class="form-group">
                            <label class="form-label" for="category">Category <span class="required">*</span></label>
                            <select id="category" name="category" class="form-select" required>
                                <option value="Diagnostic">Diagnostic</option>
                                <option value="Preventive">Preventive</option>
                                <option value="Restorative">Restorative</option>
                                <option value="Endodontics">Endodontics</option>
                                <option value="Oral Surgery">Oral Surgery</option>
                                <option value="Orthodontics">Orthodontics</option>
                                <option value="Cosmetic">Cosmetic</option>
                                <option value="Prosthodontics">Prosthodontics</option>
                            </select>
                        </div>

                        <div class="form-group">
                            <label class="form-label" for="standardCost">Standard Fee (LKR) <span class="required">*</span></label>
                            <input type="number" step="100" id="standardCost" name="standardCost" class="form-control" placeholder="0.00" required>
                        </div>

                        <div class="form-group">
                            <label class="form-label" for="estimatedDurationMins">Estimated Duration (Minutes)</label>
                            <input type="number" id="estimatedDurationMins" name="estimatedDurationMins" class="form-control" value="30" required>
                        </div>

                        <div class="form-group">
                            <label class="form-label" for="description">Description</label>
                            <textarea id="description" name="description" class="form-control" placeholder="Details of this treatment..."></textarea>
                        </div>
                    </div>

                    <div class="modal-footer">
                        <button type="button" class="btn btn-outline" onclick="closeModal('addTreatmentModal')">Cancel</button>
                        <button type="submit" class="btn btn-primary">Save Treatment</button>
                    </div>
                </form>
            </div>
        </div>

        <!-- Edit Treatment Modal -->
        <div id="editTreatmentModal" class="modal-overlay">
            <div class="modal-box">
                <div class="modal-header">
                    <div class="card-title">
                        <span>✏️ Edit Dental Treatment</span>
                    </div>
                    <button type="button" class="btn btn-outline btn-sm" onclick="closeModal('editTreatmentModal')">✕</button>
                </div>

                <form action="<%= request.getContextPath() %>/admin/treatments/edit" method="POST">
                    <input type="hidden" id="editTreatmentId" name="treatmentId">

                    <div class="modal-body">
                        <div class="form-group">
                            <label class="form-label" for="editTreatName">Treatment Name <span class="required">*</span></label>
                            <input type="text" id="editTreatName" name="treatmentName" class="form-control" required>
                        </div>

                        <div class="form-group">
                            <label class="form-label" for="editCategory">Category <span class="required">*</span></label>
                            <select id="editCategory" name="category" class="form-select" required>
                                <option value="Diagnostic">Diagnostic</option>
                                <option value="Preventive">Preventive</option>
                                <option value="Restorative">Restorative</option>
                                <option value="Endodontics">Endodontics</option>
                                <option value="Oral Surgery">Oral Surgery</option>
                                <option value="Orthodontics">Orthodontics</option>
                                <option value="Cosmetic">Cosmetic</option>
                                <option value="Prosthodontics">Prosthodontics</option>
                            </select>
                        </div>

                        <div class="form-group">
                            <label class="form-label" for="editCost">Standard Fee (LKR) <span class="required">*</span></label>
                            <input type="number" step="100" id="editCost" name="standardCost" class="form-control" required>
                        </div>

                        <div class="form-group">
                            <label class="form-label" for="editDuration">Estimated Duration (Minutes)</label>
                            <input type="number" id="editDuration" name="estimatedDurationMins" class="form-control" required>
                        </div>

                        <div class="form-group">
                            <label class="form-label" for="editDescription">Description</label>
                            <textarea id="editDescription" name="description" class="form-control"></textarea>
                        </div>

                        <div class="form-group">
                            <label style="display: flex; align-items: center; gap: 8px; font-weight: 600; cursor: pointer;">
                                <input type="checkbox" id="editTreatActive" name="active" value="true">
                                Active for scheduling
                            </label>
                        </div>
                    </div>

                    <div class="modal-footer">
                        <button type="button" class="btn btn-outline" onclick="closeModal('editTreatmentModal')">Cancel</button>
                        <button type="submit" class="btn btn-primary">Update Treatment</button>
                    </div>
                </form>
            </div>
    </main>

<script>
function openEditTreatModal(id, name, cat, cost, dur, desc, active) {
    document.getElementById('editTreatmentId').value = id;
    document.getElementById('editTreatName').value = name;
    document.getElementById('editCategory').value = cat;
    document.getElementById('editCost').value = cost;
    document.getElementById('editDuration').value = dur;
    document.getElementById('editDescription').value = desc;
    document.getElementById('editTreatActive').checked = active;
    openModal('editTreatmentModal');
}
</script>
<jsp:include page="footer.jsp" />
