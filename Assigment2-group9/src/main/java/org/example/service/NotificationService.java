/**
 * @author Group 9
 */
package org.example.service;

import org.example.model.LendingRecord;
import org.example.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for sending notifications.
 * Currently logs notifications to the console, but could be extended to send emails in the future.
 * Handles notifications for lending requests and overdue equipment.
 */
public class NotificationService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // List to store notifications for display in the UI
    private final List<Notification> notifications = new ArrayList<>();

    /**
     * Notification class to store notification details
     */
    public static class Notification {
        private final String recipient;
        private final String subject;
        private final String message;
        private final LocalDateTime timestamp;

        public Notification(String recipient, String subject, String message) {
            this.recipient = recipient;
            this.subject = subject;
            this.message = message;
            this.timestamp = LocalDateTime.now();
        }

        public String getRecipient() {
            return recipient;
        }

        public String getSubject() {
            return subject;
        }

        public String getMessage() {
            return message;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public String getFormattedTimestamp() {
            return timestamp.format(DATE_FORMATTER);
        }
    }

    /**
     * Send a notification for a new lending request
     *
     * @param lendingRecord The lending record
     * @param approver      The user who needs to approve the request
     * @return true if the notification was sent successfully, false otherwise
     */
    public boolean sendLendingRequestNotification(LendingRecord lendingRecord, User approver) {
        String subject = "New Equipment Lending Request";
        String message = String.format(
                "Dear %s,\n\n" +
                "A new equipment lending request has been submitted and requires your approval:\n\n" +
                "Equipment: %s\n" +
                "Requested by: %s\n" +
                "Requested on: %s\n" +
                "Requested from: %s\n" +
                "Requested until: %s\n\n" +
                "Please log in to the system to approve or reject this request.\n\n" +
                "Regards,\n" +
                "University Equipment Lending System",
                approver.getFullName(),
                lendingRecord.getEquipment().getName(),
                lendingRecord.getBorrower().getFullName(),
                LocalDateTime.now().format(DATE_FORMATTER),
                lendingRecord.getBorrowDate().format(DATE_FORMATTER),
                lendingRecord.getDueDate().format(DATE_FORMATTER)
        );

        return sendNotification(approver.getEmail(), subject, message);
    }

    /**
     * Send a notification for an overdue equipment
     *
     * @param lendingRecord The lending record
     * @return true if the notification was sent successfully, false otherwise
     */
    public boolean sendOverdueNotification(LendingRecord lendingRecord) {
        String subject = "Overdue Equipment Reminder";
        String message = String.format(
                "Dear %s,\n\n" +
                "This is a reminder that you have overdue equipment that needs to be returned:\n\n" +
                "Equipment: %s\n" +
                "Due date: %s\n" +
                "Days overdue: %d\n\n" +
                "Please return the equipment as soon as possible to avoid any penalties.\n\n" +
                "Regards,\n" +
                "University Equipment Lending System",
                lendingRecord.getBorrower().getFullName(),
                lendingRecord.getEquipment().getName(),
                lendingRecord.getDueDate().format(DATE_FORMATTER),
                LocalDateTime.now().toLocalDate().toEpochDay() - lendingRecord.getDueDate().toLocalDate().toEpochDay()
        );

        return sendNotification(lendingRecord.getBorrower().getEmail(), subject, message);
    }

    /**
     * Send a notification for an approved lending request
     *
     * @param lendingRecord The lending record
     * @return true if the notification was sent successfully, false otherwise
     */
    public boolean sendLendingApprovedNotification(LendingRecord lendingRecord) {
        String subject = "Equipment Lending Request Approved";
        String message = String.format(
                "Dear %s,\n\n" +
                "Your equipment lending request has been approved:\n\n" +
                "Equipment: %s\n" +
                "Approved on: %s\n" +
                "Available from: %s\n" +
                "Return by: %s\n\n" +
                "You can now collect the equipment from the designated location.\n\n" +
                "Regards,\n" +
                "University Equipment Lending System",
                lendingRecord.getBorrower().getFullName(),
                lendingRecord.getEquipment().getName(),
                LocalDateTime.now().format(DATE_FORMATTER),
                lendingRecord.getBorrowDate().format(DATE_FORMATTER),
                lendingRecord.getDueDate().format(DATE_FORMATTER)
        );

        return sendNotification(lendingRecord.getBorrower().getEmail(), subject, message);
    }

    /**
     * Send a notification for a rejected lending request
     *
     * @param lendingRecord The lending record
     * @param reason        The reason for rejection
     * @return true if the notification was sent successfully, false otherwise
     */
    public boolean sendLendingRejectedNotification(LendingRecord lendingRecord, String reason) {
        String subject = "Equipment Lending Request Rejected";
        String message = String.format(
                "Dear %s,\n\n" +
                "Unfortunately, your equipment lending request has been rejected:\n\n" +
                "Equipment: %s\n" +
                "Rejected on: %s\n" +
                "Reason: %s\n\n" +
                "If you have any questions, please contact the equipment administrator.\n\n" +
                "Regards,\n" +
                "University Equipment Lending System",
                lendingRecord.getBorrower().getFullName(),
                lendingRecord.getEquipment().getName(),
                LocalDateTime.now().format(DATE_FORMATTER),
                reason
        );

        return sendNotification(lendingRecord.getBorrower().getEmail(), subject, message);
    }

    /**
     * Send a notification
     * Currently logs to the console, but could be extended to send emails in the future.
     * Also stores the notification for display in the UI.
     *
     * @param recipientEmail The recipient's email address
     * @param subject        The notification subject
     * @param message        The notification message
     * @return true if the notification was sent successfully, false otherwise
     */
    private boolean sendNotification(String recipientEmail, String subject, String message) {
        System.out.println("=== NOTIFICATION ===");
        System.out.println("To: " + recipientEmail);
        System.out.println("Subject: " + subject);
        System.out.println("Message:");
        System.out.println(message);
        System.out.println("===================");

        // Store the notification for display in the UI
        notifications.add(new Notification(recipientEmail, subject, message));

        // In a real application, this would send an email
        // For now, we just log to the console and return true
        return true;
    }

    /**
     * Get all notifications
     *
     * @return List of all notifications
     */
    public List<Notification> getAllNotifications() {
        return new ArrayList<>(notifications);
    }

    /**
     * Clear all notifications
     */
    public void clearNotifications() {
        notifications.clear();
    }

    /**
     * Add a test notification
     * This is for demonstration purposes only
     * 
     * @param recipient The recipient's email address
     * @return true if the notification was added successfully, false otherwise
     */
    public boolean addTestNotification(String recipient) {
        String subject = "Test Notification";
        String message = "This is a test notification to demonstrate the notification system.\n\n" +
                "In a real application, notifications would be sent for various events such as:\n" +
                "- New lending requests\n" +
                "- Approved lending requests\n" +
                "- Rejected lending requests\n" +
                "- Overdue equipment reminders\n\n" +
                "Notifications are currently displayed in this tab and logged to the console.\n" +
                "In the future, they could also be sent via email.";

        notifications.add(new Notification(recipient, subject, message));
        return true;
    }

    /**
     * Check for overdue equipment and send notifications
     *
     * @param lendingService The lending service to use for retrieving lending records
     * @return The number of notifications sent
     */
    public int sendOverdueNotifications(LendingService lendingService) {
        List<LendingRecord> overdueRecords = lendingService.getOverdueLendingRecords();
        int notificationsSent = 0;

        for (LendingRecord record : overdueRecords) {
            if (sendOverdueNotification(record)) {
                notificationsSent++;
            }
        }

        return notificationsSent;
    }
}
