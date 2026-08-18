package com.sunrisedental.config;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.util.logging.Logger;

/**
 * AppContextListener
 * Listens for application startup and verifies database connection and initial schema/seed records.
 */
@WebListener
public class AppContextListener implements ServletContextListener {

    private static final Logger LOGGER = Logger.getLogger(AppContextListener.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        LOGGER.info("==================================================");
        LOGGER.info("Starting Sunrise Dental Clinic Management System");
        LOGGER.info("==================================================");

        // Auto-initialize DB tables and seed records if needed
        try {
            DBInitializer.initializeDatabase();
        } catch (Exception e) {
            LOGGER.warning("Database startup check note: " + e.getMessage());
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        LOGGER.info("Shutting down Sunrise Dental Clinic Management System");
    }
}
