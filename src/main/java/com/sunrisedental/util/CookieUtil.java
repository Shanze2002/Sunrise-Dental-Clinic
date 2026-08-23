package com.sunrisedental.util;

import com.sunrisedental.config.AppConfig;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Cookie helper for remember-me, UI theme, last module, and consent cookies.
 */
public final class CookieUtil {

    private CookieUtil() {}

    public static void addCookie(HttpServletResponse resp, String name, String value, int maxAgeSeconds, boolean httpOnly) {
        Cookie cookie = new Cookie(name, value == null ? "" : value);
        cookie.setMaxAge(maxAgeSeconds);
        cookie.setPath("/");
        cookie.setHttpOnly(httpOnly);
        cookie.setAttribute("SameSite", "Lax");
        resp.addCookie(cookie);
    }

    public static String getCookieValue(HttpServletRequest req, String name) {
        Cookie[] cookies = req.getCookies();
        if (cookies == null) return null;
        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    public static void clearCookie(HttpServletResponse resp, String name) {
        addCookie(resp, name, "", 0, true);
    }

    public static void applyRememberMeCookies(HttpServletResponse resp, String username, String roleName) {
        addCookie(resp, AppConfig.COOKIE_USERNAME, username, AppConfig.COOKIE_REMEMBER_SECONDS, true);
        addCookie(resp, AppConfig.COOKIE_ROLE, roleName, AppConfig.COOKIE_REMEMBER_SECONDS, false);
    }

    public static void clearAuthCookies(HttpServletResponse resp) {
        clearCookie(resp, AppConfig.COOKIE_USERNAME);
        clearCookie(resp, AppConfig.COOKIE_ROLE);
        clearCookie(resp, AppConfig.COOKIE_LAST_MODULE);
    }
}
