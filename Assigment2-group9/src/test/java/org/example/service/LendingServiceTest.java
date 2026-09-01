/**
 * @author Group 9
 */
package org.example.service;

import org.example.db.LendingRecordRepository;
import org.example.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the LendingService class.
 */
public class LendingServiceTest {

    @Mock
    private LendingRecordRepository lendingRecordRepository;

    @Mock
    private CourseService courseService;

    @Mock
    private NotificationService notificationService;

    private LendingService lendingService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        lendingService = new LendingService(lendingRecordRepository, courseService, notificationService);
    }

    @Test
    public void testCreateStudentLendingRequest_ValidRequest() {
        // Create test data
        Student student = new Student();
        student.setId(1);

        Equipment equipment = new Equipment();
        equipment.setId(2);
        equipment.setAvailable(true);

        Course course = new Course();
        course.setId(3);

        AcademicStaff instructor = new AcademicStaff();
        instructor.setId(4);
        course.setInstructor(instructor);

        LocalDateTime borrowDate = LocalDateTime.now();
        LocalDateTime dueDate = borrowDate.plusDays(7); // Within 2 weeks
        String purpose = "For a project";
        String notes = "Will return on time";

        // Mock the repository and service methods
        when(courseService.isStudentEnrolledInCourse(1, 3)).thenReturn(true);
        when(lendingRecordRepository.createLendingRecord(any(LendingRecord.class))).thenReturn(true);

        // Call the service method
        boolean result = lendingService.createStudentLendingRequest(
                student, equipment, course, borrowDate, dueDate, purpose, notes);

        // Verify the result
        assertTrue(result);

        // Verify that the repository and service methods were called
        verify(courseService).isStudentEnrolledInCourse(1, 3);
        verify(lendingRecordRepository).createLendingRecord(any(LendingRecord.class));
        verify(notificationService).sendLendingRequestNotification(any(LendingRecord.class), eq(instructor));
    }

    @Test
    public void testCreateStudentLendingRequest_StudentNotEnrolled() {
        // Create test data
        Student student = new Student();
        student.setId(1);

        Equipment equipment = new Equipment();
        equipment.setId(2);
        equipment.setAvailable(true);

        Course course = new Course();
        course.setId(3);

        LocalDateTime borrowDate = LocalDateTime.now();
        LocalDateTime dueDate = borrowDate.plusDays(7);
        String purpose = "For a project";
        String notes = "Will return on time";

        // Mock the repository and service methods
        when(courseService.isStudentEnrolledInCourse(1, 3)).thenReturn(false);

        // Call the service method
        boolean result = lendingService.createStudentLendingRequest(
                student, equipment, course, borrowDate, dueDate, purpose, notes);

        // Verify the result
        assertFalse(result);

        // Verify that the repository methods were not called
        verify(courseService).isStudentEnrolledInCourse(1, 3);
        verify(lendingRecordRepository, never()).createLendingRecord(any(LendingRecord.class));
        verify(notificationService, never()).sendLendingRequestNotification(any(LendingRecord.class), any(AcademicStaff.class));
    }

    @Test
    public void testCreateStudentLendingRequest_EquipmentNotAvailable() {
        // Create test data
        Student student = new Student();
        student.setId(1);

        Equipment equipment = new Equipment();
        equipment.setId(2);
        equipment.setAvailable(false); // Equipment not available

        Course course = new Course();
        course.setId(3);

        LocalDateTime borrowDate = LocalDateTime.now();
        LocalDateTime dueDate = borrowDate.plusDays(7);
        String purpose = "For a project";
        String notes = "Will return on time";

        // Mock the repository and service methods
        when(courseService.isStudentEnrolledInCourse(1, 3)).thenReturn(true);

        // Call the service method
        boolean result = lendingService.createStudentLendingRequest(
                student, equipment, course, borrowDate, dueDate, purpose, notes);

        // Verify the result
        assertFalse(result);

        // Verify that the repository methods were not called
        verify(courseService).isStudentEnrolledInCourse(1, 3);
        verify(lendingRecordRepository, never()).createLendingRecord(any(LendingRecord.class));
        verify(notificationService, never()).sendLendingRequestNotification(any(LendingRecord.class), any(AcademicStaff.class));
    }

    @Test
    public void testCreateStudentLendingRequest_BorrowingPeriodTooLong() {
        // Create test data
        Student student = new Student();
        student.setId(1);

        Equipment equipment = new Equipment();
        equipment.setId(2);
        equipment.setAvailable(true);

        Course course = new Course();
        course.setId(3);

        LocalDateTime borrowDate = LocalDateTime.now();
        LocalDateTime dueDate = borrowDate.plusDays(21); // More than 2 weeks
        String purpose = "For a project";
        String notes = "Will return on time";

        // Mock the repository and service methods
        when(courseService.isStudentEnrolledInCourse(1, 3)).thenReturn(true);

        // Call the service method
        boolean result = lendingService.createStudentLendingRequest(
                student, equipment, course, borrowDate, dueDate, purpose, notes);

        // Verify the result
        assertFalse(result);

        // Verify that the repository methods were not called
        verify(courseService).isStudentEnrolledInCourse(1, 3);
        verify(lendingRecordRepository, never()).createLendingRecord(any(LendingRecord.class));
        verify(notificationService, never()).sendLendingRequestNotification(any(LendingRecord.class), any(AcademicStaff.class));
    }

    @Test
    public void testCreateStaffLendingRequest_ValidRequest() {
        // Create test data
        AcademicStaff staff = new AcademicStaff();
        staff.setId(1);

        Equipment equipment = new Equipment();
        equipment.setId(2);
        equipment.setAvailable(true);

        LocalDateTime borrowDate = LocalDateTime.now();
        LocalDateTime dueDate = borrowDate.plusDays(30); // Staff can borrow for longer
        String purpose = "For research";
        String notes = "Will return on time";

        // Mock the repository methods
        when(lendingRecordRepository.createLendingRecord(any(LendingRecord.class))).thenReturn(true);

        // Call the service method
        boolean result = lendingService.createStaffLendingRequest(
                staff, equipment, borrowDate, dueDate, purpose, notes);

        // Verify the result
        assertTrue(result);

        // Verify that the repository methods were called
        verify(lendingRecordRepository).createLendingRecord(any(LendingRecord.class));
    }

    @Test
    public void testCreateStaffLendingRequest_NotStaff() {
        // Create test data
        Student student = new Student(); // Not a staff member
        student.setId(1);

        Equipment equipment = new Equipment();
        equipment.setId(2);
        equipment.setAvailable(true);

        LocalDateTime borrowDate = LocalDateTime.now();
        LocalDateTime dueDate = borrowDate.plusDays(30);
        String purpose = "For research";
        String notes = "Will return on time";

        // Call the service method
        boolean result = lendingService.createStaffLendingRequest(
                student, equipment, borrowDate, dueDate, purpose, notes);

        // Verify the result
        assertFalse(result);

        // Verify that the repository methods were not called
        verify(lendingRecordRepository, never()).createLendingRecord(any(LendingRecord.class));
    }

    @Test
    public void testApproveLendingRequest() {
        // Mock the repository methods
        when(lendingRecordRepository.approveLendingRequest(1, 2)).thenReturn(true);

        // Call the service method
        boolean result = lendingService.approveLendingRequest(1, 2);

        // Verify the result
        assertTrue(result);

        // Verify that the repository methods were called
        verify(lendingRecordRepository).approveLendingRequest(1, 2);
    }

    @Test
    public void testRejectLendingRequest() {
        // Mock the repository methods
        when(lendingRecordRepository.rejectLendingRequest(1, 2, "Not available")).thenReturn(true);

        // Call the service method
        boolean result = lendingService.rejectLendingRequest(1, 2, "Not available");

        // Verify the result
        assertTrue(result);

        // Verify that the repository methods were called
        verify(lendingRecordRepository).rejectLendingRequest(1, 2, "Not available");
    }

    @Test
    public void testReturnEquipment() {
        // Mock the repository methods
        LocalDateTime returnDate = LocalDateTime.now();
        when(lendingRecordRepository.returnEquipment(1, returnDate, "Good", "Returned on time")).thenReturn(true);

        // Call the service method
        boolean result = lendingService.returnEquipment(1, returnDate, "Good", "Returned on time");

        // Verify the result
        assertTrue(result);

        // Verify that the repository methods were called
        verify(lendingRecordRepository).returnEquipment(1, returnDate, "Good", "Returned on time");
    }

    @Test
    public void testGetAcademicStaffLendingStatistics() {
        // Create test data
        Course course1 = new Course();
        course1.setId(1);
        course1.setCourseCode("CS101");

        Course course2 = new Course();
        course2.setId(2);
        course2.setCourseCode("CS102");

        List<Course> courses = new ArrayList<>();
        courses.add(course1);
        courses.add(course2);

        LendingRecord record1 = new LendingRecord();
        record1.setStatus("Borrowed");
        record1.setCourse(course1);
        record1.setDueDate(LocalDateTime.now().plusDays(1)); // Set future due date

        LendingRecord record2 = new LendingRecord();
        record2.setStatus("Returned");
        record2.setCourse(course1);
        record2.setDueDate(LocalDateTime.now().plusDays(2)); // Set future due date
        record2.setReturnDate(LocalDateTime.now()); // Set return date since status is "Returned"

        LendingRecord record3 = new LendingRecord();
        record3.setStatus("Overdue");
        record3.setCourse(course2);
        record3.setDueDate(LocalDateTime.now().minusDays(1)); // Set past due date for overdue

        List<LendingRecord> records1 = new ArrayList<>();
        records1.add(record1);
        records1.add(record2);

        List<LendingRecord> records2 = new ArrayList<>();
        records2.add(record3);

        // Mock the repository and service methods
        when(courseService.getCoursesByInstructor(1)).thenReturn(courses);
        when(lendingRecordRepository.getLendingRecordsByCourse(1)).thenReturn(records1);
        when(lendingRecordRepository.getLendingRecordsByCourse(2)).thenReturn(records2);

        // Call the service method
        LendingService.LendingStatistics stats = lendingService.getAcademicStaffLendingStatistics(1);

        // Verify the result
        assertEquals(3, stats.totalLendings);
        assertEquals(1, stats.currentLendings);
        assertEquals(1, stats.overdueLendings);
        assertEquals(2, stats.lendingsPerCourse.get("CS101"));
        assertEquals(1, stats.lendingsPerCourse.get("CS102"));

        // Verify that the repository and service methods were called
        verify(courseService).getCoursesByInstructor(1);
        verify(lendingRecordRepository).getLendingRecordsByCourse(1);
        verify(lendingRecordRepository).getLendingRecordsByCourse(2);
    }
}
