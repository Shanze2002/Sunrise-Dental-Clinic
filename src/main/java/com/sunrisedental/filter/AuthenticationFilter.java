package com.sunrisedental.filter;

import com.sunrisedental.config.AppConfig;
import com.sunrisedental.model.User;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Filter / Interceptor Pattern: AuthenticationFilter
 * Intercepts requests to ensure user is authenticated before accessing protected system modules.
 */
@WebFilter(filterName = "AuthenticationFilter", urlPatterns = {"/*"})
public class AuthenticationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization if needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String path = requestURI.substring(contextPath.length());

        // Allow static assets, login page, auth endpoints, and help
        boolean isStaticAsset = path.startsWith("/assets/") || path.endsWith(".css") || path.endsWith(".js") || path.endsWith(".png") || path.endsWith(".jpg") || path.endsWith(".ico");
        boolean isAuthEndpoint = path.startsWith("/auth/") || path.equals("/index.jsp") || path.equals("/");
        boolean isHelpEndpoint = path.startsWith("/help");

        if (isStaticAsset || isAuthEndpoint || isHelpEndpoint) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = httpRequest.getSession(false);
        User user = (session != null) ? (User) session.getAttribute(AppConfig.SESSION_USER) : null;

        if (user == null) {
            // User is not logged in -> redirect to login
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/auth/login?msg=session_expired");
            return;
        }

        // User is logged in -> proceed
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Cleanup if needed
    }
}
