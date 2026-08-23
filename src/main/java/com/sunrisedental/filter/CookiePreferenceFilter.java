package com.sunrisedental.filter;

import com.sunrisedental.config.AppConfig;
import com.sunrisedental.model.User;
import com.sunrisedental.util.CookieUtil;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Intercepting Filter: applies UI theme from cookies and records last visited module.
 */
@WebFilter(filterName = "CookiePreferenceFilter", urlPatterns = {"/*"})
public class CookiePreferenceFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String theme = CookieUtil.getCookieValue(httpRequest, AppConfig.COOKIE_THEME);
        if (theme == null || theme.isBlank()) {
            theme = "light";
        }
        httpRequest.setAttribute(AppConfig.REQUEST_UI_THEME, theme);

        HttpSession session = httpRequest.getSession(false);
        User user = (session != null) ? (User) session.getAttribute(AppConfig.SESSION_USER) : null;
        if (user != null) {
            String path = httpRequest.getServletPath();
            if (path != null && !path.startsWith("/assets") && !path.startsWith("/api")) {
                CookieUtil.addCookie(httpResponse, AppConfig.COOKIE_LAST_MODULE, path, AppConfig.COOKIE_PREFERENCE_SECONDS, false);
            }
        }

        chain.doFilter(request, response);
    }
}
