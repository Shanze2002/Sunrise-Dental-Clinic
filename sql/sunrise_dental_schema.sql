-- =====================================================================
-- SUNRISE DENTAL CLINIC - COLOMBO
-- Enterprise Database Schema (MySQL 8.0+)
-- Features: Normalized Tables, Indexes, Foreign Keys, Stored Procedures,
--           Triggers, Views, and Sample Seed Data
-- =====================================================================

CREATE DATABASE IF NOT EXISTS sunrise_dental_db;
USE sunrise_dental_db;

-- 1. DROP EXISTING OBJECTS IN SAFE ORDER (IF RE-INITIALIZING)
DROP TRIGGER IF EXISTS trg_audit_appointment_insert;
DROP TRIGGER IF EXISTS trg_audit_appointment_update;
DROP TRIGGER IF EXISTS trg_update_bill_status_on_payment;
DROP PROCEDURE IF EXISTS sp_BookAppointment;
DROP PROCEDURE IF EXISTS sp_GetMonthlyFinancialReport;
DROP PROCEDURE IF EXISTS sp_GetDoctorDailySchedule;
DROP VIEW IF EXISTS vw_DailyDoctorSchedule;
DROP VIEW IF EXISTS vw_AppointmentBillingSummary;
DROP VIEW IF EXISTS vw_MonthlyRevenueSummary;

DROP TABLE IF EXISTS payments;
DROP TABLE IF EXISTS bills;
DROP TABLE IF EXISTS appointments;
DROP TABLE IF EXISTS treatments;
DROP TABLE IF EXISTS patients;
DROP TABLE IF EXISTS doctors;
DROP TABLE IF EXISTS audit_logs;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS roles;

-- =====================================================================
-- 2. CORE TABLES
-- =====================================================================

-- Roles Table
CREATE TABLE roles (
    role_id INT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Users Table (Staff: Admin, Receptionist, Doctor, Cashier)
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    salt VARCHAR(64) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL,
    role_id INT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES roles(role_id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- Doctors / Dentists Table
CREATE TABLE doctors (
    doctor_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    specialization VARCHAR(100) NOT NULL,
    license_number VARCHAR(50) NOT NULL UNIQUE,
    consultation_fee DECIMAL(10, 2) NOT NULL DEFAULT 1500.00,
    room_number VARCHAR(20) NOT NULL,
    available_days VARCHAR(100) DEFAULT 'Monday,Tuesday,Wednesday,Thursday,Friday,Saturday',
    start_time TIME DEFAULT '08:30:00',
    end_time TIME DEFAULT '17:30:00',
    slot_duration_mins INT DEFAULT 30,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Patients Table
CREATE TABLE patients (
    patient_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_code VARCHAR(30) NOT NULL UNIQUE,
    full_name VARCHAR(120) NOT NULL,
    nic_passport VARCHAR(30),
    dob DATE NOT NULL,
    gender ENUM('Male', 'Female', 'Other') NOT NULL,
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(100),
    address VARCHAR(255) NOT NULL,
    emergency_contact VARCHAR(100),
    medical_history TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_patient_phone (phone),
    INDEX idx_patient_nic (nic_passport),
    INDEX idx_patient_name (full_name)
) ENGINE=InnoDB;

-- Treatments / Dental Services Table
CREATE TABLE treatments (
    treatment_id INT AUTO_INCREMENT PRIMARY KEY,
    treatment_code VARCHAR(30) NOT NULL UNIQUE,
    treatment_name VARCHAR(120) NOT NULL,
    category VARCHAR(60) NOT NULL,
    standard_cost DECIMAL(10, 2) NOT NULL,
    estimated_duration_mins INT DEFAULT 30,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE
) ENGINE=InnoDB;

-- Appointments Table
CREATE TABLE appointments (
    appointment_id INT AUTO_INCREMENT PRIMARY KEY,
    appointment_number VARCHAR(40) NOT NULL UNIQUE,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    treatment_id INT NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    status ENUM('Scheduled', 'Confirmed', 'In-Treatment', 'Completed', 'Cancelled', 'No-Show') DEFAULT 'Scheduled',
    clinical_notes TEXT,
    tooth_numbers VARCHAR(50),
    prescription TEXT,
    created_by INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id) ON DELETE RESTRICT,
    FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id) ON DELETE RESTRICT,
    FOREIGN KEY (treatment_id) REFERENCES treatments(treatment_id) ON DELETE RESTRICT,
    FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_app_date_doc (appointment_date, doctor_id, appointment_time),
    INDEX idx_app_number (appointment_number),
    INDEX idx_app_status (status)
) ENGINE=InnoDB;

-- Bills / Invoices Table
CREATE TABLE bills (
    bill_id INT AUTO_INCREMENT PRIMARY KEY,
    invoice_number VARCHAR(40) NOT NULL UNIQUE,
    appointment_id INT NOT NULL UNIQUE,
    patient_id INT NOT NULL,
    consultation_fee DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    treatment_cost DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    additional_charges DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    discount_type VARCHAR(50) DEFAULT 'Standard',
    discount_percentage DECIMAL(5, 2) DEFAULT 0.00,
    discount_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    tax_percentage DECIMAL(5, 2) DEFAULT 0.00,
    tax_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    total_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    paid_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    balance_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    payment_status ENUM('Unpaid', 'Partially Paid', 'Paid', 'Refunded') DEFAULT 'Unpaid',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (appointment_id) REFERENCES appointments(appointment_id) ON DELETE RESTRICT,
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- Payments Table
CREATE TABLE payments (
    payment_id INT AUTO_INCREMENT PRIMARY KEY,
    bill_id INT NOT NULL,
    receipt_number VARCHAR(40) NOT NULL UNIQUE,
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    amount DECIMAL(10, 2) NOT NULL,
    payment_method ENUM('Cash', 'Credit Card', 'Debit Card', 'Bank Transfer', 'Insurance') NOT NULL,
    cashier_id INT,
    transaction_reference VARCHAR(100),
    remarks VARCHAR(255),
    FOREIGN KEY (bill_id) REFERENCES bills(bill_id) ON DELETE CASCADE,
    FOREIGN KEY (cashier_id) REFERENCES users(user_id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- Audit Logs Table
CREATE TABLE audit_logs (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    action VARCHAR(80) NOT NULL,
    entity_name VARCHAR(60) NOT NULL,
    entity_id VARCHAR(50),
    details TEXT,
    ip_address VARCHAR(45),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- =====================================================================
-- 3. DATABASE VIEWS
-- =====================================================================

-- View for Daily Doctor Schedule & Queue
CREATE OR REPLACE VIEW vw_DailyDoctorSchedule AS
SELECT 
    a.appointment_id,
    a.appointment_number,
    a.appointment_date,
    a.appointment_time,
    a.status AS appointment_status,
    a.tooth_numbers,
    a.clinical_notes,
    p.patient_id,
    p.patient_code,
    p.full_name AS patient_name,
    p.phone AS patient_phone,
    p.dob AS patient_dob,
    p.gender AS patient_gender,
    d.doctor_id,
    u.full_name AS doctor_name,
    d.room_number,
    d.consultation_fee,
    t.treatment_id,
    t.treatment_name,
    t.standard_cost AS treatment_cost
FROM appointments a
JOIN patients p ON a.patient_id = p.patient_id
JOIN doctors d ON a.doctor_id = d.doctor_id
JOIN users u ON d.user_id = u.user_id
JOIN treatments t ON a.treatment_id = t.treatment_id;

-- View for Appointment & Billing Summary
CREATE OR REPLACE VIEW vw_AppointmentBillingSummary AS
SELECT 
    a.appointment_id,
    a.appointment_number,
    a.appointment_date,
    a.appointment_time,
    a.status AS appointment_status,
    p.patient_id,
    p.patient_code,
    p.full_name AS patient_name,
    p.phone AS patient_phone,
    u.full_name AS doctor_name,
    t.treatment_name,
    b.bill_id,
    b.invoice_number,
    COALESCE(b.total_amount, (d.consultation_fee + t.standard_cost)) AS estimated_total,
    COALESCE(b.paid_amount, 0.00) AS paid_amount,
    COALESCE(b.payment_status, 'Unbilled') AS billing_status
FROM appointments a
JOIN patients p ON a.patient_id = p.patient_id
JOIN doctors d ON a.doctor_id = d.doctor_id
JOIN users u ON d.user_id = u.user_id
JOIN treatments t ON a.treatment_id = t.treatment_id
LEFT JOIN bills b ON a.appointment_id = b.appointment_id;

-- View for Monthly Revenue Summary
CREATE OR REPLACE VIEW vw_MonthlyRevenueSummary AS
SELECT 
    DATE_FORMAT(b.created_at, '%Y-%m') AS month_year,
    COUNT(DISTINCT b.bill_id) AS total_bills,
    COUNT(DISTINCT b.patient_id) AS unique_patients,
    SUM(b.consultation_fee) AS total_consultation,
    SUM(b.treatment_cost) AS total_treatment_income,
    SUM(b.discount_amount) AS total_discounts,
    SUM(b.total_amount) AS net_revenue,
    SUM(b.paid_amount) AS collected_revenue
FROM bills b
GROUP BY DATE_FORMAT(b.created_at, '%Y-%m')
ORDER BY month_year DESC;

-- =====================================================================
-- 4. TRIGGERS
-- =====================================================================

DELIMITER $$

-- Trigger: Audit new appointments
CREATE TRIGGER trg_audit_appointment_insert
AFTER INSERT ON appointments
FOR EACH ROW
BEGIN
    INSERT INTO audit_logs (user_id, action, entity_name, entity_id, details)
    VALUES (NEW.created_by, 'CREATE_APPOINTMENT', 'appointments', NEW.appointment_number, 
            CONCAT('Booked appointment on ', NEW.appointment_date, ' at ', NEW.appointment_time, ' for Patient ID ', NEW.patient_id));
END$$

-- Trigger: Audit appointment status change
CREATE TRIGGER trg_audit_appointment_update
AFTER UPDATE ON appointments
FOR EACH ROW
BEGIN
    IF OLD.status <> NEW.status THEN
        INSERT INTO audit_logs (user_id, action, entity_name, entity_id, details)
        VALUES (NEW.created_by, 'UPDATE_APPOINTMENT_STATUS', 'appointments', NEW.appointment_number, 
                CONCAT('Status changed from ', OLD.status, ' to ', NEW.status));
    END IF;
END$$

-- Trigger: Update Bill Status whenever a payment is registered
CREATE TRIGGER trg_update_bill_status_on_payment
AFTER INSERT ON payments
FOR EACH ROW
BEGIN
    DECLARE v_total DECIMAL(10,2);
    DECLARE v_already_paid DECIMAL(10,2);
    
    SELECT total_amount INTO v_total FROM bills WHERE bill_id = NEW.bill_id;
    SELECT COALESCE(SUM(amount), 0) INTO v_already_paid FROM payments WHERE bill_id = NEW.bill_id;
    
    IF v_already_paid >= v_total THEN
        UPDATE bills 
        SET paid_amount = v_already_paid,
            balance_amount = 0.00,
            payment_status = 'Paid'
        WHERE bill_id = NEW.bill_id;
    ELSEIF v_already_paid > 0 THEN
        UPDATE bills 
        SET paid_amount = v_already_paid,
            balance_amount = (v_total - v_already_paid),
            payment_status = 'Partially Paid'
        WHERE bill_id = NEW.bill_id;
    END IF;
END$$

DELIMITER ;

-- =====================================================================
-- 5. STORED PROCEDURES
-- =====================================================================

DELIMITER $$

-- Stored Procedure: Book Appointment with strict conflict checking
CREATE PROCEDURE sp_BookAppointment(
    IN p_patient_id INT,
    IN p_doctor_id INT,
    IN p_treatment_id INT,
    IN p_appointment_date DATE,
    IN p_appointment_time TIME,
    IN p_notes TEXT,
    IN p_created_by INT,
    OUT p_out_appointment_number VARCHAR(40),
    OUT p_out_status VARCHAR(20),
    OUT p_out_message VARCHAR(255)
)
BEGIN
    DECLARE v_conflict_count INT DEFAULT 0;
    DECLARE v_app_num VARCHAR(40);
    DECLARE v_next_id INT DEFAULT 1;

    -- Check if doctor already has a booked appointment at that exact slot
    SELECT COUNT(*) INTO v_conflict_count
    FROM appointments
    WHERE doctor_id = p_doctor_id 
      AND appointment_date = p_appointment_date 
      AND appointment_time = p_appointment_time
      AND status NOT IN ('Cancelled', 'No-Show');

    IF v_conflict_count > 0 THEN
        SET p_out_status = 'ERROR';
        SET p_out_message = 'Doctor is already booked for the selected date and time slot.';
        SET p_out_appointment_number = NULL;
    ELSE
        -- Generate unique appointment number: APT-YYYYMMDD-XXXX
        SELECT COALESCE(MAX(appointment_id), 0) + 1 INTO v_next_id FROM appointments;
        SET v_app_num = CONCAT('APT-', DATE_FORMAT(p_appointment_date, '%Y%m%d'), '-', LPAD(v_next_id, 4, '0'));

        INSERT INTO appointments (
            appointment_number, patient_id, doctor_id, treatment_id, 
            appointment_date, appointment_time, status, clinical_notes, created_by
        ) VALUES (
            v_app_num, p_patient_id, p_doctor_id, p_treatment_id,
            p_appointment_date, p_appointment_time, 'Scheduled', p_notes, p_created_by
        );

        SET p_out_status = 'SUCCESS';
        SET p_out_message = 'Appointment successfully scheduled.';
        SET p_out_appointment_number = v_app_num;
    END IF;
END$$

-- Stored Procedure: Monthly Financial & Treatment Report
CREATE PROCEDURE sp_GetMonthlyFinancialReport(
    IN p_year_month VARCHAR(7) -- e.g. '2026-08'
)
BEGIN
    SELECT 
        DATE_FORMAT(b.created_at, '%Y-%m') AS report_period,
        COUNT(DISTINCT b.bill_id) AS total_invoices,
        COUNT(DISTINCT b.patient_id) AS total_patients_billed,
        COALESCE(SUM(b.consultation_fee), 0) AS gross_consultation_income,
        COALESCE(SUM(b.treatment_cost), 0) AS gross_treatment_income,
        COALESCE(SUM(b.additional_charges), 0) AS gross_additional_charges,
        COALESCE(SUM(b.discount_amount), 0) AS total_discounts_granted,
        COALESCE(SUM(b.tax_amount), 0) AS total_tax_collected,
        COALESCE(SUM(b.total_amount), 0) AS total_net_revenue,
        COALESCE(SUM(b.paid_amount), 0) AS total_cash_collected
    FROM bills b
    WHERE DATE_FORMAT(b.created_at, '%Y-%m') = p_year_month;
END$$

DELIMITER ;

-- =====================================================================
-- 6. SEED INITIAL DATA (ROLES, DEFAULT USERS, DENTISTS, TREATMENTS)
-- Passwords: All default passwords are 'Admin@123', 'Staff@123', etc.
-- Hashes below are SHA-256 with corresponding salts
-- =====================================================================

INSERT INTO roles (role_id, role_name, description) VALUES
(1, 'ADMIN', 'Full access to system, user management, and executive analytics reports'),
(2, 'RECEPTIONIST', 'Patient registration, appointment scheduling, search, and queue management'),
(3, 'DOCTOR', 'View clinical schedule, manage treatment records, tooth chart, and prescriptions'),
(4, 'CASHIER', 'Billing calculation, discount application, payment collection, and receipts');

-- Default Staff Users (Password for all initially: 'Admin@123' -> SHA256 hashed with salt 'SDC_SALT_2026')
-- Hash of ("Admin@123" + "SDC_SALT_2026") = 047e927bd63bd8ad5575a4eb79161f804188c146eb3aaaffe3576b102b7e37ee
INSERT INTO users (user_id, username, password_hash, salt, full_name, email, phone, role_id, is_active) VALUES
(1, 'admin', '047e927bd63bd8ad5575a4eb79161f804188c146eb3aaaffe3576b102b7e37ee', 'SDC_SALT_2026', 'Dr. Nihal Perera (Clinic Director)', 'admin@sunrisedental.lk', '+94 11 258 9631', 1, TRUE),
(2, 'reception', '047e927bd63bd8ad5575a4eb79161f804188c146eb3aaaffe3576b102b7e37ee', 'SDC_SALT_2026', 'Sanduni Fernando', 'reception@sunrisedental.lk', '+94 77 123 4567', 2, TRUE),
(3, 'dr_kamal', '047e927bd63bd8ad5575a4eb79161f804188c146eb3aaaffe3576b102b7e37ee', 'SDC_SALT_2026', 'Dr. Kamal Wickramasinghe (BDS, MS)', 'kamal.w@sunrisedental.lk', '+94 71 456 7890', 3, TRUE),
(4, 'dr_anusha', '047e927bd63bd8ad5575a4eb79161f804188c146eb3aaaffe3576b102b7e37ee', 'SDC_SALT_2026', 'Dr. Anusha Senaratne (Orthodontist)', 'anusha.s@sunrisedental.lk', '+94 72 345 6789', 3, TRUE),
(5, 'dr_rohan', '047e927bd63bd8ad5575a4eb79161f804188c146eb3aaaffe3576b102b7e37ee', 'SDC_SALT_2026', 'Dr. Rohan Jayasuriya (Endodontist)', 'rohan.j@sunrisedental.lk', '+94 76 987 6543', 3, TRUE),
(6, 'cashier', '047e927bd63bd8ad5575a4eb79161f804188c146eb3aaaffe3576b102b7e37ee', 'SDC_SALT_2026', 'Nuwan Jayawardena', 'cashier@sunrisedental.lk', '+94 75 555 1234', 4, TRUE);

-- Doctors Profiles
INSERT INTO doctors (doctor_id, user_id, specialization, license_number, consultation_fee, room_number, available_days, start_time, end_time, slot_duration_mins, is_active) VALUES
(1, 3, 'General Dental Surgeon & Implantology', 'SLMC-DEN-4521', 2000.00, 'Room 01', 'Monday,Tuesday,Wednesday,Thursday,Friday,Saturday', '09:00:00', '17:00:00', 30, TRUE),
(2, 4, 'Consultant Orthodontist (Braces & Aligners)', 'SLMC-DEN-3890', 2500.00, 'Room 02', 'Monday,Wednesday,Friday,Saturday', '10:00:00', '18:00:00', 30, TRUE),
(3, 5, 'Consultant Endodontist (Root Canal Specialist)', 'SLMC-DEN-5120', 2200.00, 'Room 03', 'Tuesday,Thursday,Saturday,Sunday', '08:30:00', '16:30:00', 30, TRUE);

-- Dental Treatments & Services
INSERT INTO treatments (treatment_id, treatment_code, treatment_name, category, standard_cost, estimated_duration_mins, description) VALUES
(1, 'TRT-001', 'Dental Checkup & Consultation', 'Diagnostic', 1500.00, 20, 'Comprehensive oral examination, gum check, and treatment planning'),
(2, 'TRT-002', 'Scaling & Polishing (Full Mouth Cleaning)', 'Preventive', 4500.00, 30, 'Ultrasonic scaling, plaque removal, and fluoride polishing'),
(3, 'TRT-003', 'Composite Dental Filling (Per Tooth)', 'Restorative', 3500.00, 30, 'Tooth-colored aesthetic light-cure composite resin filling'),
(4, 'TRT-004', 'Root Canal Treatment (RCT - Anterior)', 'Endodontics', 15000.00, 45, 'Complete root canal therapy for front teeth with digital X-ray'),
(5, 'TRT-005', 'Root Canal Treatment (RCT - Molar)', 'Endodontics', 22000.00, 60, 'Multi-canal therapy for posterior molar teeth'),
(6, 'TRT-006', 'Simple Tooth Extraction', 'Oral Surgery', 3000.00, 25, 'Painless local anesthetic tooth removal'),
(7, 'TRT-007', 'Surgical Wisdom Tooth Removal', 'Oral Surgery', 18000.00, 60, 'Surgical impaction removal under local anesthesia'),
(8, 'TRT-008', 'Teeth Whitening (Laser/In-Office)', 'Cosmetic', 28000.00, 45, 'Professional LED laser bleaching session for instant brightness'),
(9, 'TRT-009', 'Orthodontic Consultation & Braces Setup', 'Orthodontics', 45000.00, 60, 'Initial orthodontic assessment, study models, and bracket bonding'),
(10, 'TRT-010', 'Ceramic Crown / Porcelain Cap', 'Prosthodontics', 25000.00, 45, 'High-strength aesthetic ceramic crown restoration');

-- Sample Patients
INSERT INTO patients (patient_id, patient_code, full_name, nic_passport, dob, gender, phone, email, address, emergency_contact, medical_history) VALUES
(1, 'PT-2026-001', 'Kasun Mendis', '199214502341', '1992-05-14', 'Male', '0778901234', 'kasun.m@gmail.com', 'No. 45, Galle Road, Colombo 03', 'Wife: 0778901235', 'No known allergies. Mild asthma.'),
(2, 'PT-2026-002', 'Dilani Jayasundara', '198574109823', '1985-11-20', 'Female', '0712345678', 'dilani.j@yahoo.com', 'No. 12/A, Havelock Road, Colombo 05', 'Husband: 0712345679', 'Penicillin allergy. Diabetic on Metformin.'),
(3, 'PT-2026-003', 'Ashan Silva', '200108901234', '2001-03-29', 'Male', '0761122334', 'ashan.silva@outlook.com', 'No. 88, Kandy Road, Kelaniya', 'Father: 0761122335', 'Hypertension under medication.'),
(4, 'PT-2026-004', 'Mary Angela Perera', '195863201456', '1958-08-12', 'Female', '0759988776', 'mary.perera@gmail.com', 'No. 102, Baseline Road, Colombo 09', 'Son: 0759988777', 'Senior citizen. Blood thinner (Aspirin).');

-- Sample Appointments
INSERT INTO appointments (appointment_id, appointment_number, patient_id, doctor_id, treatment_id, appointment_date, appointment_time, status, clinical_notes, tooth_numbers, prescription, created_by) VALUES
(1, 'APT-20260817-0001', 1, 1, 2, '2026-08-17', '09:30:00', 'Completed', 'Scaling completed. Gingival health good. Advised soft brushing.', 'All', 'Chlorhexidine mouthwash 0.2%', 2),
(2, 'APT-20260817-0002', 2, 3, 4, '2026-08-17', '10:30:00', 'In-Treatment', 'Root canal stage 1 completed. Working length measured.', 'UR1 (11)', 'Amoxicillin 500mg tds 5d, Paracetamol 1g prn', 2),
(3, 'APT-20260817-0003', 3, 2, 9, '2026-08-17', '11:30:00', 'Confirmed', 'Orthodontic bonding scheduled.', 'Upper & Lower', NULL, 2),
(4, 'APT-20260818-0004', 4, 1, 3, '2026-08-18', '14:00:00', 'Scheduled', 'Patient reports sensitivity in lower left molar.', 'LL6 (36)', NULL, 2);

-- Sample Bills & Payments
INSERT INTO bills (bill_id, invoice_number, appointment_id, patient_id, consultation_fee, treatment_cost, additional_charges, discount_type, discount_percentage, discount_amount, tax_percentage, tax_amount, total_amount, paid_amount, balance_amount, payment_status, created_at) VALUES
(1, 'INV-20260817-0001', 1, 1, 2000.00, 4500.00, 0.00, 'Standard', 0.00, 0.00, 0.00, 0.00, 6500.00, 6500.00, 0.00, 'Paid', '2026-08-17 10:15:00');

INSERT INTO payments (payment_id, bill_id, receipt_number, payment_date, amount, payment_method, cashier_id, transaction_reference, remarks) VALUES
(1, 1, 'REC-20260817-0001', '2026-08-17 10:20:00', 6500.00, 'Cash', 6, 'CASH-TXN-101', 'Full payment settled at cashier desk');
