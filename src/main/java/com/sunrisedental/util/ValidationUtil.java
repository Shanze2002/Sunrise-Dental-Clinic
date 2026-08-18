package com.sunrisedental.util;

import java.util.regex.Pattern;

/**
 * Utility: ValidationUtil
 * Validates inputs against business rules and security policies.
 */
public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9+()\\s-]{9,20}$");
    private static final Pattern NIC_OLD_PATTERN = Pattern.compile("^[0-9]{9}[vVxX]$");
    private static final Pattern NIC_NEW_PATTERN = Pattern.compile("^[0-9]{12}$");

    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) return false;
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) return false;
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    public static boolean isValidNIC(String nic) {
        if (nic == null || nic.trim().isEmpty()) return true; // optional in some cases
        String trimmed = nic.trim();
        return NIC_OLD_PATTERN.matcher(trimmed).matches() || NIC_NEW_PATTERN.matcher(trimmed).matches();
    }

    public static double parseDoubleOrDefault(String str, double defaultVal) {
        if (str == null || str.trim().isEmpty()) return defaultVal;
        try {
            return Double.parseDouble(str.trim());
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    public static int parseIntOrDefault(String str, int defaultVal) {
        if (str == null || str.trim().isEmpty()) return defaultVal;
        try {
            return Integer.parseInt(str.trim());
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    public static String sanitizeHtml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#x27;");
    }
}
