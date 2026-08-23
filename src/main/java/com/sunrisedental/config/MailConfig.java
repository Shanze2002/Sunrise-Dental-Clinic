package com.sunrisedental.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * SMTP settings: classpath db.properties, then override from user-home file (admin UI).
 */
public class MailConfig {

    private static final Logger LOGGER = Logger.getLogger(MailConfig.class.getName());
    private static volatile MailConfig instance;
    private static final File OVERRIDE_FILE = new File(System.getProperty("user.home"), "sunrise-dental-mail.properties");

    private boolean enabled = false;
    private String host = "smtp.gmail.com";
    private int port = 587;
    private boolean startTls = true;
    private boolean ssl = false;
    private String username = "";
    private String password = "";
    private String from = "";

    private MailConfig() {
        load();
    }

    public static MailConfig getInstance() {
        if (instance == null) {
            synchronized (MailConfig.class) {
                if (instance == null) {
                    instance = new MailConfig();
                }
            }
        }
        return instance;
    }

    public synchronized void reload() {
        load();
    }

    private void load() {
        apply(loadClasspath());
        if (OVERRIDE_FILE.exists()) {
            Properties overlay = new Properties();
            try (FileInputStream in = new FileInputStream(OVERRIDE_FILE)) {
                overlay.load(in);
                apply(overlay);
                LOGGER.info("Loaded SMTP override from " + OVERRIDE_FILE.getAbsolutePath());
            } catch (Exception e) {
                LOGGER.warning("Could not read mail override file: " + e.getMessage());
            }
        }
        LOGGER.info("MailConfig enabled=" + isEnabled() + " host=" + host + ":" + port + " user=" + username);
    }

    private Properties loadClasspath() {
        Properties props = new Properties();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            if (is != null) {
                props.load(is);
            }
        } catch (Exception e) {
            LOGGER.warning("Could not load mail settings from db.properties: " + e.getMessage());
        }
        return props;
    }

    private void apply(Properties props) {
        if (props == null || props.isEmpty()) return;
        if (props.getProperty("mail.enabled") != null) {
            enabled = Boolean.parseBoolean(props.getProperty("mail.enabled").trim());
        }
        if (props.getProperty("mail.host") != null) host = props.getProperty("mail.host").trim();
        if (props.getProperty("mail.port") != null) {
            port = Integer.parseInt(props.getProperty("mail.port").trim());
        }
        if (props.getProperty("mail.starttls") != null) {
            startTls = Boolean.parseBoolean(props.getProperty("mail.starttls").trim());
        }
        if (props.getProperty("mail.ssl") != null) {
            ssl = Boolean.parseBoolean(props.getProperty("mail.ssl").trim());
        }
        if (props.getProperty("mail.username") != null) username = props.getProperty("mail.username").trim();
        if (props.getProperty("mail.password") != null) {
            password = props.getProperty("mail.password").trim().replace(" ", "");
        }
        if (props.getProperty("mail.from") != null) from = props.getProperty("mail.from").trim();
    }

    public synchronized boolean save(String gmailUser, String appPassword, boolean turnOn) {
        Properties props = new Properties();
        props.setProperty("mail.enabled", String.valueOf(turnOn));
        props.setProperty("mail.host", "smtp.gmail.com");
        props.setProperty("mail.port", "587");
        props.setProperty("mail.starttls", "true");
        props.setProperty("mail.ssl", "false");
        props.setProperty("mail.username", gmailUser == null ? "" : gmailUser.trim());
        props.setProperty("mail.password", appPassword == null ? "" : appPassword.trim().replace(" ", ""));
        props.setProperty("mail.from", gmailUser == null ? "" : gmailUser.trim());
        try (FileOutputStream out = new FileOutputStream(OVERRIDE_FILE)) {
            props.store(out, "Sunrise Dental Clinic SMTP (Gmail App Password)");
            apply(props);
            return true;
        } catch (Exception e) {
            LOGGER.warning("Could not save mail settings: " + e.getMessage());
            return false;
        }
    }

    public boolean isEnabled() {
        return enabled && !username.isEmpty() && !password.isEmpty();
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public boolean isStartTls() {
        return startTls;
    }

    public boolean isSsl() {
        return ssl;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFrom() {
        if (username != null && !username.isBlank()) return username;
        if (from != null && !from.isBlank()) return from;
        return AppConfig.CLINIC_EMAIL;
    }
}
