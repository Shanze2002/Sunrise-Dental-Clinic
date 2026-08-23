/**
 * Sunrise Dental Clinic - Core JavaScript
 */
document.addEventListener('DOMContentLoaded', () => {
    // Auto-dismiss alerts after 5 seconds
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        setTimeout(() => {
            alert.style.opacity = '0';
            alert.style.transition = 'opacity 0.5s ease';
            setTimeout(() => alert.remove(), 500);
        }, 5000);
    });

    // Setup print trigger buttons
    const printBtns = document.querySelectorAll('.btn-print-trigger');
    printBtns.forEach(btn => {
        btn.addEventListener('click', (e) => {
            e.preventDefault();
            window.print();
        });
    });

    // Setup table search filters if present
    const tableSearchInput = document.getElementById('tableSearchInput');
    if (tableSearchInput) {
        tableSearchInput.addEventListener('keyup', function() {
            const val = this.value.toLowerCase();
            const rows = document.querySelectorAll('.data-table tbody tr');
            rows.forEach(row => {
                const text = row.textContent.toLowerCase();
                row.style.display = text.indexOf(val) > -1 ? '' : 'none';
            });
        });
    }

    setupCookieConsent();
    setupThemeToggle();
});

// Modal helpers
function openModal(modalId) {
    const m = document.getElementById(modalId);
    if (m) m.classList.add('active');
}

function closeModal(modalId) {
    const m = document.getElementById(modalId);
    if (m) m.classList.remove('active');
}

function readCookie(name) {
    const parts = ('; ' + document.cookie).split('; ' + name + '=');
    if (parts.length === 2) return parts.pop().split(';').shift();
    return '';
}

function writeCookie(name, value, days) {
    const expires = new Date(Date.now() + days * 24 * 60 * 60 * 1000).toUTCString();
    document.cookie = name + '=' + encodeURIComponent(value) + '; expires=' + expires + '; path=/; SameSite=Lax';
}

function setupCookieConsent() {
    const banner = document.getElementById('cookieConsentBanner');
    const acceptBtn = document.getElementById('acceptCookiesBtn');
    const consentName = window.SDC_COOKIE_CONSENT || 'sdc_cookie_consent';
    if (!banner) return;
    if (!readCookie(consentName)) {
        banner.hidden = false;
    }
    if (acceptBtn) {
        acceptBtn.addEventListener('click', () => {
            writeCookie(consentName, 'accepted', 180);
            banner.hidden = true;
        });
    }
}

function setupThemeToggle() {
    const themeName = window.SDC_COOKIE_THEME || 'sdc_theme';
    document.querySelectorAll('.js-theme-toggle').forEach(btn => {
        btn.addEventListener('click', () => {
            const next = document.body.classList.contains('theme-dark') ? 'light' : 'dark';
            document.body.classList.remove('theme-light', 'theme-dark');
            document.body.classList.add('theme-' + next);
            writeCookie(themeName, next, 90);
        });
    });
}
