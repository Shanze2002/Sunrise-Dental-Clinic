package com.sunrisedental.service.notification;

/**
 * Immutable domain event published by clinic services (Observer Pattern).
 */
public class NotificationEvent {

    public static final String APPOINTMENT_BOOKED = "APPOINTMENT_BOOKED";
    public static final String APPOINTMENT_CANCELLED = "APPOINTMENT_CANCELLED";
    public static final String BILL_GENERATED = "BILL_GENERATED";
    public static final String PAYMENT_RECEIVED = "PAYMENT_RECEIVED";
    public static final String PATIENT_REGISTERED = "PATIENT_REGISTERED";
    public static final String BILL_REMAINDER = "BILL_REMAINDER";

    private final String eventType;
    private final String recipientEmail;
    private final String recipientName;
    private final String subject;
    private final String messageBody;

    public NotificationEvent(String eventType, String recipientEmail, String recipientName, String subject, String messageBody) {
        this.eventType = eventType;
        this.recipientEmail = recipientEmail;
        this.recipientName = recipientName;
        this.subject = subject;
        this.messageBody = messageBody;
    }

    public String getEventType() {
        return eventType;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public String getSubject() {
        return subject;
    }

    public String getMessageBody() {
        return messageBody;
    }
}
