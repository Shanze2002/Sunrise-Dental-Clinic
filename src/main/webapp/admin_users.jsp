<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.sunrisedental.model.*" %>
<%@ page import="com.sunrisedental.service.factory.ServiceFactory" %>
<%@ page import="com.sunrisedental.util.DateUtil" %>

<%
    List<User> users = ServiceFactory.getUserService().getAllUsers();
    request.setAttribute("pageTitle", "Staff & User Management");
%>
<jsp:include page="header.jsp" />
<jsp:include page="sidebar.jsp" />

<div class="app-main">
    <jsp:include page="topbar.jsp" />

    <main class="content-body">
        <jsp:include page="alerts.jsp" />

        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px;">
            <div>
                <h2 style="font-size: 1.25rem; font-weight: 700; color: var(--primary-900);">Staff Access & Permissions</h2>
                <p style="font-size: 0.85rem; color: var(--text-muted);">Manage clinic employee logins, roles, and credential security.</p>
            </div>
            <button class="btn btn-primary" onclick="openModal('createUserModal')">
                <span>➕ Add New Staff User</span>
            </button>
        </div>

        <div class="card">
            <div class="card-header">
                <div class="card-title">
                    <span>👥 Active Staff Accounts (<%= users != null ? users.size() : 0 %>)</span>
                </div>
                <input type="text" id="tableSearchInput" class="form-control" placeholder="Search staff..." style="width: 220px; padding: 6px 12px;">
            </div>

            <div class="card-body" style="padding: 0;">
                <div class="table-responsive">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>User ID</th>
                                <th>Username</th>
                                <th>Full Name</th>
                                <th>Role</th>
                                <th>Email</th>
                                <th>Contact Phone</th>
                                <th>Status</th>
                                <th style="text-align: right;">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                if (users != null && !users.isEmpty()) {
                                    for (User u : users) {
                            %>
                                <tr>
                                    <td>#<%= u.getUserId() %></td>
                                    <td><strong><%= u.getUsername() %></strong></td>
                                    <td><%= u.getFullName() %></td>
                                    <td>
                                        <span class="badge badge-confirmed"><%= u.getRoleName() %></span>
                                    </td>
                                    <td><%= u.getEmail() %></td>
                                    <td><%= u.getPhone() %></td>
                                    <td>
                                        <% if (u.isActive()) { %>
                                            <span class="badge badge-paid">Active</span>
                                        <% } else { %>
                                            <span class="badge badge-unpaid">Disabled</span>
                                        <% } %>
                                    </td>
                                    <td style="text-align: right;">
                                        <div style="display: inline-flex; gap: 6px;">
                                            <button type="button" class="btn btn-outline btn-sm" 
                                                    onclick="openPasswordModal(<%= u.getUserId() %>, '<%= u.getUsername() %>')">
                                                Reset Password
                                            </button>
                                            <a href="<%= request.getContextPath() %>/admin/users/toggle?id=<%= u.getUserId() %>&active=<%= !u.isActive() %>" 
                                               class="btn <%= u.isActive() ? "btn-danger" : "btn-primary" %> btn-sm"
                                               onclick="return confirm('Are you sure you want to <%= u.isActive() ? "deactivate" : "activate" %> this user?');">
                                                <%= u.isActive() ? "Disable" : "Enable" %>
                                            </a>
                                        </div>
                                    </td>
                                </tr>
                            <%
                                    }
                                } else {
                            %>
                                <tr>
                                    <td colspan="8" style="text-align: center; padding: 32px; color: var(--text-muted);">
                                        No users registered.
                                    </td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <!-- Add User Modal Dialog -->
        <div id="createUserModal" class="modal-overlay">
            <div class="modal-box">
                <div class="modal-header">
                    <div class="card-title">
                        <span>👤 Register New Staff Account</span>
                    </div>
                    <button type="button" class="btn btn-outline btn-sm" onclick="closeModal('createUserModal')">✕</button>
                </div>

                <form action="<%= request.getContextPath() %>/admin/users/create" method="POST">
                    <div class="modal-body">
                        <div class="form-group">
                            <label class="form-label" for="newUsername">Username <span class="required">*</span></label>
                            <input type="text" id="newUsername" name="username" class="form-control" placeholder="e.g. receptionist2" required>
                        </div>

                        <div class="form-group">
                            <label class="form-label" for="newPassword">Initial Password <span class="required">*</span></label>
                            <input type="password" id="newPassword" name="password" class="form-control" placeholder="Strong password (min 6 chars)" required>
                        </div>

                        <div class="form-group">
                            <label class="form-label" for="newFullName">Full Name <span class="required">*</span></label>
                            <input type="text" id="newFullName" name="fullName" class="form-control" placeholder="e.g. Ruwanthi Perera" required>
                        </div>

                        <div class="form-group">
                            <label class="form-label" for="newRoleId">System Role <span class="required">*</span></label>
                            <select id="newRoleId" name="roleId" class="form-select" required>
                                <option value="2">RECEPTIONIST (Appointments & Patients)</option>
                                <option value="3">DOCTOR (Clinical & Dental Charts)</option>
                                <option value="4">CASHIER (Billing & Invoices)</option>
                                <option value="1">ADMIN (Director & Full Access)</option>
                            </select>
                        </div>

                        <div class="form-group">
                            <label class="form-label" for="newEmail">Email Address <span class="required">*</span></label>
                            <input type="email" id="newEmail" name="email" class="form-control" placeholder="staff@sunrisedental.lk" required>
                        </div>

                        <div class="form-group">
                            <label class="form-label" for="newPhone">Contact Phone</label>
                            <input type="tel" id="newPhone" name="phone" class="form-control" placeholder="077 123 4567">
                        </div>
                    </div>

                    <div class="modal-footer">
                        <button type="button" class="btn btn-outline" onclick="closeModal('createUserModal')">Cancel</button>
                        <button type="submit" class="btn btn-primary">Create User Account</button>
                    </div>
                </form>
            </div>
        </div>

        <!-- Reset Password Modal Dialog -->
        <div id="resetPasswordModal" class="modal-overlay">
            <div class="modal-box" style="max-width: 420px;">
                <div class="modal-header">
                    <div class="card-title">
                        <span>🔑 Reset Staff Password</span>
                    </div>
                    <button type="button" class="btn btn-outline btn-sm" onclick="closeModal('resetPasswordModal')">✕</button>
                </div>

                <form action="<%= request.getContextPath() %>/admin/users/reset-password" method="POST">
                    <input type="hidden" id="resetUserId" name="userId">

                    <div class="modal-body">
                        <p style="font-size: 0.9rem; margin-bottom: 16px;">
                            Resetting password for user: <strong id="resetUsernameDisplay" style="color: var(--teal-700);"></strong>
                        </p>

                        <div class="form-group">
                            <label class="form-label" for="resetNewPass">New Password <span class="required">*</span></label>
                            <input type="password" id="resetNewPass" name="newPassword" class="form-control" placeholder="Enter new password" required>
                        </div>
                    </div>

                    <div class="modal-footer">
                        <button type="button" class="btn btn-outline" onclick="closeModal('resetPasswordModal')">Cancel</button>
                        <button type="submit" class="btn btn-primary">Update Password</button>
                    </div>
                </form>
            </div>
    </main>

<script>
function openPasswordModal(userId, username) {
    document.getElementById('resetUserId').value = userId;
    document.getElementById('resetUsernameDisplay').textContent = username;
    openModal('resetPasswordModal');
}
</script>
<jsp:include page="footer.jsp" />
