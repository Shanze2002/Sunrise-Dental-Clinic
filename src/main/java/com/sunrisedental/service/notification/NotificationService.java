package com.sunrisedental.service.notification;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Observer Pattern Subject (Singleton).
 * Clinic services publish events; registered observers (email, future SMS) handle delivery.
 */
public class NotificationService {

    private static final Logger LOGGER = Logger.getLogger(NotificationService.class.getName());
    private static volatile NotificationService instance;

    private final List<NotificationObserver> observers = new ArrayList<>();

    private NotificationService() {}

    public static NotificationService getInstance() {
        if (instance == null) {
            synchronized (NotificationService.class) {
                if (instance == null) {
                    instance = new NotificationService();
                }
            }
        }
        return instance;
    }

    public synchronized void register(NotificationObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void publish(NotificationEvent event) {
        if (event == null) return;
        List<NotificationObserver> snapshot;
        synchronized (this) {
            snapshot = new ArrayList<>(observers);
        }
        for (NotificationObserver observer : snapshot) {
            try {
                observer.onNotification(event);
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Notification observer failed: " + observer.getClass().getSimpleName(), e);
            }
        }
    }
}
