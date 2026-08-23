package com.sunrisedental.config;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DBInitializer
 * Verifies and auto-initializes the database tables and initial seed data on application startup.
 */
public class DBInitializer {

    private static final Logger LOGGER = Logger.getLogger(DBInitializer.class.getName());

    public static void initializeDatabase() {
        LOGGER.info("Starting Sunrise Dental Clinic Database Verification & Initialization...");
        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {

            // Check if roles table exists
            boolean tablesExist = false;
            try (ResultSet rs = stmt.executeQuery("SHOW TABLES LIKE 'users'")) {
                if (rs.next()) {
                    tablesExist = true;
                }
            }

            if (!tablesExist) {
                LOGGER.info("Tables not found. Creating schema and seeding initial clinic data...");
                executeSchemaCreation(stmt);
                LOGGER.info("Database schema and seed data created successfully!");
            } else {
                LOGGER.info("Database schema already exists. Verifying required records...");
                seedDefaultUsersIfMissing(stmt);
            }
            ensureEmailOutboxTable(stmt);

        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Automatic database initialization encountered an issue (MySQL may need manual schema run): " + e.getMessage());
        }
    }

    private static void ensureEmailOutboxTable(Statement stmt) throws SQLException {
        stmt.executeUpdate(
            "CREATE TABLE IF NOT EXISTS email_outbox (" +
            "  email_id INT AUTO_INCREMENT PRIMARY KEY," +
            "  recipient VARCHAR(150) NOT NULL," +
            "  subject VARCHAR(255) NOT NULL," +
            "  body TEXT," +
            "  event_type VARCHAR(50) NOT NULL," +
            "  delivery_status VARCHAR(30) DEFAULT 'QUEUED'," +
            "  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            ") ENGINE=InnoDB;"
        );
    }

    private static void executeSchemaCreation(Statement stmt) throws SQLException {
        // 1. Roles
        stmt.executeUpdate(
            "CREATE TABLE IF NOT EXISTS roles (" +
            "  role_id INT AUTO_INCREMENT PRIMARY KEY," +
            "  role_name VARCHAR(50) NOT NULL UNIQUE," +
            "  description VARCHAR(255)," +
            "  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            ") ENGINE=InnoDB;"
        );

        // 2. Users
        stmt.executeUpdate(
            "CREATE TABLE IF NOT EXISTS users (" +
            "  user_id INT AUTO_INCREMENT PRIMARY KEY," +
            "  username VARCHAR(50) NOT NULL UNIQUE," +
            "  password_hash VARCHAR(255) NOT NULL," +
            "  salt VARCHAR(64) NOT NULL," +
            "  full_name VARCHAR(100) NOT NULL," +
            "  email VARCHAR(100) NOT NULL UNIQUE," +
            "  phone VARCHAR(20) NOT NULL," +
            "  role_id INT NOT NULL," +
            "  is_active BOOLEAN DEFAULT TRUE," +
            "  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
            "  FOREIGN KEY (role_id) REFERENCES roles(role_id)" +
            ") ENGINE=InnoDB;"
        );

        // 3. Doctors
        stmt.executeUpdate(
            "CREATE TABLE IF NOT EXISTS doctors (" +
            "  doctor_id INT AUTO_INCREMENT PRIMARY KEY," +
            "  user_id INT NOT NULL UNIQUE," +
            "  specialization VARCHAR(100) NOT NULL," +
            "  license_number VARCHAR(50) NOT NULL UNIQUE," +
            "  consultation_fee DECIMAL(10, 2) NOT NULL DEFAULT 1500.00," +
            "  room_number VARCHAR(20) NOT NULL," +
            "  available_days VARCHAR(100) DEFAULT 'Monday,Tuesday,Wednesday,Thursday,Friday,Saturday'," +
            "  start_time TIME DEFAULT '08:30:00'," +
            "  end_time TIME DEFAULT '17:30:00'," +
            "  slot_duration_mins INT DEFAULT 30," +
            "  is_active BOOLEAN DEFAULT TRUE," +
            "  FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE" +
            ") ENGINE=InnoDB;"
        );

        // 4. Patients
        stmt.executeUpdate(
            "CREATE TABLE IF NOT EXISTS patients (" +
            "  patient_id INT AUTO_INCREMENT PRIMARY KEY," +
            "  patient_code VARCHAR(30) NOT NULL UNIQUE," +
            "  full_name VARCHAR(120) NOT NULL," +
            "  nic_passport VARCHAR(30)," +
            "  dob DATE NOT NULL," +
            "  gender ENUM('Male', 'Female', 'Other') NOT NULL," +
            "  phone VARCHAR(20) NOT NULL," +
            "  email VARCHAR(100)," +
            "  address VARCHAR(255) NOT NULL," +
            "  emergency_contact VARCHAR(100)," +
            "  medical_history TEXT," +
            "  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            ") ENGINE=InnoDB;"
        );

        // 5. Treatments
        stmt.executeUpdate(
            "CREATE TABLE IF NOT EXISTS treatments (" +
            "  treatment_id INT AUTO_INCREMENT PRIMARY KEY," +
            "  treatment_code VARCHAR(30) NOT NULL UNIQUE," +
            "  treatment_name VARCHAR(120) NOT NULL," +
            "  category VARCHAR(60) NOT NULL," +
            "  standard_cost DECIMAL(10, 2) NOT NULL," +
            "  estimated_duration_mins INT DEFAULT 30," +
            "  description TEXT," +
            "  is_active BOOLEAN DEFAULT TRUE" +
            ") ENGINE=InnoDB;"
        );

        // 6. Appointments
        stmt.executeUpdate(
            "CREATE TABLE IF NOT EXISTS appointments (" +
            "  appointment_id INT AUTO_INCREMENT PRIMARY KEY," +
            "  appointment_number VARCHAR(40) NOT NULL UNIQUE," +
            "  patient_id INT NOT NULL," +
            "  doctor_id INT NOT NULL," +
            "  treatment_id INT NOT NULL," +
            "  appointment_date DATE NOT NULL," +
            "  appointment_time TIME NOT NULL," +
            "  status ENUM('Scheduled', 'Confirmed', 'In-Treatment', 'Completed', 'Cancelled', 'No-Show') DEFAULT 'Scheduled'," +
            "  clinical_notes TEXT," +
            "  tooth_numbers VARCHAR(50)," +
            "  prescription TEXT," +
            "  created_by INT," +
            "  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
            "  FOREIGN KEY (patient_id) REFERENCES patients(patient_id)," +
            "  FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id)," +
            "  FOREIGN KEY (treatment_id) REFERENCES treatments(treatment_id)" +
            ") ENGINE=InnoDB;"
        );

        // 7. Bills
        stmt.executeUpdate(
            "CREATE TABLE IF NOT EXISTS bills (" +
            "  bill_id INT AUTO_INCREMENT PRIMARY KEY," +
            "  invoice_number VARCHAR(40) NOT NULL UNIQUE," +
            "  appointment_id INT NOT NULL UNIQUE," +
            "  patient_id INT NOT NULL," +
            "  consultation_fee DECIMAL(10, 2) NOT NULL DEFAULT 0.00," +
            "  treatment_cost DECIMAL(10, 2) NOT NULL DEFAULT 0.00," +
            "  additional_charges DECIMAL(10, 2) NOT NULL DEFAULT 0.00," +
            "  discount_type VARCHAR(50) DEFAULT 'Standard'," +
            "  discount_percentage DECIMAL(5, 2) DEFAULT 0.00," +
            "  discount_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00," +
            "  tax_percentage DECIMAL(5, 2) DEFAULT 0.00," +
            "  tax_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00," +
            "  total_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00," +
            "  paid_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00," +
            "  balance_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00," +
            "  payment_status ENUM('Unpaid', 'Partially Paid', 'Paid', 'Refunded') DEFAULT 'Unpaid'," +
            "  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
            "  FOREIGN KEY (appointment_id) REFERENCES appointments(appointment_id)," +
            "  FOREIGN KEY (patient_id) REFERENCES patients(patient_id)" +
            ") ENGINE=InnoDB;"
        );

        // 8. Payments
        stmt.executeUpdate(
            "CREATE TABLE IF NOT EXISTS payments (" +
            "  payment_id INT AUTO_INCREMENT PRIMARY KEY," +
            "  bill_id INT NOT NULL," +
            "  receipt_number VARCHAR(40) NOT NULL UNIQUE," +
            "  payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "  amount DECIMAL(10, 2) NOT NULL," +
            "  payment_method ENUM('Cash', 'Credit Card', 'Debit Card', 'Bank Transfer', 'Insurance') NOT NULL," +
            "  cashier_id INT," +
            "  transaction_reference VARCHAR(100)," +
            "  remarks VARCHAR(255)," +
            "  FOREIGN KEY (bill_id) REFERENCES bills(bill_id) ON DELETE CASCADE" +
            ") ENGINE=InnoDB;"
        );

        // 9. Audit Logs
        stmt.executeUpdate(
            "CREATE TABLE IF NOT EXISTS audit_logs (" +
            "  log_id INT AUTO_INCREMENT PRIMARY KEY," +
            "  user_id INT," +
            "  action VARCHAR(80) NOT NULL," +
            "  entity_name VARCHAR(60) NOT NULL," +
            "  entity_id VARCHAR(50)," +
            "  details TEXT," +
            "  ip_address VARCHAR(45)," +
            "  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            ") ENGINE=InnoDB;"
        );

        seedDefaultUsersIfMissing(stmt);
    }

    private static void seedDefaultUsersIfMissing(Statement stmt) {
        try {
            // Seed Roles
            stmt.executeUpdate("INSERT IGNORE INTO roles (role_id, role_name, description) VALUES " +
                "(1, 'ADMIN', 'System Administrator with full access'), " +
                "(2, 'RECEPTIONIST', 'Front desk receptionist for booking and patient records'), " +
                "(3, 'DOCTOR', 'Dentist / Dental surgeon'), " +
                "(4, 'CASHIER', 'Billing and payment officer');");

            // Seed Users (Default password 'Admin@123' with salt 'SDC_SALT_2026')
            String hash = "047e927bd63bd8ad5575a4eb79161f804188c146eb3aaaffe3576b102b7e37ee";
            String salt = "SDC_SALT_2026";

            stmt.executeUpdate("INSERT IGNORE INTO users (user_id, username, password_hash, salt, full_name, email, phone, role_id, is_active) VALUES " +
                "(1, 'admin', '" + hash + "', '" + salt + "', 'Dr. Nihal Perera (Director)', 'admin@sunrisedental.lk', '+94 11 258 9631', 1, TRUE), " +
                "(2, 'reception', '" + hash + "', '" + salt + "', 'Sanduni Fernando', 'reception@sunrisedental.lk', '+94 77 123 4567', 2, TRUE), " +
                "(3, 'dr_kamal', '" + hash + "', '" + salt + "', 'Dr. Kamal Wickramasinghe (BDS, MS)', 'kamal.w@sunrisedental.lk', '+94 71 456 7890', 3, TRUE), " +
                "(4, 'dr_anusha', '" + hash + "', '" + salt + "', 'Dr. Anusha Senaratne (Orthodontist)', 'anusha.s@sunrisedental.lk', '+94 72 345 6789', 3, TRUE), " +
                "(5, 'dr_rohan', '" + hash + "', '" + salt + "', 'Dr. Rohan Jayasuriya (Endodontist)', 'rohan.j@sunrisedental.lk', '+94 76 987 6543', 3, TRUE), " +
                "(6, 'cashier', '" + hash + "', '" + salt + "', 'Nuwan Jayawardena', 'cashier@sunrisedental.lk', '+94 75 555 1234', 4, TRUE);");

            // Seed Doctors
            stmt.executeUpdate("INSERT IGNORE INTO doctors (doctor_id, user_id, specialization, license_number, consultation_fee, room_number, available_days) VALUES " +
                "(1, 3, 'General Dental Surgeon & Implantology', 'SLMC-DEN-4521', 2000.00, 'Room 01', 'Monday,Tuesday,Wednesday,Thursday,Friday,Saturday'), " +
                "(2, 4, 'Consultant Orthodontist (Braces)', 'SLMC-DEN-3890', 2500.00, 'Room 02', 'Monday,Wednesday,Friday,Saturday'), " +
                "(3, 5, 'Consultant Endodontist (Root Canal)', 'SLMC-DEN-5120', 2200.00, 'Room 03', 'Tuesday,Thursday,Saturday,Sunday');");

            // Seed Treatments
            stmt.executeUpdate("INSERT IGNORE INTO treatments (treatment_id, treatment_code, treatment_name, category, standard_cost, estimated_duration_mins, description) VALUES " +
                "(1, 'TRT-001', 'Dental Checkup & Consultation', 'Diagnostic', 1500.00, 20, 'Comprehensive oral examination & treatment planning'), " +
                "(2, 'TRT-002', 'Scaling & Polishing (Full Cleaning)', 'Preventive', 4500.00, 30, 'Ultrasonic scaling and polishing'), " +
                "(3, 'TRT-003', 'Composite Dental Filling (Per Tooth)', 'Restorative', 3500.00, 30, 'Light-cure aesthetic composite filling'), " +
                "(4, 'TRT-004', 'Root Canal Treatment (RCT - Anterior)', 'Endodontics', 15000.00, 45, 'Complete anterior root canal therapy'), " +
                "(5, 'TRT-005', 'Root Canal Treatment (RCT - Molar)', 'Endodontics', 22000.00, 60, 'Posterior molar root canal therapy'), " +
                "(6, 'TRT-006', 'Simple Tooth Extraction', 'Oral Surgery', 3000.00, 25, 'Painless tooth extraction with local anesthesia'), " +
                "(7, 'TRT-007', 'Surgical Wisdom Tooth Removal', 'Oral Surgery', 18000.00, 60, 'Surgical impaction removal'), " +
                "(8, 'TRT-008', 'Teeth Whitening (Laser)', 'Cosmetic', 28000.00, 45, 'In-office laser teeth whitening session'), " +
                "(9, 'TRT-009', 'Orthodontic Braces Setup', 'Orthodontics', 45000.00, 60, 'Orthodontic bracket bonding and archwire setup'), " +
                "(10, 'TRT-010', 'Ceramic Crown / Cap', 'Prosthodontics', 25000.00, 45, 'High-strength aesthetic ceramic crown');");

            // Seed Sample Patients
            stmt.executeUpdate("INSERT IGNORE INTO patients (patient_id, patient_code, full_name, nic_passport, dob, gender, phone, email, address, emergency_contact, medical_history) VALUES " +
                "(1, 'PT-2026-001', 'Kasun Mendis', '199214502341', '1992-05-14', 'Male', '0778901234', 'kasun.m@gmail.com', 'No. 45, Galle Road, Colombo 03', 'Wife: 0778901235', 'No known allergies. Mild asthma.'), " +
                "(2, 'PT-2026-002', 'Dilani Jayasundara', '198574109823', '1985-11-20', 'Female', '0712345678', 'dilani.j@yahoo.com', 'No. 12/A, Havelock Road, Colombo 05', 'Husband: 0712345679', 'Penicillin allergy. Diabetic on Metformin.'), " +
                "(3, 'PT-2026-003', 'Ashan Silva', '200108901234', '2001-03-29', 'Male', '0761122334', 'ashan.silva@outlook.com', 'No. 88, Kandy Road, Kelaniya', 'Father: 0761122335', 'Hypertension under medication.'), " +
                "(4, 'PT-2026-004', 'Mary Angela Perera', '195863201456', '1958-08-12', 'Female', '0759988776', 'mary.perera@gmail.com', 'No. 102, Baseline Road, Colombo 09', 'Son: 0759988777', 'Senior citizen. Aspirin therapy.');");

            // Seed Sample Appointments
            stmt.executeUpdate("INSERT IGNORE INTO appointments (appointment_id, appointment_number, patient_id, doctor_id, treatment_id, appointment_date, appointment_time, status, clinical_notes, tooth_numbers, prescription, created_by) VALUES " +
                "(1, 'APT-20260817-0001', 1, 1, 2, CURDATE(), '09:30:00', 'Completed', 'Scaling completed. Gingival health good.', 'All', 'Chlorhexidine mouthwash 0.2%', 2), " +
                "(2, 'APT-20260817-0002', 2, 3, 4, CURDATE(), '10:30:00', 'In-Treatment', 'Root canal stage 1 done.', 'UR1 (11)', 'Amoxicillin 500mg, Paracetamol 1g', 2), " +
                "(3, 'APT-20260817-0003', 3, 2, 9, CURDATE(), '11:30:00', 'Confirmed', 'Orthodontic bonding scheduled.', 'Upper & Lower', NULL, 2), " +
                "(4, 'APT-20260818-0004', 4, 1, 3, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '14:00:00', 'Scheduled', 'Tooth sensitivity.', 'LL6 (36)', NULL, 2);");

            // Seed Sample Bill & Payment
            stmt.executeUpdate("INSERT IGNORE INTO bills (bill_id, invoice_number, appointment_id, patient_id, consultation_fee, treatment_cost, additional_charges, discount_type, discount_percentage, discount_amount, tax_percentage, tax_amount, total_amount, paid_amount, balance_amount, payment_status, created_at) VALUES " +
                "(1, 'INV-20260817-0001', 1, 1, 2000.00, 4500.00, 0.00, 'Standard', 0.00, 0.00, 0.00, 0.00, 6500.00, 6500.00, 0.00, 'Paid', NOW());");

            stmt.executeUpdate("INSERT IGNORE INTO payments (payment_id, bill_id, receipt_number, payment_date, amount, payment_method, cashier_id, transaction_reference, remarks) VALUES " +
                "(1, 1, 'REC-20260817-0001', NOW(), 6500.00, 'Cash', 6, 'CASH-TXN-101', 'Settled in full at front cashier desk');");

            LOGGER.info("Default seed users and demo clinic records verified/seeded successfully.");
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Seeding records: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        System.out.println("Running DBInitializer standalone...");
        initializeDatabase();
        System.out.println("DBInitializer finished successfully!");
    }
}
