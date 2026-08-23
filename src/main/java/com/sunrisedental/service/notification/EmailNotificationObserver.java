package com.sunrisedental.service.notification;

import com.sunrisedental.config.AppConfig;
import com.sunrisedental.config.MailConfig;
import com.sunrisedental.dao.EmailNotificationDAO;
import com.sunrisedental.model.EmailNotification;
import com.sunrisedental.util.SmtpMailSender;
import com.sunrisedental.util.ValidationUtil;

import java.util.logging.Logger;

/**
 * Observer Pattern: email channel — persists outbox row then sends via SMTP.
 */
public class EmailNotificationObserver implements NotificationObserver {

    private static final Logger LOGGER = Logger.getLogger(EmailNotificationObserver.class.getName());
    private final EmailNotificationDAO emailDAO = new EmailNotificationDAO();

    @Override
    public void onNotification(NotificationEvent event) {
        if (event == null || !ValidationUtil.isValidEmail(event.getRecipientEmail())) {
            LOGGER.warning("Email skipped: missing or invalid recipient for event " +
                    (event != null ? event.getEventType() : "null"));
            return;
        }

        EmailNotification record = new EmailNotification();
        record.setRecipient(event.getRecipientEmail().trim());
        record.setSubject(event.getSubject());
        record.setBody(event.getMessageBody());
        record.setEventType(event.getEventType());
        record.setDeliveryStatus("QUEUED");
        emailDAO.create(record);

        boolean sent = SmtpMailSender.send(event.getRecipientEmail().trim(), event.getSubject(), event.getMessageBody());
        String status;
        if (sent) {
            status = "SENT";
        } else if (!MailConfig.getInstance().isEnabled()) {
            status = "QUEUED";
            LOGGER.warning("Email queued (SMTP not configured) for " + event.getRecipientEmail()
                    + " From: " + AppConfig.CLINIC_EMAIL);
        } else {
            status = "FAILED";
        }
        if (record.getEmailId() > 0) {
            emailDAO.updateStatus(record.getEmailId(), status);
        }
    }
}
