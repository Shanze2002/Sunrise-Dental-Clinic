package com.sunrisedental.service.notification;

/**
 * Observer Pattern: subscribers react to clinic notification events.
 */
public interface NotificationObserver {
    void onNotification(NotificationEvent event);
}
