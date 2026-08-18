<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.sunrisedental.config.AppConfig" %>
<% request.setAttribute("pageTitle", "Staff User Guide & Help Manual"); %>
<jsp:include page="header.jsp" />
<jsp:include page="sidebar.jsp" />

<div class="app-main">
    <jsp:include page="topbar.jsp" />

    <main class="content-body">
        <jsp:include page="alerts.jsp" />

        <div style="margin-bottom: 24px;">
            <h2 style="font-size: 1.35rem; font-weight: 800; color: var(--primary-900);">📖 Clinic Operations & Staff User Manual</h2>
            <p style="font-size: 0.88rem; color: var(--text-muted);">Step-by-step guidance and standard operating procedures for Sunrise Dental Clinic staff.</p>
        </div>

        <!-- Role-Based Guidance Cards -->
        <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(320px, 1fr)); gap: 24px; margin-bottom: 30px;">
            
            <!-- 1. Receptionist Guide -->
            <div class="card">
                <div class="card-header">
                    <div class="card-title">
                        <span>👩‍💼 1. Receptionist Workflow</span>
                    </div>
                </div>
                <div class="card-body" style="font-size: 0.9rem; line-height: 1.6;">
                    <ol style="padding-left: 20px;">
                        <li style="margin-bottom: 8px;">
                            <strong>Registering a New Patient:</strong> Navigate to <em>New Patient</em>, fill in mandatory details (Full Name, Contact Phone, DOB, Address, Emergency Contact, and Allergies).
                        </li>
                        <li style="margin-bottom: 8px;">
                            <strong>Booking an Appointment:</strong> Select patient, choose the dentist, treatment type, and date. The system's <em>Conflict Prevention Engine</em> will automatically disable already-booked slots. Select an available time slot and click <em>Confirm & Schedule</em>.
                        </li>
                        <li style="margin-bottom: 8px;">
                            <strong>Searching Appointments:</strong> Go to <em>Search Appointments</em>, type the unique Appointment Number (e.g. <code>APT-20260817-0001</code>) or patient name/phone to pull up complete information.
                        </li>
                        <li>
                            <strong>Canceling:</strong> In the appointment view, authorized staff can cancel appointments with one click.
                        </li>
                    </ol>
                </div>
            </div>

            <!-- 2. Doctor Guide -->
            <div class="card">
                <div class="card-header">
                    <div class="card-title">
                        <span>🩺 2. Dentist / Doctor Workflow</span>
                    </div>
                </div>
                <div class="card-body" style="font-size: 0.9rem; line-height: 1.6;">
                    <ol style="padding-left: 20px;">
                        <li style="margin-bottom: 8px;">
                            <strong>Viewing Assigned Queue:</strong> Navigate to <em>Doctor's Queue</em> to see your scheduled patients for the day in chronological order.
                        </li>
                        <li style="margin-bottom: 8px;">
                            <strong>Clinical Dental Records:</strong> Click <em>Treat Patient</em>. Enter the specific tooth numbers involved (FDI notation, e.g. <code>UR1 (11)</code>, <code>LL6 (36)</code>).
                        </li>
                        <li style="margin-bottom: 8px;">
                            <strong>Prescriptions & Status Updates:</strong> Record clinical diagnosis notes, medications/dosages, and transition status to <em>In-Treatment</em> or <em>Completed</em>.
                        </li>
                        <li>
                            Once marked <strong>Completed</strong>, the patient is immediately pushed to the Cashier Billing Desk.
                        </li>
                    </ol>
                </div>
            </div>

            <!-- 3. Cashier Guide -->
            <div class="card">
                <div class="card-header">
                    <div class="card-title">
                        <span>💳 3. Cashier & Billing Desk</span>
                    </div>
                </div>
                <div class="card-body" style="font-size: 0.9rem; line-height: 1.6;">
                    <ol style="padding-left: 20px;">
                        <li style="margin-bottom: 8px;">
                            <strong>Billing Queue:</strong> Completed visits show in the <em>Billing Queue</em> with pre-calculated Consultation Fee and Treatment Costs.
                        </li>
                        <li style="margin-bottom: 8px;">
                            <strong>Applying Discount Strategies:</strong> Choose policy (<em>Standard</em> 0%, <em>Senior Citizen</em> 10%, <em>Insurance</em> 15%, <em>Loyalty</em> 5%). The system calculates discounts and taxes live.
                        </li>
                        <li style="margin-bottom: 8px;">
                            <strong>Receiving Payments:</strong> Settle payment via Cash, Credit/Debit Card, Bank Transfer, or Insurance and issue receipt numbers.
                        </li>
                        <li>
                            <strong>Printing Invoices:</strong> Click <em>Print Invoice / Receipt</em> to generate a clean branded receipt.
                        </li>
                    </ol>
                </div>
            </div>

            <!-- 4. Administrator Guide -->
            <div class="card">
                <div class="card-header">
                    <div class="card-title">
                        <span>👤 4. Administrator & Director</span>
                    </div>
                </div>
                <div class="card-body" style="font-size: 0.9rem; line-height: 1.6;">
                    <ol style="padding-left: 20px;">
                        <li style="margin-bottom: 8px;">
                            <strong>Staff User Management:</strong> Create new staff logins, assign role permissions (Admin, Receptionist, Doctor, Cashier), reset forgotten passwords, or disable accounts.
                        </li>
                        <li style="margin-bottom: 8px;">
                            <strong>Doctor & Surgery Setup:</strong> Allocate surgery rooms, configure consultation fees, and schedule available days.
                        </li>
                        <li style="margin-bottom: 8px;">
                            <strong>Treatment Pricing:</strong> Maintain the dental service catalog, categories, and standard pricing.
                        </li>
                        <li>
                            <strong>Executive Decision Reports:</strong> View monthly revenue streams, patient volume trends, and top dental procedures.
                        </li>
                    </ol>
                </div>
            </div>

        </div>

        <!-- System Exit / Logout Section -->
        <div class="card">
            <div class="card-header">
                <div class="card-title">
                    <span>🚪 5. Safe System Exit (Logout)</span>
                </div>
            </div>
            <div class="card-body">
                <p style="font-size: 0.92rem; color: var(--text-secondary); line-height: 1.5; margin-bottom: 14px;">
                    To protect patient confidentiality and clinic security, always safely log out of your session whenever stepping away from the computer terminal. Click the <strong>Exit / Logout</strong> button in the bottom left corner of the sidebar or top navigation.
                </p>
                <a href="<%= request.getContextPath() %>/login.jsp?msg=logged_out" class="btn btn-danger btn-sm">
                    🚪 Safely Exit / Log Out Now
                </a>
            </div>
        </div>

    </main>

<jsp:include page="footer.jsp" />
