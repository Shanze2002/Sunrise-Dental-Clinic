<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.sunrisedental.config.AppConfig" %>
    <footer class="app-footer no-print">
        <p>© 2026 <%= AppConfig.CLINIC_NAME %>, <%= AppConfig.CLINIC_ADDRESS %> | System Portal v1.0 (Jakarta EE 10 / MVC Architecture)</p>
    </footer>
</div> <!-- Closes app-main -->
</div> <!-- Closes app-layout -->

<div id="cookieConsentBanner" class="cookie-banner no-print" hidden>
    <div>
        <strong>Cookie notice.</strong>
        This clinic portal uses cookies to remember your username, UI theme, and last opened module. Session cookies keep you signed in securely.
    </div>
    <button type="button" class="btn btn-primary btn-sm" id="acceptCookiesBtn">Accept cookies</button>
</div>

<script>
    window.SDC_COOKIE_THEME = "<%= AppConfig.COOKIE_THEME %>";
    window.SDC_COOKIE_CONSENT = "<%= AppConfig.COOKIE_CONSENT %>";
</script>
<script src="<%= request.getContextPath() %>/assets/js/main.js"></script>
</body>
</html>
