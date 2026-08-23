<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.sunrisedental.config.MailConfig" %>
<%
    request.setAttribute("pageTitle", "Email SMTP Setup");
    MailConfig mail = MailConfig.getInstance();
%>
<jsp:include page="header.jsp" />
<jsp:include page="sidebar.jsp" />

<div class="app-main">
    <jsp:include page="topbar.jsp" />

    <main class="content-body">
        <jsp:include page="alerts.jsp" />

        <div class="card" style="max-width: 720px; margin: 0 auto;">
            <div class="card-header">
                <div class="card-title"><span>📧 Patient Email (Gmail SMTP)</span></div>
                <span class="badge <%= mail.isEnabled() ? "badge-confirmed" : "badge-cancelled" %>">
                    <%= mail.isEnabled() ? "READY TO SEND" : "NOT CONFIGURED" %>
                </span>
            </div>
            <div class="card-body">
                <p style="font-size: 0.88rem; color: var(--text-muted); margin-bottom: 16px;">
                    Patient emails (registration, booking, bill remainder) are sent through Gmail.
                    Use a <strong>Gmail App Password</strong> (Google Account → Security → 2-Step Verification → App passwords), not the normal Gmail password.
                </p>

                <form action="<%= request.getContextPath() %>/admin/mail" method="POST">
                    <div class="form-group">
                        <label class="form-label">Enable sending</label>
                        <select name="mailEnabled" class="form-select">
                            <option value="true" <%= mail.isEnabled() ? "selected" : "" %>>On — send real emails</option>
                            <option value="false" <%= !mail.isEnabled() ? "selected" : "" %>>Off</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label class="form-label" for="mailUsername">Gmail address <span class="required">*</span></label>
                        <input type="email" id="mailUsername" name="mailUsername" class="form-control"
                               placeholder="yourclinic@gmail.com"
                               value="<%= mail.getUsername() != null ? mail.getUsername() : "" %>" required>
                    </div>
                    <div class="form-group">
                        <label class="form-label" for="mailPassword">Gmail App Password <span class="required">*</span></label>
                        <input type="password" id="mailPassword" name="mailPassword" class="form-control"
                               placeholder="16-character app password" required>
                    </div>
                    <div class="form-group">
                        <label class="form-label" for="testTo">Send a test email to (optional)</label>
                        <input type="email" id="testTo" name="testTo" class="form-control"
                               placeholder="your.own@gmail.com">
                    </div>
                    <button type="submit" class="btn btn-primary">Save SMTP &amp; Send Test</button>
                </form>
            </div>
        </div>
    </main>

<jsp:include page="footer.jsp" />
