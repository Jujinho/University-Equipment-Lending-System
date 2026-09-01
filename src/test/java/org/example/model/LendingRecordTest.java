/**
 * @author Group 9
 */
package org.example.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the LendingRecord class.
 */
public class LendingRecordTest {
    
    private LendingRecord lendingRecord;
    private Student student;
    private Equipment equipment;
    private Course course;
    private AcademicStaff approver;
    private LocalDateTime borrowDate;
    private LocalDateTime dueDate;
    
    @BeforeEach
    public void setUp() {
        // Create a student
        student = new Student(
                1,
                "jsmith",
                "password123",
                "John",
                "Smith",
                "john.smith@example.com",
                "1234567890",
                LocalDate.of(2000, 1, 15),
                "123 Main St",
                "S12345",
                "Computer Science",
                2
        );
        
        // Create an equipment
        equipment = new Equipment(
                2,
                "3D Printer",
                "A high-quality 3D printer for creating prototypes",
                "Printer",
                "Good",
                LocalDate.of(2022, 1, 15),
                1500.00,
                "MakerBot",
                "Replicator+",
                "MB12345",
                "Engineering Lab",
                true
        );
        
        // Create an academic staff (approver)
        approver = new AcademicStaff(
                3,
                "jdoe",
                "password123",
                "Jane",
                "Doe",
                "jane.doe@example.com",
                "1234567890",
                LocalDate.of(1980, 5, 15),
                "123 Oak St",
                "AS12345",
                "Computer Science",
                "Professor"
        );
        
        // Create a course
        course = new Course(
                4,
                "CS101",
                "Introduction to Programming",
                "A course that introduces basic programming concepts",
                1,
                2023,
                approver
        );
        
        // Set up dates
        borrowDate = LocalDateTime.now();
        dueDate = borrowDate.plusDays(7);
        
        // Create a new lending record instance before each test
        lendingRecord = new LendingRecord(
                1,
                student,
                equipment,
                course,
                borrowDate,
                dueDate,
                "Pending",
                "For a project",
                "Good",
                "Will return on time",
                approver
        );
    }
    
    @Test
    public void testLendingRecordConstructorForStudent() {
        // Test that the lending record is created with the correct values
        assertEquals(1, lendingRecord.getId());
        assertEquals(student, lendingRecord.getBorrower());
        assertEquals(equipment, lendingRecord.getEquipment());
        assertEquals(course, lendingRecord.getCourse());
        assertEquals(borrowDate, lendingRecord.getBorrowDate());
        assertEquals(dueDate, lendingRecord.getDueDate());
        assertNull(lendingRecord.getReturnDate());
        assertEquals("Pending", lendingRecord.getStatus());
        assertEquals("For a project", lendingRecord.getPurpose());
        assertEquals("Good", lendingRecord.getCondition());
        assertEquals("Will return on time", lendingRecord.getNotes());
        assertEquals(approver, lendingRecord.getApprover());
    }
    
    @Test
    public void testLendingRecordConstructorForStaff() {
        // Create a lending record for a staff member (no course or approver required)
        LendingRecord staffLendingRecord = new LendingRecord(
                2,
                approver, // Staff member as borrower
                equipment,
                borrowDate,
                dueDate,
                "Approved", // Staff lending requests are automatically approved
                "For research",
                "Good",
                "Will return on time"
        );
        
        // Test that the lending record is created with the correct values
        assertEquals(2, staffLendingRecord.getId());
        assertEquals(approver, staffLendingRecord.getBorrower());
        assertEquals(equipment, staffLendingRecord.getEquipment());
        assertNull(staffLendingRecord.getCourse()); // No course for staff
        assertEquals(borrowDate, staffLendingRecord.getBorrowDate());
        assertEquals(dueDate, staffLendingRecord.getDueDate());
        assertNull(staffLendingRecord.getReturnDate());
        assertEquals("Approved", staffLendingRecord.getStatus());
        assertEquals("For research", staffLendingRecord.getPurpose());
        assertEquals("Good", staffLendingRecord.getCondition());
        assertEquals("Will return on time", staffLendingRecord.getNotes());
        assertNull(staffLendingRecord.getApprover()); // No approver for staff
    }
    
    @Test
    public void testLendingRecordSetters() {
        // Create a new student, equipment, course, and approver
        Student newStudent = new Student(
                5,
                "bwhite",
                "password456",
                "Bob",
                "White",
                "bob.white@example.com",
                "0987654321",
                LocalDate.of(2001, 2, 20),
                "456 Oak St",
                "S67890",
                "Information Technology",
                3
        );
        
        Equipment newEquipment = new Equipment(
                6,
                "Laser Cutter",
                "A precision laser cutter for detailed work",
                "Cutter",
                "Excellent",
                LocalDate.of(2023, 3, 10),
                2500.00,
                "Glowforge",
                "Pro",
                "GF67890",
                "Design Studio",
                true
        );
        
        AcademicStaff newApprover = new AcademicStaff(
                7,
                "asmith",
                "password789",
                "Alice",
                "Smith",
                "alice.smith@example.com",
                "5555555555",
                LocalDate.of(1975, 10, 20),
                "789 Pine St",
                "AS67890",
                "Mathematics",
                "Associate Professor"
        );
        
        Course newCourse = new Course(
                8,
                "CS102",
                "Data Structures",
                "A course on data structures and algorithms",
                2,
                2023,
                newApprover
        );
        
        // New dates
        LocalDateTime newBorrowDate = LocalDateTime.now().plusDays(1);
        LocalDateTime newDueDate = newBorrowDate.plusDays(10);
        LocalDateTime returnDate = newDueDate.minusDays(2);
        
        // Test the setters
        lendingRecord.setId(9);
        lendingRecord.setBorrower(newStudent);
        lendingRecord.setEquipment(newEquipment);
        lendingRecord.setCourse(newCourse);
        lendingRecord.setBorrowDate(newBorrowDate);
        lendingRecord.setDueDate(newDueDate);
        lendingRecord.setReturnDate(returnDate);
        lendingRecord.setStatus("Returned");
        lendingRecord.setPurpose("For a different project");
        lendingRecord.setCondition("Excellent");
        lendingRecord.setNotes("Returned early");
        lendingRecord.setApprover(newApprover);
        
        // Verify the values were set correctly
        assertEquals(9, lendingRecord.getId());
        assertEquals(newStudent, lendingRecord.getBorrower());
        assertEquals(newEquipment, lendingRecord.getEquipment());
        assertEquals(newCourse, lendingRecord.getCourse());
        assertEquals(newBorrowDate, lendingRecord.getBorrowDate());
        assertEquals(newDueDate, lendingRecord.getDueDate());
        assertEquals(returnDate, lendingRecord.getReturnDate());
        assertEquals("Returned", lendingRecord.getStatus());
        assertEquals("For a different project", lendingRecord.getPurpose());
        assertEquals("Excellent", lendingRecord.getCondition());
        assertEquals("Returned early", lendingRecord.getNotes());
        assertEquals(newApprover, lendingRecord.getApprover());
    }
    
    @Test
    public void testIsOverdue() {
        // Test when the lending is not overdue
        assertFalse(lendingRecord.isOverdue());
        
        // Test when the lending is overdue
        lendingRecord.setDueDate(LocalDateTime.now().minusDays(1)); // Due date in the past
        assertTrue(lendingRecord.isOverdue());
        
        // Test when the lending has been returned (should not be overdue)
        lendingRecord.setReturnDate(LocalDateTime.now());
        assertFalse(lendingRecord.isOverdue());
    }
    
    @Test
    public void testReturnEquipment() {
        // Set up return details
        LocalDateTime returnDate = LocalDateTime.now();
        String condition = "Excellent";
        String notes = "Returned in better condition";
        
        // Return the equipment
        lendingRecord.returnEquipment(returnDate, condition, notes);
        
        // Verify the return details
        assertEquals(returnDate, lendingRecord.getReturnDate());
        assertEquals(condition, lendingRecord.getCondition());
        assertEquals(notes, lendingRecord.getNotes());
        assertEquals("Returned", lendingRecord.getStatus());
    }
    
    @Test
    public void testToString() {
        // Test the toString method
        String expectedString = "LendingRecord{" +
                "id=1" +
                ", borrower=" + student.getUsername() +
                ", equipment=" + equipment.getName() +
                ", course=" + course.getCourseCode() +
                ", borrowDate=" + borrowDate +
                ", dueDate=" + dueDate +
                ", returnDate=" + null +
                ", status='Pending'" +
                ", purpose='For a project'" +
                ", condition='Good'" +
                ", approver=" + approver.getUsername() +
                '}';
        
        assertEquals(expectedString, lendingRecord.toString());
    }
    
    @Test
    public void testEquals() {
        // Create a lending record with the same ID but different attributes
        LendingRecord sameLendingRecord = new LendingRecord(
                1, // Same ID
                approver, // Different borrower
                equipment,
                borrowDate,
                dueDate,
                "Approved",
                "Different purpose",
                "Different condition",
                "Different notes"
        );
        
        // Create a lending record with a different ID
        LendingRecord differentLendingRecord = new LendingRecord(
                2, // Different ID
                student,
                equipment,
                course,
                borrowDate,
                dueDate,
                "Pending",
                "For a project",
                "Good",
                "Will return on time",
                approver
        );
        
        // Test equals
        assertEquals(lendingRecord, lendingRecord); // Same object
        assertEquals(lendingRecord, sameLendingRecord); // Same ID
        assertNotEquals(lendingRecord, differentLendingRecord); // Different ID
        assertNotEquals(lendingRecord, null); // Null
        assertNotEquals(lendingRecord, "Not a LendingRecord"); // Different class
    }
    
    @Test
    public void testHashCode() {
        // Create a lending record with the same ID but different attributes
        LendingRecord sameLendingRecord = new LendingRecord(
                1, // Same ID
                approver, // Different borrower
                equipment,
                borrowDate,
                dueDate,
                "Approved",
                "Different purpose",
                "Different condition",
                "Different notes"
        );
        
        // Create a lending record with a different ID
        LendingRecord differentLendingRecord = new LendingRecord(
                2, // Different ID
                student,
                equipment,
                course,
                borrowDate,
                dueDate,
                "Pending",
                "For a project",
                "Good",
                "Will return on time",
                approver
        );
        
        // Test hashCode
        assertEquals(lendingRecord.hashCode(), sameLendingRecord.hashCode()); // Same ID
        assertNotEquals(lendingRecord.hashCode(), differentLendingRecord.hashCode()); // Different ID
    }
}