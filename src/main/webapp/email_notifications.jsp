<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.sunrisedental.model.EmailNotification" %>
<%
    request.setAttribute("pageTitle", "Email Notifications");
    List<EmailNotification> emails = (List<EmailNotification>) request.getAttribute("emailNotifications");
%>
<jsp:include page="header.jsp" />
<jsp:include page="sidebar.jsp" />

<div class="app-main">
    <jsp:include page="topbar.jsp" />

    <main class="content-body">
        <jsp:include page="alerts.jsp" />

        <div class="card">
            <div class="card-header">
                <div class="card-title">
                    <span>📧 Patient Email Outbox</span>
                </div>
                <span class="badge badge-confirmed">Observer Pattern</span>
            </div>
            <div class="card-body">
                <p style="color: var(--text-muted); margin-bottom: 16px; font-size: 0.9rem;">
                    Confirmation emails are queued when appointments are booked/cancelled and when invoices or payments are recorded.
                    Each message is stored here so staff can verify delivery during demonstrations.
                </p>
                <div class="table-responsive">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>#</th>
                                <th>Sent</th>
                                <th>Event</th>
                                <th>Recipient</th>
                                <th>Subject</th>
                                <th>Status</th>
                            </tr>
                        </thead>
                        <tbody>
                        <% if (emails != null && !emails.isEmpty()) {
                            for (EmailNotification mail : emails) { %>
                            <tr>
                                <td><%= mail.getEmailId() %></td>
                                <td><%= mail.getCreatedAt() %></td>
                                <td><%= mail.getEventType() %></td>
                                <td><%= mail.getRecipient() %></td>
                                <td><%= mail.getSubject() %></td>
                                <td><span class="badge badge-confirmed"><%= mail.getDeliveryStatus() %></span></td>
                            </tr>
                            <tr>
                                <td colspan="6" style="white-space: pre-wrap; color: var(--text-secondary); font-size: 0.82rem; background: var(--bg-subtle);">
<%= mail.getBody() != null ? mail.getBody() : "" %>
                                </td>
                            </tr>
                        <%  }
                           } else { %>
                            <tr>
                                <td colspan="6" style="text-align:center; color: var(--text-muted);">No email notifications yet. Book an appointment for a patient who has an email address.</td>
                            </tr>
                        <% } %>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </main>

<jsp:include page="footer.jsp" />
