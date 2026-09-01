/**
 * @author Group 9
 */
package org.example.service;

import org.example.model.Equipment;
import org.example.model.LendingRecord;
import org.example.model.Student;
import org.example.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the NotificationService class.
 */
public class NotificationServiceTest {
    
    private NotificationService notificationService;
    
    @Mock
    private LendingService lendingService;
    
    @Mock
    private Equipment equipment;
    
    @Mock
    private Student student;
    
    @Mock
    private User approver;
    
    private LendingRecord lendingRecord;
    
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        notificationService = new NotificationService();
        
        // Set up the mocks
        when(equipment.getName()).thenReturn("Test Equipment");
        when(student.getFullName()).thenReturn("John Doe");
        when(student.getEmail()).thenReturn("john.doe@example.com");
        when(approver.getFullName()).thenReturn("Jane Smith");
        when(approver.getEmail()).thenReturn("jane.smith@example.com");
        
        // Create a lending record
        lendingRecord = new LendingRecord();
        lendingRecord.setBorrower(student);
        lendingRecord.setEquipment(equipment);
        lendingRecord.setBorrowDate(LocalDateTime.now().plusDays(1));
        lendingRecord.setDueDate(LocalDateTime.now().plusDays(7));
        
        // Redirect System.out to capture console output
        System.setOut(new PrintStream(outContent));
    }
    
    @AfterEach
    public void restoreStreams() {
        // Restore original System.out after each test
        System.setOut(originalOut);
    }
    
    @Test
    public void testSendLendingRequestNotification() {
        // Call the service method
        boolean result = notificationService.sendLendingRequestNotification(lendingRecord, approver);
        
        // Verify the result
        assertTrue(result);
        
        // Verify that the notification was logged to the console
        String output = outContent.toString();
        assertTrue(output.contains("=== NOTIFICATION ==="));
        assertTrue(output.contains("To: jane.smith@example.com"));
        assertTrue(output.contains("Subject: New Equipment Lending Request"));
        assertTrue(output.contains("Dear Jane Smith"));
        assertTrue(output.contains("Equipment: Test Equipment"));
        assertTrue(output.contains("Requested by: John Doe"));
    }
    
    @Test
    public void testSendOverdueNotification() {
        // Call the service method
        boolean result = notificationService.sendOverdueNotification(lendingRecord);
        
        // Verify the result
        assertTrue(result);
        
        // Verify that the notification was logged to the console
        String output = outContent.toString();
        assertTrue(output.contains("=== NOTIFICATION ==="));
        assertTrue(output.contains("To: john.doe@example.com"));
        assertTrue(output.contains("Subject: Overdue Equipment Reminder"));
        assertTrue(output.contains("Dear John Doe"));
        assertTrue(output.contains("Equipment: Test Equipment"));
    }
    
    @Test
    public void testSendLendingApprovedNotification() {
        // Call the service method
        boolean result = notificationService.sendLendingApprovedNotification(lendingRecord);
        
        // Verify the result
        assertTrue(result);
        
        // Verify that the notification was logged to the console
        String output = outContent.toString();
        assertTrue(output.contains("=== NOTIFICATION ==="));
        assertTrue(output.contains("To: john.doe@example.com"));
        assertTrue(output.contains("Subject: Equipment Lending Request Approved"));
        assertTrue(output.contains("Dear John Doe"));
        assertTrue(output.contains("Equipment: Test Equipment"));
    }
    
    @Test
    public void testSendLendingRejectedNotification() {
        // Call the service method
        boolean result = notificationService.sendLendingRejectedNotification(lendingRecord, "Equipment not available");
        
        // Verify the result
        assertTrue(result);
        
        // Verify that the notification was logged to the console
        String output = outContent.toString();
        assertTrue(output.contains("=== NOTIFICATION ==="));
        assertTrue(output.contains("To: john.doe@example.com"));
        assertTrue(output.contains("Subject: Equipment Lending Request Rejected"));
        assertTrue(output.contains("Dear John Doe"));
        assertTrue(output.contains("Equipment: Test Equipment"));
        assertTrue(output.contains("Reason: Equipment not available"));
    }
    
    @Test
    public void testSendOverdueNotifications() {
        // Create a list of overdue lending records
        List<LendingRecord> overdueRecords = new ArrayList<>();
        overdueRecords.add(lendingRecord);
        
        // Mock the lendingService
        when(lendingService.getOverdueLendingRecords()).thenReturn(overdueRecords);
        
        // Call the service method
        int notificationsSent = notificationService.sendOverdueNotifications(lendingService);
        
        // Verify the result
        assertEquals(1, notificationsSent);
        
        // Verify that the lendingService method was called
        verify(lendingService).getOverdueLendingRecords();
    }
    
    @Test
    public void testSendOverdueNotifications_NoOverdueRecords() {
        // Create an empty list of overdue lending records
        List<LendingRecord> overdueRecords = new ArrayList<>();
        
        // Mock the lendingService
        when(lendingService.getOverdueLendingRecords()).thenReturn(overdueRecords);
        
        // Call the service method
        int notificationsSent = notificationService.sendOverdueNotifications(lendingService);
        
        // Verify the result
        assertEquals(0, notificationsSent);
        
        // Verify that the lendingService method was called
        verify(lendingService).getOverdueLendingRecords();
    }
}