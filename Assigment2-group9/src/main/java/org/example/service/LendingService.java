/**
 * @author Group 9
 */
package org.example.service;

import org.example.db.LendingRecordRepository;
import org.example.model.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

/**
 * Service class for lending-related operations.
 */
public class LendingService {

    private final LendingRecordRepository lendingRecordRepository;
    private final CourseService courseService;
    private final NotificationService notificationService;

    /**
     * Constructor
     *
     * @param lendingRecordRepository The LendingRecordRepository to use for lending record operations
     * @param courseService           The CourseService to use for course operations
     * @param notificationService     The NotificationService to use for sending notifications
     */
    public LendingService(LendingRecordRepository lendingRecordRepository, CourseService courseService, NotificationService notificationService) {
        this.lendingRecordRepository = lendingRecordRepository;
        this.courseService = courseService;
        this.notificationService = notificationService;
    }

    /**
     * Get a lending record by ID
     *
     * @param id The lending record ID
     * @return An Optional containing the LendingRecord if found, or empty if not found
     */
    public Optional<LendingRecord> getLendingRecordById(int id) {
        return lendingRecordRepository.getLendingRecordById(id);
    }

    /**
     * Get all lending records
     *
     * @return A list of all lending records
     */
    public List<LendingRecord> getAllLendingRecords() {
        return lendingRecordRepository.getAllLendingRecords();
    }

    /**
     * Get lending records by borrower
     *
     * @param borrowerId The ID of the borrower
     * @return A list of lending records for the borrower
     */
    public List<LendingRecord> getLendingRecordsByBorrower(int borrowerId) {
        return lendingRecordRepository.getLendingRecordsByBorrower(borrowerId);
    }

    /**
     * Get lending records by equipment
     *
     * @param equipmentId The ID of the equipment
     * @return A list of lending records for the equipment
     */
    public List<LendingRecord> getLendingRecordsByEquipment(int equipmentId) {
        return lendingRecordRepository.getLendingRecordsByEquipment(equipmentId);
    }

    /**
     * Get lending records by course
     *
     * @param courseId The ID of the course
     * @return A list of lending records for the course
     */
    public List<LendingRecord> getLendingRecordsByCourse(int courseId) {
        return lendingRecordRepository.getLendingRecordsByCourse(courseId);
    }

    /**
     * Get lending records by status
     *
     * @param status The status to search for
     * @return A list of lending records with the specified status
     */
    public List<LendingRecord> getLendingRecordsByStatus(String status) {
        return lendingRecordRepository.getLendingRecordsByStatus(status);
    }

    /**
     * Get overdue lending records
     *
     * @return A list of overdue lending records
     */
    public List<LendingRecord> getOverdueLendingRecords() {
        return lendingRecordRepository.getOverdueLendingRecords();
    }

    /**
     * Get lending records by date range
     *
     * @param startDate The start date of the range
     * @param endDate   The end date of the range
     * @return A list of lending records within the date range
     */
    public List<LendingRecord> getLendingRecordsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return lendingRecordRepository.getLendingRecordsByDateRange(startDate, endDate);
    }

    /**
     * Create a new lending record
     *
     * @param lendingRecord The lending record to create
     * @return true if the lending record was created successfully, false otherwise
     */
    public boolean createLendingRecord(LendingRecord lendingRecord) {
        // Validate the lending record
        if (!validateLendingRecord(lendingRecord)) {
            return false;
        }

        return lendingRecordRepository.createLendingRecord(lendingRecord);
    }

    /**
     * Update a lending record
     *
     * @param lendingRecord The lending record to update
     * @return true if the lending record was updated successfully, false otherwise
     */
    public boolean updateLendingRecord(LendingRecord lendingRecord) {
        return lendingRecordRepository.updateLendingRecord(lendingRecord);
    }

    /**
     * Delete a lending record
     *
     * @param id The ID of the lending record to delete
     * @return true if the lending record was deleted successfully, false otherwise
     */
    public boolean deleteLendingRecord(int id) {
        return lendingRecordRepository.deleteLendingRecord(id);
    }

    /**
     * Return equipment
     *
     * @param lendingRecordId The ID of the lending record
     * @param returnDate      The date and time when the equipment was returned
     * @param condition       The condition of the equipment at the time of return
     * @param notes           Additional notes about the return
     * @return true if the equipment was returned successfully, false otherwise
     */
    public boolean returnEquipment(int lendingRecordId, LocalDateTime returnDate, String condition, String notes) {
        return lendingRecordRepository.returnEquipment(lendingRecordId, returnDate, condition, notes);
    }

    /**
     * Approve a lending request
     *
     * @param lendingRecordId The ID of the lending record
     * @param approverId      The ID of the approver
     * @return true if the lending request was approved successfully, false otherwise
     */
    public boolean approveLendingRequest(int lendingRecordId, int approverId) {
        return lendingRecordRepository.approveLendingRequest(lendingRecordId, approverId);
    }

    /**
     * Reject a lending request
     *
     * @param lendingRecordId The ID of the lending record
     * @param approverId      The ID of the approver
     * @param notes           The reason for rejection
     * @return true if the lending request was rejected successfully, false otherwise
     */
    public boolean rejectLendingRequest(int lendingRecordId, int approverId, String notes) {
        return lendingRecordRepository.rejectLendingRequest(lendingRecordId, approverId, notes);
    }

    /**
     * Create a new lending request for a student
     *
     * @param student    The student borrowing the equipment
     * @param equipment  The equipment being borrowed
     * @param course     The course for which the equipment is being borrowed
     * @param borrowDate The date and time when the equipment will be borrowed
     * @param dueDate    The date and time when the equipment is due to be returned
     * @param purpose    The purpose for borrowing the equipment
     * @param notes      Additional notes about the lending
     * @return true if the lending request was created successfully, false otherwise
     */
    public boolean createStudentLendingRequest(Student student, Equipment equipment, Course course,
                                              LocalDateTime borrowDate, LocalDateTime dueDate,
                                              String purpose, String notes) {
        // Check if the student is enrolled in the course
        if (!courseService.isStudentEnrolledInCourse(student.getId(), course.getId())) {
            return false;
        }

        // Check if the equipment is available
        if (!equipment.isAvailable()) {
            return false;
        }

        // Check if the borrowing period is valid (max 2 weeks for students)
        if (ChronoUnit.DAYS.between(borrowDate, dueDate) > 14) {
            return false;
        }

        // Create the lending record
        LendingRecord lendingRecord = new LendingRecord(
                0, student, equipment, course,
                borrowDate, dueDate, "Pending",
                purpose, equipment.getCondition(), notes, null
        );

        boolean success = lendingRecordRepository.createLendingRecord(lendingRecord);

        // If the lending record was created successfully, send a notification to the course instructor
        if (success) {
            // Get the course instructor (academic staff)
            AcademicStaff instructor = course.getInstructor();
            if (instructor != null) {
                // Send notification to the instructor
                notificationService.sendLendingRequestNotification(lendingRecord, instructor);
            }
        }

        return success;
    }

    /**
     * Create a new lending request for a staff member
     *
     * @param staff      The staff member borrowing the equipment
     * @param equipment  The equipment being borrowed
     * @param borrowDate The date and time when the equipment will be borrowed
     * @param dueDate    The date and time when the equipment is due to be returned
     * @param purpose    The purpose for borrowing the equipment
     * @param notes      Additional notes about the lending
     * @return true if the lending request was created successfully, false otherwise
     */
    public boolean createStaffLendingRequest(User staff, Equipment equipment,
                                            LocalDateTime borrowDate, LocalDateTime dueDate,
                                            String purpose, String notes) {
        // Check if the user is a staff member
        if (!(staff instanceof AcademicStaff || staff instanceof ProfessionalStaff)) {
            return false;
        }

        // Check if the equipment is available
        if (!equipment.isAvailable()) {
            return false;
        }

        // Create the lending record
        LendingRecord lendingRecord = new LendingRecord(
                0, staff, equipment,
                borrowDate, dueDate, "Approved",
                purpose, equipment.getCondition(), notes
        );

        return lendingRecordRepository.createLendingRecord(lendingRecord);
    }

    /**
     * Get lending statistics for an academic staff
     *
     * @param academicStaffId The ID of the academic staff
     * @return A summary of lending statistics for the academic staff
     */
    public LendingStatistics getAcademicStaffLendingStatistics(int academicStaffId) {
        LendingStatistics stats = new LendingStatistics();

        // Get all courses taught by the academic staff
        List<Course> courses = courseService.getCoursesByInstructor(academicStaffId);

        // Get all lending records for these courses
        for (Course course : courses) {
            List<LendingRecord> records = lendingRecordRepository.getLendingRecordsByCourse(course.getId());        

            for (LendingRecord record : records) {
                if (record.getStatus().equals("Borrowed") || record.getStatus().equals("Returned") || 
                        record.getStatus().equals("Overdue")) {
                    stats.totalLendings++;
                }

                if (record.getReturnDate() == null && record.isOverdue()) {
                    stats.overdueLendings++;
                }

                if (record.getStatus().equals("Borrowed") && record.getReturnDate() == null) {
                    stats.currentLendings++;
                }

                // Add to course-specific statistics
                stats.lendingsPerCourse.put(
                        course.getCourseCode(),
                        stats.lendingsPerCourse.getOrDefault(course.getCourseCode(), 0) + 1
                );
            }
        }

        return stats;
    }

    /**
     * Get system-wide lending statistics
     *
     * @return A summary of system-wide lending statistics
     */
    public LendingStatistics getSystemLendingStatistics() {
        LendingStatistics stats = new LendingStatistics();

        // Get all lending records
        List<LendingRecord> records = lendingRecordRepository.getAllLendingRecords();

        // Update statistics
        stats.totalLendings = records.size();

        for (LendingRecord record : records) {
            if (record.getReturnDate() == null && record.isOverdue()) {
                stats.overdueLendings++;
            }

            if (record.getReturnDate() == null && !record.isOverdue()) {
                stats.currentLendings++;
            }

            // Add to equipment-specific statistics
            String equipmentName = record.getEquipment().getName();
            stats.lendingsPerEquipment.put(
                    equipmentName,
                    stats.lendingsPerEquipment.getOrDefault(equipmentName, 0) + 1
            );

            // Add to borrower-specific statistics
            String borrowerType = record.getBorrower().getRole();
            stats.lendingsPerBorrowerType.put(
                    borrowerType,
                    stats.lendingsPerBorrowerType.getOrDefault(borrowerType, 0) + 1
            );
        }

        return stats;
    }

    /**
     * Get optimized lending statistics for a date range (more efficient implementation)
     *
     * @param startDate The start date of the range
     * @param endDate   The end date of the range
     * @return A map containing the calculated statistics
     */
    public Map<String, Object> getOptimizedLendingStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        return lendingRecordRepository.getLendingStatisticsInDateRange(startDate, endDate);
    }

    /**
     * Convert raw statistics map to LendingStatistics object
     *
     * @param rawStats The raw statistics map from the database
     * @return A LendingStatistics object
     */
    public LendingStatistics convertToLendingStatistics(Map<String, Object> rawStats) {
        LendingStatistics stats = new LendingStatistics();
        
        stats.totalLendings = (int) rawStats.getOrDefault("totalLendings", 0);
        stats.currentLendings = (int) rawStats.getOrDefault("currentLendings", 0);
        stats.overdueLendings = (int) rawStats.getOrDefault("overdueLendings", 0);
        
        @SuppressWarnings("unchecked")
        Map<String, Integer> equipmentStats = (Map<String, Integer>) rawStats.getOrDefault("lendingsPerEquipment", new HashMap<>());
        stats.lendingsPerEquipment.putAll(equipmentStats);
        
        @SuppressWarnings("unchecked")
        Map<String, Integer> borrowerStats = (Map<String, Integer>) rawStats.getOrDefault("lendingsPerBorrowerType", new HashMap<>());
        stats.lendingsPerBorrowerType.putAll(borrowerStats);
        
        return stats;
    }

    /**
     * Get quick count statistics for lending records directly from the database
     * This method uses optimized SQL queries for better performance
     *
     * @param startDate The start date of the range
     * @param endDate   The end date of the range
     * @return A Map containing the counts for total, current and overdue lendings
     */
    public Map<String, Integer> getQuickLendingCounts(LocalDateTime startDate, LocalDateTime endDate) {
        // Delegate to the optimized repository method
        return lendingRecordRepository.getQuickLendingCounts(startDate, endDate);
    }

    /**
     * Validate a lending record
     *
     * @param lendingRecord The lending record to validate
     * @return true if the lending record is valid, false otherwise
     */
    private boolean validateLendingRecord(LendingRecord lendingRecord) {
        // Check if the borrower is valid
        if (lendingRecord.getBorrower() == null) {
            return false;
        }

        // Check if the equipment is valid
        if (lendingRecord.getEquipment() == null) {
            return false;
        }

        // Check if the equipment is available
        if (!lendingRecord.getEquipment().isAvailable()) {
            return false;
        }

        // Check if the borrower is a student
        if (lendingRecord.getBorrower() instanceof Student) {
            // Students must have a course
            if (lendingRecord.getCourse() == null) {
                return false;
            }

            // Check if the student is enrolled in the course
            Student student = (Student) lendingRecord.getBorrower();
            if (!courseService.isStudentEnrolledInCourse(student.getId(), lendingRecord.getCourse().getId())) {
                return false;
            }

            // Check if the borrowing period is valid (max 2 weeks for students)
            if (ChronoUnit.DAYS.between(lendingRecord.getBorrowDate(), lendingRecord.getDueDate()) > 14) {
                return false;
            }
        }

        return true;
    }
    
    /**
     * A simple class to hold lending statistics
     */
    public static class LendingStatistics {
        public int totalLendings;
        public int currentLendings;
        public int overdueLendings;
        public Map<String, Integer> lendingsPerEquipment = new HashMap<>();
        public Map<String, Integer> lendingsPerBorrowerType = new HashMap<>();
        public Map<String, Integer> lendingsPerCourse = new HashMap<>();
    }
}
