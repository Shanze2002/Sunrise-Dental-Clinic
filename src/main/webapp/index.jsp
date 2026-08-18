<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // Redirect directly to the login portal or dashboard
    response.sendRedirect(request.getContextPath() + "/auth/login");
%>

