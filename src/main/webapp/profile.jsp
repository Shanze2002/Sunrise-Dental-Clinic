<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.sunrisedental.model.User" %>
<%@ page import="com.sunrisedental.config.AppConfig" %>
<%@ page import="jakarta.servlet.http.Cookie" %>

<%
    User logged = (User) session.getAttribute(AppConfig.SESSION_USER);
    request.setAttribute("pageTitle", "Staff Profile");
%>
<jsp:include page="header.jsp" />
<jsp:include page="sidebar.jsp" />

<div class="app-main">
    <jsp:include page="topbar.jsp" />

    <main class="content-body">
        <jsp:include page="alerts.jsp" />

        <div class="card" style="max-width: 650px; margin: 0 auto;">
            <div class="card-header">
                <div class="card-title">
                    <span>👤 My Staff Profile</span>
                </div>
                <span class="badge badge-confirmed"><%= logged != null ? logged.getRoleName() : "Staff" %></span>
            </div>

            <div class="card-body">
                <% if (logged != null) { %>
                    <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 18px; margin-bottom: 24px;">
                        <div>
                            <div class="form-label">Username</div>
                            <div style="font-weight: 700; font-size: 1.05rem;"><%= logged.getUsername() %></div>
                        </div>

                        <div>
                            <div class="form-label">Full Name</div>
                            <div style="font-weight: 700; font-size: 1.05rem;"><%= logged.getFullName() %></div>
                        </div>

                        <div>
                            <div class="form-label">Email</div>
                            <div><%= logged.getEmail() %></div>
                        </div>

                        <div>
                            <div class="form-label">Phone</div>
                            <div><%= logged.getPhone() %></div>
                        </div>
                    </div>

                    <hr style="border: 0; border-top: 1px solid var(--border-color); margin: 20px 0;">

                    <h4 style="font-size: 0.95rem; font-weight: 700; margin-bottom: 8px; color: var(--primary-900);">
                        🍪 Cookie Preferences
                    </h4>
                    <p style="font-size: 0.82rem; color: var(--text-muted); margin-bottom: 12px;">
                        Theme and last-module cookies are stored in your browser. Remember-me stores your username only (not the password).
                    </p>
                    <p style="font-size: 0.85rem; margin-bottom: 12px;">
                        Last module cookie:
                        <strong>
                            <%
                                String lastModule = null;
                                Cookie[] prefCookies = request.getCookies();
                                if (prefCookies != null) {
                                    for (Cookie c : prefCookies) {
                                        if (AppConfig.COOKIE_LAST_MODULE.equals(c.getName())) lastModule = c.getValue();
                                    }
                                }
                            %>
                            <%= lastModule != null ? lastModule : "Not set yet" %>
                        </strong>
                    </p>
                    <button type="button" class="btn btn-outline btn-sm js-theme-toggle">Toggle Light / Dark Theme</button>

                    <h4 style="font-size: 0.95rem; font-weight: 700; margin-bottom: 14px; color: var(--primary-900);">
                        🔑 Change My Password
                    </h4>

                    <form action="<%= request.getContextPath() %>/admin/users/reset-password" method="POST">
                        <input type="hidden" name="userId" value="<%= logged.getUserId() %>">

                        <div class="form-group">
                            <label class="form-label" for="newPassword">New Password <span class="required">*</span></label>
                            <input type="password" id="newPassword" name="newPassword" class="form-control" placeholder="Enter new strong password" required>
                        </div>

                        <div style="display: flex; justify-content: flex-end; margin-top: 16px;">
                            <button type="submit" class="btn btn-primary">Update Password</button>
                        </div>
                    </form>
                <% } else { %>
                    <p style="color: var(--text-muted);">Please log in to view your profile.</p>
                <% } %>
            </div>
        </div>
    </main>

<jsp:include page="footer.jsp" />
