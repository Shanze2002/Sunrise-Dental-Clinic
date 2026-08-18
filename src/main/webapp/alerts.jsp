<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String successMsg = (String) request.getAttribute("successMessage");
    if (successMsg == null) successMsg = request.getParameter("success");

    String errMsg = (String) request.getAttribute("errorMessage");
    if (errMsg == null) errMsg = request.getParameter("error");

    String infoMsg = (String) request.getAttribute("infoMessage");
    if (infoMsg == null) infoMsg = request.getParameter("info");

    if ("access_denied".equalsIgnoreCase(errMsg)) {
        errMsg = "Access Denied: Your account role does not have permission to access that section.";
    }
%>

<% if (successMsg != null && !successMsg.trim().isEmpty()) { %>
    <div class="alert alert-success" role="alert">
        <span>✅</span>
        <div style="flex-grow: 1;"><%= successMsg %></div>
        <button type="button" style="background:none;border:none;cursor:pointer;font-size:1.1rem;color:inherit;" onclick="this.parentElement.style.display='none';">&times;</button>
    </div>
<% } %>

<% if (errMsg != null && !errMsg.trim().isEmpty()) { %>
    <div class="alert alert-danger" role="alert">
        <span>⚠️</span>
        <div style="flex-grow: 1;"><%= errMsg %></div>
        <button type="button" style="background:none;border:none;cursor:pointer;font-size:1.1rem;color:inherit;" onclick="this.parentElement.style.display='none';">&times;</button>
    </div>
<% } %>

<% if (infoMsg != null && !infoMsg.trim().isEmpty()) { %>
    <div class="alert alert-info" role="alert">
        <span>ℹ️</span>
        <div style="flex-grow: 1;"><%= infoMsg %></div>
        <button type="button" style="background:none;border:none;cursor:pointer;font-size:1.1rem;color:inherit;" onclick="this.parentElement.style.display='none';">&times;</button>
    </div>
<% } %>
