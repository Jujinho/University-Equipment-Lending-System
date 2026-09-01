/**
 * @author Group 9
 */
package org.example.db;

import org.example.model.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

/**
 * Repository class for LendingRecord entities.
 * Handles database operations for lending records, including borrowing and returning equipment.
 */
public class LendingRecordRepository {

    private final UserRepository userRepository;
    private final EquipmentRepository equipmentRepository;
    private final CourseRepository courseRepository;

    /**
     * Constructor
     *
     * @param userRepository      The UserRepository to use for retrieving user information
     * @param equipmentRepository The EquipmentRepository to use for retrieving equipment information
     * @param courseRepository    The CourseRepository to use for retrieving course information
     */
    public LendingRecordRepository(UserRepository userRepository, EquipmentRepository equipmentRepository,
                                  CourseRepository courseRepository) {
        this.userRepository = userRepository;
        this.equipmentRepository = equipmentRepository;
        this.courseRepository = courseRepository;
    }

    /**
     * Get a lending record by ID
     *
     * @param id The lending record ID
     * @return An Optional containing the LendingRecord if found, or empty if not found
     */
    public Optional<LendingRecord> getLendingRecordById(int id) {
        String sql = "SELECT lr.*, " +
                "u_borrower.id as borrower_id, u_borrower.username as borrower_username, u_borrower.first_name as borrower_first_name, u_borrower.last_name as borrower_last_name, u_borrower.user_type as borrower_user_type, " +
                "u_approver.id as approver_id, u_approver.username as approver_username, u_approver.first_name as approver_first_name, u_approver.last_name as approver_last_name, u_approver.user_type as approver_user_type, " +
                "e.id as equipment_id, e.name as equipment_name, e.description as equipment_description, e.category as equipment_category, e.condition as equipment_condition, e.available as equipment_available, " +
                "c.id as course_id, c.course_code as course_code, c.course_name as course_name, c.description as course_description " +
                "FROM lending_records lr " +
                "LEFT JOIN users u_borrower ON lr.borrower_id = u_borrower.id " +
                "LEFT JOIN users u_approver ON lr.approver_id = u_approver.id " +
                "LEFT JOIN equipment e ON lr.equipment_id = e.id " +
                "LEFT JOIN courses c ON lr.course_id = c.id " +
                "WHERE lr.id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(extractLendingRecordFromJoinedResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting lending record by ID: " + e.getMessage());
        }

        return Optional.empty();
    }

    /**
     * Get all lending records
     *
     * @return A list of all lending records
     */
    public List<LendingRecord> getAllLendingRecords() {
        List<LendingRecord> lendingRecords = new ArrayList<>();
        String sql = "SELECT lr.*, " +
                "u_borrower.id as borrower_id, u_borrower.username as borrower_username, u_borrower.first_name as borrower_first_name, u_borrower.last_name as borrower_last_name, u_borrower.user_type as borrower_user_type, " +
                "u_approver.id as approver_id, u_approver.username as approver_username, u_approver.first_name as approver_first_name, u_approver.last_name as approver_last_name, u_approver.user_type as approver_user_type, " +
                "e.id as equipment_id, e.name as equipment_name, e.description as equipment_description, e.category as equipment_category, e.condition as equipment_condition, e.available as equipment_available, " +
                "c.id as course_id, c.course_code as course_code, c.course_name as course_name, c.description as course_description " +
                "FROM lending_records lr " +
                "LEFT JOIN users u_borrower ON lr.borrower_id = u_borrower.id " +
                "LEFT JOIN users u_approver ON lr.approver_id = u_approver.id " +
                "LEFT JOIN equipment e ON lr.equipment_id = e.id " +
                "LEFT JOIN courses c ON lr.course_id = c.id " +
                "ORDER BY lr.borrow_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                lendingRecords.add(extractLendingRecordFromJoinedResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all lending records: " + e.getMessage());
        }

        return lendingRecords;
    }

    /**
     * Get lending records by borrower
     *
     * @param borrowerId The ID of the borrower
     * @return A list of lending records for the borrower
     */
    public List<LendingRecord> getLendingRecordsByBorrower(int borrowerId) {
        List<LendingRecord> lendingRecords = new ArrayList<>();
        String sql = "SELECT * FROM lending_records WHERE borrower_id = ? ORDER BY borrow_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, borrowerId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lendingRecords.add(extractLendingRecordFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting lending records by borrower: " + e.getMessage());
        }

        return lendingRecords;
    }

    /**
     * Get lending records by equipment
     *
     * @param equipmentId The ID of the equipment
     * @return A list of lending records for the equipment
     */
    public List<LendingRecord> getLendingRecordsByEquipment(int equipmentId) {
        List<LendingRecord> lendingRecords = new ArrayList<>();
        String sql = "SELECT * FROM lending_records WHERE equipment_id = ? ORDER BY borrow_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, equipmentId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lendingRecords.add(extractLendingRecordFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting lending records by equipment: " + e.getMessage());
        }

        return lendingRecords;
    }

    /**
     * Get lending records by course
     *
     * @param courseId The ID of the course
     * @return A list of lending records for the course
     */
    public List<LendingRecord> getLendingRecordsByCourse(int courseId) {
        List<LendingRecord> lendingRecords = new ArrayList<>();
        String sql = "SELECT lr.*, " +
                "u_borrower.id as borrower_id, u_borrower.username as borrower_username, u_borrower.first_name as borrower_first_name, u_borrower.last_name as borrower_last_name, u_borrower.user_type as borrower_user_type, " +
                "u_approver.id as approver_id, u_approver.username as approver_username, u_approver.first_name as approver_first_name, u_approver.last_name as approver_last_name, u_approver.user_type as approver_user_type, " +
                "e.id as equipment_id, e.name as equipment_name, e.description as equipment_description, e.category as equipment_category, e.condition as equipment_condition, e.available as equipment_available, " +
                "c.id as course_id, c.course_code as course_code, c.course_name as course_name, c.description as course_description " +
                "FROM lending_records lr " +
                "LEFT JOIN users u_borrower ON lr.borrower_id = u_borrower.id " +
                "LEFT JOIN users u_approver ON lr.approver_id = u_approver.id " +
                "LEFT JOIN equipment e ON lr.equipment_id = e.id " +
                "LEFT JOIN courses c ON lr.course_id = c.id " +
                "WHERE lr.course_id = ? " +
                "ORDER BY lr.borrow_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, courseId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lendingRecords.add(extractLendingRecordFromJoinedResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting lending records by course: " + e.getMessage());
        }

        return lendingRecords;
    }

    /**
     * Get lending records by status
     *
     * @param status The status to search for
     * @return A list of lending records with the specified status
     */
    public List<LendingRecord> getLendingRecordsByStatus(String status) {
        List<LendingRecord> lendingRecords = new ArrayList<>();
        String sql = "SELECT * FROM lending_records WHERE status = ? ORDER BY borrow_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lendingRecords.add(extractLendingRecordFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting lending records by status: " + e.getMessage());
        }

        return lendingRecords;
    }

    /**
     * Get overdue lending records
     *
     * @return A list of overdue lending records
     */
    public List<LendingRecord> getOverdueLendingRecords() {
        List<LendingRecord> lendingRecords = new ArrayList<>();
        String sql = "SELECT * FROM lending_records WHERE due_date < CURRENT_TIMESTAMP AND return_date IS NULL " +
                "ORDER BY due_date ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                lendingRecords.add(extractLendingRecordFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting overdue lending records: " + e.getMessage());
        }

        return lendingRecords;
    }

    /**
     * Get lending records by date range
     *
     * @param startDate The start date of the range
     * @param endDate   The end date of the range
     * @return A list of lending records within the date range
     */
    public List<LendingRecord> getLendingRecordsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<LendingRecord> lendingRecords = new ArrayList<>();
        String sql = "SELECT * FROM lending_records WHERE borrow_date BETWEEN ? AND ? ORDER BY borrow_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf(startDate));
            stmt.setTimestamp(2, Timestamp.valueOf(endDate));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lendingRecords.add(extractLendingRecordFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting lending records by date range: " + e.getMessage());
        }

        return lendingRecords;
    }

    /**
     * Create a new lending record
     *
     * @param lendingRecord The lending record to create
     * @return true if the lending record was created successfully, false otherwise
     */
    public boolean createLendingRecord(LendingRecord lendingRecord) {
        String sql = "INSERT INTO lending_records (borrower_id, equipment_id, course_id, borrow_date, due_date, " +
                "status, purpose, condition, notes, approver_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, lendingRecord.getBorrower().getId());
            stmt.setInt(2, lendingRecord.getEquipment().getId());

            if (lendingRecord.getCourse() != null) {
                stmt.setInt(3, lendingRecord.getCourse().getId());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }

            stmt.setTimestamp(4, Timestamp.valueOf(lendingRecord.getBorrowDate()));
            stmt.setTimestamp(5, Timestamp.valueOf(lendingRecord.getDueDate()));
            stmt.setString(6, lendingRecord.getStatus());
            stmt.setString(7, lendingRecord.getPurpose());
            stmt.setString(8, lendingRecord.getCondition());
            stmt.setString(9, lendingRecord.getNotes());

            if (lendingRecord.getApprover() != null) {
                stmt.setInt(10, lendingRecord.getApprover().getId());
            } else {
                stmt.setNull(10, Types.INTEGER);
            }

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating lending record failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int lendingRecordId = generatedKeys.getInt(1);
                    lendingRecord.setId(lendingRecordId);

                    // Update equipment availability
                    updateEquipmentAvailability(lendingRecord.getEquipment().getId(), false);

                    return true;
                } else {
                    throw new SQLException("Creating lending record failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating lending record: " + e.getMessage());
            return false;
        }
    }

    /**
     * Update a lending record
     *
     * @param lendingRecord The lending record to update
     * @return true if the lending record was updated successfully, false otherwise
     */
    public boolean updateLendingRecord(LendingRecord lendingRecord) {
        String sql = "UPDATE lending_records SET borrower_id = ?, equipment_id = ?, course_id = ?, " +
                "borrow_date = ?, due_date = ?, return_date = ?, status = ?, purpose = ?, " +
                "condition = ?, notes = ?, approver_id = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, lendingRecord.getBorrower().getId());
            stmt.setInt(2, lendingRecord.getEquipment().getId());

            if (lendingRecord.getCourse() != null) {
                stmt.setInt(3, lendingRecord.getCourse().getId());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }

            stmt.setTimestamp(4, Timestamp.valueOf(lendingRecord.getBorrowDate()));
            stmt.setTimestamp(5, Timestamp.valueOf(lendingRecord.getDueDate()));

            if (lendingRecord.getReturnDate() != null) {
                stmt.setTimestamp(6, Timestamp.valueOf(lendingRecord.getReturnDate()));
            } else {
                stmt.setNull(6, Types.TIMESTAMP);
            }

            stmt.setString(7, lendingRecord.getStatus());
            stmt.setString(8, lendingRecord.getPurpose());
            stmt.setString(9, lendingRecord.getCondition());
            stmt.setString(10, lendingRecord.getNotes());

            if (lendingRecord.getApprover() != null) {
                stmt.setInt(11, lendingRecord.getApprover().getId());
            } else {
                stmt.setNull(11, Types.INTEGER);
            }

            stmt.setInt(12, lendingRecord.getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating lending record: " + e.getMessage());
            return false;
        }
    }

    /**
     * Delete a lending record
     *
     * @param id The ID of the lending record to delete
     * @return true if the lending record was deleted successfully, false otherwise
     */
    public boolean deleteLendingRecord(int id) {
        // First, get the lending record to update equipment availability
        Optional<LendingRecord> lendingRecordOpt = getLendingRecordById(id);
        if (!lendingRecordOpt.isPresent()) {
            return false;
        }

        LendingRecord lendingRecord = lendingRecordOpt.get();

        String sql = "DELETE FROM lending_records WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                // If the equipment was not returned, make it available again
                if (lendingRecord.getReturnDate() == null) {
                    updateEquipmentAvailability(lendingRecord.getEquipment().getId(), true);
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Error deleting lending record: " + e.getMessage());
            return false;
        }
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
        String sql = "UPDATE lending_records SET return_date = ?, status = 'Returned', " +
                "condition = ?, notes = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf(returnDate));
            stmt.setString(2, condition);
            stmt.setString(3, notes);
            stmt.setInt(4, lendingRecordId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                // Get the equipment ID from the lending record
                Optional<LendingRecord> lendingRecordOpt = getLendingRecordById(lendingRecordId);
                if (lendingRecordOpt.isPresent()) {
                    // Update equipment availability
                    updateEquipmentAvailability(lendingRecordOpt.get().getEquipment().getId(), true);
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Error returning equipment: " + e.getMessage());
            return false;
        }
    }

    /**
     * Approve a lending request
     *
     * @param lendingRecordId The ID of the lending record
     * @param approverId      The ID of the approver
     * @return true if the lending request was approved successfully, false otherwise
     */
    public boolean approveLendingRequest(int lendingRecordId, int approverId) {
        String sql = "UPDATE lending_records SET status = 'Approved', approver_id = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, approverId);
            stmt.setInt(2, lendingRecordId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error approving lending request: " + e.getMessage());
            return false;
        }
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
        String sql = "UPDATE lending_records SET status = 'Rejected', approver_id = ?, notes = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, approverId);
            stmt.setString(2, notes);
            stmt.setInt(3, lendingRecordId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error rejecting lending request: " + e.getMessage());
            return false;
        }
    }

    /**
     * Update equipment availability
     *
     * @param equipmentId The ID of the equipment
     * @param available   Whether the equipment is available
     * @return true if the equipment availability was updated successfully, false otherwise
     */
    private boolean updateEquipmentAvailability(int equipmentId, boolean available) {
        String sql = "UPDATE equipment SET available = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, available);
            stmt.setInt(2, equipmentId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating equipment availability: " + e.getMessage());
            return false;
        }
    }

    /**
     * Extract a lending record from a ResultSet
     *
     * @param rs The ResultSet containing lending record data
     * @return The extracted LendingRecord
     * @throws SQLException If a database access error occurs
     */
    private LendingRecord extractLendingRecordFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int borrowerId = rs.getInt("borrower_id");
        int equipmentId = rs.getInt("equipment_id");
        int courseId = rs.getInt("course_id");
        Timestamp borrowDate = rs.getTimestamp("borrow_date");
        Timestamp dueDate = rs.getTimestamp("due_date");
        Timestamp returnDate = rs.getTimestamp("return_date");
        String status = rs.getString("status");
        String purpose = rs.getString("purpose");
        String condition = rs.getString("condition");
        String notes = rs.getString("notes");
        int approverId = rs.getInt("approver_id");

        // Get the borrower
        User borrower = userRepository.getUserById(borrowerId).orElse(null);

        // Get the equipment
        Equipment equipment = equipmentRepository.getEquipmentById(equipmentId).orElse(null);

        // Get the course if available
        Course course = null;
        if (courseId > 0) {
            course = courseRepository.getCourseById(courseId).orElse(null);
        }

        // Get the approver if available
        User approver = null;
        if (approverId > 0) {
            approver = userRepository.getUserById(approverId).orElse(null);
        }

        LendingRecord lendingRecord;

        if (course != null) {
            // For student borrowers (with course)
            lendingRecord = new LendingRecord(
                    id, borrower, equipment, course,
                    borrowDate.toLocalDateTime(), dueDate.toLocalDateTime(),
                    status, purpose, condition, notes, approver
            );
        } else {
            // For staff borrowers (no course)
            lendingRecord = new LendingRecord(
                    id, borrower, equipment,
                    borrowDate.toLocalDateTime(), dueDate.toLocalDateTime(),
                    status, purpose, condition, notes
            );
        }

        // Set return date if available
        if (returnDate != null) {
            lendingRecord.setReturnDate(returnDate.toLocalDateTime());
        }

        return lendingRecord;
    }

    /**
     * Extract a lending record from a joined ResultSet
     * This method extracts all data from a single query with JOINs to avoid multiple database calls
     *
     * @param rs The ResultSet containing joined lending record data
     * @return The extracted LendingRecord
     * @throws SQLException If a database access error occurs
     */
    private LendingRecord extractLendingRecordFromJoinedResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        Timestamp borrowDate = rs.getTimestamp("borrow_date");
        Timestamp dueDate = rs.getTimestamp("due_date");
        Timestamp returnDate = rs.getTimestamp("return_date");
        String status = rs.getString("status");
        String purpose = rs.getString("purpose");
        String condition = rs.getString("condition");
        String notes = rs.getString("notes");

        // Create borrower from joined data
        int borrowerId = rs.getInt("borrower_id");
        String borrowerUsername = rs.getString("borrower_username");
        String borrowerFirstName = rs.getString("borrower_first_name");
        String borrowerLastName = rs.getString("borrower_last_name");
        String borrowerUserType = rs.getString("borrower_user_type");

        // Create appropriate user subclass based on user_type
        User borrower;
        switch (borrowerUserType) {
            case "Student":
                borrower = new Student(borrowerId, borrowerUsername, "", borrowerFirstName, borrowerLastName, "", "", null, "", "", "", 0);
                break;
            case "AcademicStaff":
                borrower = new AcademicStaff(borrowerId, borrowerUsername, "", borrowerFirstName, borrowerLastName, "", "", null, "", "", "", "");
                break;
            case "ProfessionalStaff":
                borrower = new ProfessionalStaff(borrowerId, borrowerUsername, "", borrowerFirstName, borrowerLastName, "", "", null, "", "", "", "", "");
                break;
            case "Administrator":
                borrower = new Administrator(borrowerId, borrowerUsername, "", borrowerFirstName, borrowerLastName, "", "", null, "", "", "", "", "");
                break;
            default:
                throw new SQLException("Unknown user type: " + borrowerUserType);
        }

        // Create equipment from joined data
        int equipmentId = rs.getInt("equipment_id");
        String equipmentName = rs.getString("equipment_name");
        String equipmentDescription = rs.getString("equipment_description");
        String equipmentCategory = rs.getString("equipment_category");
        String equipmentCondition = rs.getString("equipment_condition");
        boolean equipmentAvailable = rs.getBoolean("equipment_available");

        Equipment equipment = new Equipment(equipmentId, equipmentName, equipmentDescription, equipmentCategory, equipmentCondition, null, 0, "", "", "", "", equipmentAvailable);

        // Create course from joined data if available
        Course course = null;
        int courseId = rs.getInt("course_id");
        if (!rs.wasNull()) {
            String courseCode = rs.getString("course_code");
            String courseName = rs.getString("course_name");
            String courseDescription = rs.getString("course_description");

            course = new Course(courseId, courseCode, courseName, courseDescription, 0, 0, null);
        }

        // Create approver from joined data if available
        User approver = null;
        int approverId = rs.getInt("approver_id");
        if (!rs.wasNull()) {
            String approverUsername = rs.getString("approver_username");
            String approverFirstName = rs.getString("approver_first_name");
            String approverLastName = rs.getString("approver_last_name");
            String approverUserType = rs.getString("approver_user_type");

            // Create appropriate user subclass based on user_type
            switch (approverUserType) {
                case "Student":
                    approver = new Student(approverId, approverUsername, "", approverFirstName, approverLastName, "", "", null, "", "", "", 0);
                    break;
                case "AcademicStaff":
                    approver = new AcademicStaff(approverId, approverUsername, "", approverFirstName, approverLastName, "", "", null, "", "", "", "");
                    break;
                case "ProfessionalStaff":
                    approver = new ProfessionalStaff(approverId, approverUsername, "", approverFirstName, approverLastName, "", "", null, "", "", "", "", "");
                    break;
                case "Administrator":
                    approver = new Administrator(approverId, approverUsername, "", approverFirstName, approverLastName, "", "", null, "", "", "", "", "");
                    break;
                default:
                    throw new SQLException("Unknown user type: " + approverUserType);
            }
        }

        LendingRecord lendingRecord;

        if (course != null) {
            // For student borrowers (with course)
            lendingRecord = new LendingRecord(
                    id, borrower, equipment, course,
                    borrowDate.toLocalDateTime(), dueDate.toLocalDateTime(),
                    status, purpose, condition, notes, approver
            );
        } else {
            // For staff borrowers (no course)
            lendingRecord = new LendingRecord(
                    id, borrower, equipment,
                    borrowDate.toLocalDateTime(), dueDate.toLocalDateTime(),
                    status, purpose, condition, notes
            );
        }

        // Set return date if available
        if (returnDate != null) {
            lendingRecord.setReturnDate(returnDate.toLocalDateTime());
        }

        return lendingRecord;
    }

    /**
     * Get lending statistics within a date range using direct SQL aggregation for better performance
     *
     * @param startDate The start date of the range
     * @param endDate   The end date of the range
     * @return A map containing the calculated statistics
     */
    public Map<String, Object> getLendingStatisticsInDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Object> stats = new HashMap<>();
        Map<String, Integer> lendingsPerEquipment = new HashMap<>();
        Map<String, Integer> lendingsPerBorrowerType = new HashMap<>();
        
        // Initialize counters
        int totalLendings = 0;
        int currentLendings = 0;
        int overdueLendings = 0;
        
        // Get total count and basic stats in one query
        String countSql = "SELECT COUNT(*) as total, " +
                "SUM(CASE WHEN return_date IS NULL AND due_date < CURRENT_TIMESTAMP THEN 1 ELSE 0 END) as overdue, " +
                "SUM(CASE WHEN return_date IS NULL AND due_date >= CURRENT_TIMESTAMP THEN 1 ELSE 0 END) as current " +
                "FROM lending_records WHERE borrow_date BETWEEN ? AND ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(countSql)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(startDate));
            stmt.setTimestamp(2, Timestamp.valueOf(endDate));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    totalLendings = rs.getInt("total");
                    overdueLendings = rs.getInt("overdue");
                    currentLendings = rs.getInt("current");
                }
            }
            
            // Get equipment statistics
            String equipmentSql = "SELECT e.name as equipment_name, COUNT(*) as count " +
                    "FROM lending_records lr " +
                    "JOIN equipment e ON lr.equipment_id = e.id " +
                    "WHERE borrow_date BETWEEN ? AND ? " +
                    "GROUP BY e.name " +
                    "ORDER BY count DESC";
            
            try (PreparedStatement equipStmt = conn.prepareStatement(equipmentSql)) {
                equipStmt.setTimestamp(1, Timestamp.valueOf(startDate));
                equipStmt.setTimestamp(2, Timestamp.valueOf(endDate));
                
                try (ResultSet rs = equipStmt.executeQuery()) {
                    while (rs.next()) {
                        lendingsPerEquipment.put(rs.getString("equipment_name"), rs.getInt("count"));
                    }
                }
            }
            
            // Get borrower type statistics
            String borrowerSql = "SELECT u.user_type as borrower_type, COUNT(*) as count " +
                    "FROM lending_records lr " +
                    "JOIN users u ON lr.borrower_id = u.id " +
                    "WHERE borrow_date BETWEEN ? AND ? " +
                    "GROUP BY u.user_type";
            
            try (PreparedStatement borrowerStmt = conn.prepareStatement(borrowerSql)) {
                borrowerStmt.setTimestamp(1, Timestamp.valueOf(startDate));
                borrowerStmt.setTimestamp(2, Timestamp.valueOf(endDate));
                
                try (ResultSet rs = borrowerStmt.executeQuery()) {
                    while (rs.next()) {
                        lendingsPerBorrowerType.put(rs.getString("borrower_type"), rs.getInt("count"));
                    }
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting lending statistics: " + e.getMessage());
        }
        
        stats.put("totalLendings", totalLendings);
        stats.put("currentLendings", currentLendings);
        stats.put("overdueLendings", overdueLendings);
        stats.put("lendingsPerEquipment", lendingsPerEquipment);
        stats.put("lendingsPerBorrowerType", lendingsPerBorrowerType);
        
        return stats;
    }

    /**
     * Get quick count statistics for lending records
     * This method uses optimized SQL queries for better performance
     *
     * @param startDate The start date of the range
     * @param endDate   The end date of the range
     * @return A Map containing the counts for total, current and overdue lendings
     */
    public Map<String, Integer> getQuickLendingCounts(LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Integer> counts = new HashMap<>();
        counts.put("total", 0);
        counts.put("current", 0);
        counts.put("overdue", 0);
        
        String sql = "SELECT COUNT(*) as total, " +
                "SUM(CASE WHEN return_date IS NULL AND due_date < CURRENT_TIMESTAMP THEN 1 ELSE 0 END) as overdue, " +
                "SUM(CASE WHEN return_date IS NULL AND due_date >= CURRENT_TIMESTAMP THEN 1 ELSE 0 END) as current " +
                "FROM lending_records WHERE borrow_date BETWEEN ? AND ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(startDate));
            stmt.setTimestamp(2, Timestamp.valueOf(endDate));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    counts.put("total", rs.getInt("total"));
                    counts.put("overdue", rs.getInt("overdue"));
                    counts.put("current", rs.getInt("current"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting quick lending counts: " + e.getMessage());
        }
        
        return counts;
    }
}
