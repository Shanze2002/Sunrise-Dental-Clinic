package com.sunrisedental.config;

import com.sunrisedental.service.notification.EmailNotificationObserver;
import com.sunrisedental.service.notification.NotificationService;
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

        NotificationService.getInstance().register(new EmailNotificationObserver());
        LOGGER.info("Email notification observer registered (Observer Pattern)");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        LOGGER.info("Shutting down Sunrise Dental Clinic Management System");
    }
}
