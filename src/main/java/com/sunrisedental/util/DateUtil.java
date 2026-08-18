package com.sunrisedental.util;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Utility: DateUtil
 * Provides date/time formatting and parsing helpers.
 */
public class DateUtil {

    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String TIME_FORMAT_24 = "HH:mm:ss";
    private static final String TIME_FORMAT_SHORT = "HH:mm";
    private static final String DISPLAY_DATE_FORMAT = "dd MMM yyyy";
    private static final String DISPLAY_TIME_FORMAT = "hh:mm a";
    private static final String DISPLAY_DATETIME_FORMAT = "dd MMM yyyy, hh:mm a";

    public static Date parseSqlDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return null;
        try {
            java.util.Date parsed = new SimpleDateFormat(DATE_FORMAT).parse(dateStr.trim());
            return new Date(parsed.getTime());
        } catch (ParseException e) {
            return null;
        }
    }

    public static Time parseSqlTime(String timeStr) {
        if (timeStr == null || timeStr.trim().isEmpty()) return null;
        try {
            timeStr = timeStr.trim();
            if (timeStr.length() == 5) {
                timeStr = timeStr + ":00";
            }
            java.util.Date parsed = new SimpleDateFormat(TIME_FORMAT_24).parse(timeStr);
            return new Time(parsed.getTime());
        } catch (ParseException e) {
            return null;
        }
    }

    public static String formatDisplayDate(java.util.Date date) {
        if (date == null) return "-";
        return new SimpleDateFormat(DISPLAY_DATE_FORMAT).format(date);
    }

    public static String formatDisplayDate(Date date) {
        if (date == null) return "-";
        return new SimpleDateFormat(DISPLAY_DATE_FORMAT).format(date);
    }

    public static String formatDisplayDate(Timestamp timestamp) {
        if (timestamp == null) return "-";
        return new SimpleDateFormat(DISPLAY_DATE_FORMAT).format(timestamp);
    }

    public static String formatDisplayTime(Time time) {
        if (time == null) return "-";
        return new SimpleDateFormat(DISPLAY_TIME_FORMAT).format(time);
    }

    public static String formatDisplayTime(java.util.Date time) {
        if (time == null) return "-";
        return new SimpleDateFormat(DISPLAY_TIME_FORMAT).format(time);
    }

    public static String formatDisplayDateTime(Timestamp timestamp) {
        if (timestamp == null) return "-";
        return new SimpleDateFormat(DISPLAY_DATETIME_FORMAT).format(timestamp);
    }

    public static String formatDisplayDateTime(java.util.Date date) {
        if (date == null) return "-";
        return new SimpleDateFormat(DISPLAY_DATETIME_FORMAT).format(date);
    }

    public static String getCurrentSqlDate() {
        return new SimpleDateFormat(DATE_FORMAT).format(new java.util.Date());
    }

    public static String getCurrentYearMonth() {
        return new SimpleDateFormat("yyyy-MM").format(new java.util.Date());
    }
}
