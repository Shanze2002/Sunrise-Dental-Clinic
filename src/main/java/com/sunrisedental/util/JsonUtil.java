package com.sunrisedental.util;

import java.util.List;
import java.util.Map;

/**
 * Lightweight JSON utility for REST Web Service endpoints
 */
public class JsonUtil {

    public static String escape(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            switch (ch) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\b': sb.append("\\b"); break;
                case '\f': sb.append("\\f"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (ch < ' ') {
                        String t = "000" + Integer.toHexString(ch);
                        sb.append("\\u").append(t.substring(t.length() - 4));
                    } else {
                        sb.append(ch);
                    }
            }
        }
        return sb.toString();
    }

    public static String toJsonSuccess(String message, String dataJson) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"success\":true,");
        sb.append("\"message\":\"").append(escape(message)).append("\"");
        if (dataJson != null && !dataJson.trim().isEmpty()) {
            sb.append(",\"data\":").append(dataJson);
        }
        sb.append("}");
        return sb.toString();
    }

    public static String toJsonError(String message) {
        return "{\"success\":false,\"message\":\"" + escape(message) + "\"}";
    }

    public static String mapToJson(Map<String, ?> map) {
        if (map == null) return "{}";
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            if (!first) sb.append(",");
            sb.append("\"").append(escape(entry.getKey())).append("\":");
            Object val = entry.getValue();
            if (val == null) {
                sb.append("null");
            } else if (val instanceof Number || val instanceof Boolean) {
                sb.append(val);
            } else {
                sb.append("\"").append(escape(val.toString())).append("\"");
            }
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }

    public static String listToJson(List<String> jsonItems) {
        if (jsonItems == null) return "[]";
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < jsonItems.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(jsonItems.get(i));
        }
        sb.append("]");
        return sb.toString();
    }
}
