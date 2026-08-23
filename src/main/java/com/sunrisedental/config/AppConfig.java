package com.sunrisedental.config;

/**
 * Application Configuration and System Constants
 * Sunrise Dental Clinic - Colombo
 */
public class AppConfig {
    public static final String CLINIC_NAME = "Sunrise Dental Clinic";
    public static final String CLINIC_TAGLINE = "Advanced Dental Care & Implantology";
    public static final String CLINIC_ADDRESS = "No. 128, Galle Road, Kollupitiya, Colombo 03, Sri Lanka";
    public static final String CLINIC_PHONE = "+94 11 258 9631 / +94 77 123 4567";
    public static final String CLINIC_EMAIL = "info@sunrisedental.lk";
    public static final String CLINIC_WEB = "www.sunrisedental.lk";
    public static final String CLINIC_REG_NO = "PV-88912/COL/2020";

    // Session Attribute Keys & Timeout
    public static final String SESSION_USER = "loggedUser";
    public static final String SESSION_ROLE = "userRole";
    public static final String SESSION_USER_ROLE = "userRole";
    public static final int SESSION_TIMEOUT_SECONDS = 1800; // 30 minutes
    public static final String SESSION_FLASH_SUCCESS = "flashSuccess";
    public static final String SESSION_FLASH_ERROR = "flashError";

    // Persistent cookies (remember-me, theme, last module, consent)
    public static final String COOKIE_USERNAME = "sdc_username";
    public static final String COOKIE_ROLE = "sdc_role";
    public static final String COOKIE_THEME = "sdc_theme";
    public static final String COOKIE_LAST_MODULE = "sdc_last_module";
    public static final String COOKIE_CONSENT = "sdc_cookie_consent";
    public static final String REQUEST_UI_THEME = "uiTheme";
    public static final int COOKIE_REMEMBER_SECONDS = 30 * 24 * 60 * 60;
    public static final int COOKIE_PREFERENCE_SECONDS = 90 * 24 * 60 * 60;

    // Tax & Default settings
    public static final double DEFAULT_TAX_RATE = 0.00;
    public static final double DEFAULT_TAX_PERCENTAGE = 0.00;
    public static final String DEFAULT_CURRENCY = "LKR";

    private AppConfig() {
        // Prevent instantiation
    }
}
