package com.sunrisedental.config;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Singleton Pattern: DBConnectionManager
 * Manages MySQL connection pooling with configurable db.properties and intelligent auto-database creation.
 */
public class DBConnectionManager {

    private static final Logger LOGGER = Logger.getLogger(DBConnectionManager.class.getName());
    private static volatile DBConnectionManager instance;

    private String dbUrl = "jdbc:mysql://localhost:3306/sunrise_dental_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";
    private String dbUser = "root";
    private String dbPassword = "Anas@2002";

    private DBConnectionManager() {
        // 1. Load MySQL JDBC Driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            LOGGER.info("MySQL JDBC Driver registered successfully.");
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "MySQL JDBC Driver not found in classpath!", e);
        }

        // 2. Load configurations from db.properties if present
        loadProperties();
    }

    private void loadProperties() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            if (is != null) {
                Properties props = new Properties();
                props.load(is);
                if (props.getProperty("db.url") != null) this.dbUrl = props.getProperty("db.url").trim();
                if (props.getProperty("db.user") != null) this.dbUser = props.getProperty("db.user").trim();
                if (props.getProperty("db.password") != null) this.dbPassword = props.getProperty("db.password").trim();
                LOGGER.info("Loaded db.properties successfully (User: " + this.dbUser + ")");
            }
        } catch (Exception e) {
            LOGGER.warning("Could not read db.properties, using defaults: " + e.getMessage());
        }
    }

    public static DBConnectionManager getInstance() {
        if (instance == null) {
            synchronized (DBConnectionManager.class) {
                if (instance == null) {
                    instance = new DBConnectionManager();
                }
            }
        }
        return instance;
    }

    public synchronized void configure(String url, String user, String password) {
        if (url != null && !url.trim().isEmpty()) this.dbUrl = url.trim();
        if (user != null) this.dbUser = user.trim();
        if (password != null) this.dbPassword = password.trim();
    }

    public Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        } catch (SQLException e) {
            // If sunrise_dental_db does not exist yet, try connecting to root server and creating it
            if (e.getMessage() != null && e.getMessage().contains("Unknown database")) {
                tryCreateDatabase();
                return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            }
            LOGGER.log(Level.SEVERE, "Failed to connect to MySQL: " + e.getMessage() + " (User: " + dbUser + "). Please set your MySQL password in db.properties or DBConnectionManager.");
            throw e;
        }
    }

    private void tryCreateDatabase() {
        String serverUrl = "jdbc:mysql://localhost:3306/?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";
        try (Connection rootConn = DriverManager.getConnection(serverUrl, dbUser, dbPassword);
             Statement stmt = rootConn.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS sunrise_dental_db;");
            LOGGER.info("Database 'sunrise_dental_db' created successfully!");
        } catch (Exception ex) {
            LOGGER.warning("Could not auto-create database: " + ex.getMessage());
        }
    }

    public static void closeQuietly(AutoCloseable... closeables) {
        for (AutoCloseable c : closeables) {
            if (c != null) {
                try {
                    c.close();
                } catch (Exception ignored) {}
            }
        }
    }

    public String getDbUrl() { return dbUrl; }
    public String getDbUser() { return dbUser; }
    public String getDbPassword() { return dbPassword; }
    public void setDbPassword(String dbPassword) { this.dbPassword = dbPassword; }
}
