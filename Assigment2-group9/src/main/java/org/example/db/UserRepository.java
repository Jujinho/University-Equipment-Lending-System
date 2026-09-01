/**
 * @author Group 9
 */
package org.example.db;

import org.example.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository class for User entities.
 * Handles database operations for users (including students, academic staff, professional staff, and administrators).
 */
public class UserRepository {

    /**
     * Authenticate a user by username and password
     * This optimized version uses a LEFT JOIN to fetch all user data in a single query
     * and verifies the password using the PasswordHasher
     *
     * @param username The username
     * @param password The password
     * @return An Optional containing the authenticated User if successful, or empty if authentication fails
     */
    public Optional<User> authenticate(String username, String password) {
        String sql = "SELECT u.*, " +
                "s.student_id, s.major, s.year, " +
                "a_staff.staff_id as academic_staff_id, a_staff.department as academic_department, a_staff.position as academic_position, " +
                "p_staff.staff_id as professional_staff_id, p_staff.department as professional_department, " +
                "p_staff.position as professional_position, p_staff.specialization, " +
                "admin.admin_id, admin.department as admin_department, admin.position as admin_position, admin.access_level " +
                "FROM users u " +
                "LEFT JOIN students s ON u.id = s.user_id AND u.user_type = 'Student' " +
                "LEFT JOIN academic_staff a_staff ON u.id = a_staff.user_id AND u.user_type = 'AcademicStaff' " +
                "LEFT JOIN professional_staff p_staff ON u.id = p_staff.user_id AND u.user_type = 'ProfessionalStaff' " +
                "LEFT JOIN administrators admin ON u.id = admin.user_id AND u.user_type = 'Administrator' " +
                "WHERE u.username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedPassword = rs.getString("password");

                    // Check if the password is already hashed (contains the delimiter)
                    boolean isPasswordHashed = storedPassword.contains("$");

                    // Verify the password
                    boolean passwordMatches;
                    if (isPasswordHashed) {
                        // Verify using PasswordHasher
                        passwordMatches = org.example.util.PasswordHasher.verifyPassword(password, storedPassword);
                    } else {
                        // Legacy plain text comparison (for backward compatibility)
                        passwordMatches = password.equals(storedPassword);
                    }

                    if (passwordMatches) {
                        String userType = rs.getString("user_type");
                        switch (userType) {
                            case "Student":
                                return Optional.of(extractStudentFromResultSet(rs));
                            case "AcademicStaff":
                                return Optional.of(extractAcademicStaffFromResultSet(rs));
                            case "ProfessionalStaff":
                                return Optional.of(extractProfessionalStaffFromResultSet(rs));
                            case "Administrator":
                                return Optional.of(extractAdministratorFromResultSet(rs));
                            default:
                                return Optional.empty();
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error authenticating user: " + e.getMessage());
        }

        return Optional.empty();
    }

    /**
     * Get a user by ID
     * This optimized version uses a LEFT JOIN to fetch all user data in a single query
     *
     * @param id The user ID
     * @return An Optional containing the User if found, or empty if not found
     */
    public Optional<User> getUserById(int id) {
        String sql = "SELECT u.*, " +
                "s.student_id, s.major, s.year, " +
                "a_staff.staff_id as academic_staff_id, a_staff.department as academic_department, a_staff.position as academic_position, " +
                "p_staff.staff_id as professional_staff_id, p_staff.department as professional_department, " +
                "p_staff.position as professional_position, p_staff.specialization, " +
                "admin.admin_id, admin.department as admin_department, admin.position as admin_position, admin.access_level " +
                "FROM users u " +
                "LEFT JOIN students s ON u.id = s.user_id AND u.user_type = 'Student' " +
                "LEFT JOIN academic_staff a_staff ON u.id = a_staff.user_id AND u.user_type = 'AcademicStaff' " +
                "LEFT JOIN professional_staff p_staff ON u.id = p_staff.user_id AND u.user_type = 'ProfessionalStaff' " +
                "LEFT JOIN administrators admin ON u.id = admin.user_id AND u.user_type = 'Administrator' " +
                "WHERE u.id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String userType = rs.getString("user_type");
                    switch (userType) {
                        case "Student":
                            return Optional.of(extractStudentFromResultSet(rs));
                        case "AcademicStaff":
                            return Optional.of(extractAcademicStaffFromResultSet(rs));
                        case "ProfessionalStaff":
                            return Optional.of(extractProfessionalStaffFromResultSet(rs));
                        case "Administrator":
                            return Optional.of(extractAdministratorFromResultSet(rs));
                        default:
                            return Optional.empty();
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting user by ID: " + e.getMessage());
        }

        return Optional.empty();
    }

    /**
     * Get a user by username
     * This optimized version uses a LEFT JOIN to fetch all user data in a single query
     *
     * @param username The username
     * @return An Optional containing the User if found, or empty if not found
     */
    public Optional<User> getUserByUsername(String username) {
        String sql = "SELECT u.*, " +
                "s.student_id, s.major, s.year, " +
                "a_staff.staff_id as academic_staff_id, a_staff.department as academic_department, a_staff.position as academic_position, " +
                "p_staff.staff_id as professional_staff_id, p_staff.department as professional_department, " +
                "p_staff.position as professional_position, p_staff.specialization, " +
                "admin.admin_id, admin.department as admin_department, admin.position as admin_position, admin.access_level " +
                "FROM users u " +
                "LEFT JOIN students s ON u.id = s.user_id AND u.user_type = 'Student' " +
                "LEFT JOIN academic_staff a_staff ON u.id = a_staff.user_id AND u.user_type = 'AcademicStaff' " +
                "LEFT JOIN professional_staff p_staff ON u.id = p_staff.user_id AND u.user_type = 'ProfessionalStaff' " +
                "LEFT JOIN administrators admin ON u.id = admin.user_id AND u.user_type = 'Administrator' " +
                "WHERE u.username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String userType = rs.getString("user_type");
                    switch (userType) {
                        case "Student":
                            return Optional.of(extractStudentFromResultSet(rs));
                        case "AcademicStaff":
                            return Optional.of(extractAcademicStaffFromResultSet(rs));
                        case "ProfessionalStaff":
                            return Optional.of(extractProfessionalStaffFromResultSet(rs));
                        case "Administrator":
                            return Optional.of(extractAdministratorFromResultSet(rs));
                        default:
                            return Optional.empty();
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting user by username: " + e.getMessage());
        }

        return Optional.empty();
    }

    /**
     * Get all users
     *
     * @return A list of all users
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                getUserByType(rs).ifPresent(users::add);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all users: " + e.getMessage());
        }

        return users;
    }

    /**
     * Get all students
     *
     * @return A list of all students
     */
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT u.*, s.student_id, s.major, s.year " +
                "FROM users u " +
                "JOIN students s ON u.id = s.user_id " +
                "WHERE u.user_type = 'Student'";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                students.add(extractStudentFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all students: " + e.getMessage());
        }

        return students;
    }

    /**
     * Get all academic staff
     *
     * @return A list of all academic staff
     */
    public List<AcademicStaff> getAllAcademicStaff() {
        List<AcademicStaff> academicStaff = new ArrayList<>();
        String sql = "SELECT u.*, a.staff_id as academic_staff_id, a.department as academic_department, a.position as academic_position " +
                "FROM users u " +
                "JOIN academic_staff a ON u.id = a.user_id " +
                "WHERE u.user_type = 'AcademicStaff'";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                academicStaff.add(extractAcademicStaffFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all academic staff: " + e.getMessage());
        }

        return academicStaff;
    }

    /**
     * Get all professional staff
     *
     * @return A list of all professional staff
     */
    public List<ProfessionalStaff> getAllProfessionalStaff() {
        List<ProfessionalStaff> professionalStaff = new ArrayList<>();
        String sql = "SELECT u.*, p.staff_id as professional_staff_id, p.department as professional_department, p.position as professional_position, p.specialization " +
                "FROM users u " +
                "JOIN professional_staff p ON u.id = p.user_id " +
                "WHERE u.user_type = 'ProfessionalStaff'";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                professionalStaff.add(extractProfessionalStaffFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all professional staff: " + e.getMessage());
        }

        return professionalStaff;
    }

    /**
     * Get all administrators
     *
     * @return A list of all administrators
     */
    public List<Administrator> getAllAdministrators() {
        List<Administrator> administrators = new ArrayList<>();
        String sql = "SELECT u.*, a.admin_id, a.department as admin_department, a.position as admin_position, a.access_level " +
                "FROM users u " +
                "JOIN administrators a ON u.id = a.user_id " +
                "WHERE u.user_type = 'Administrator'";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                administrators.add(extractAdministratorFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all administrators: " + e.getMessage());
        }

        return administrators;
    }

    /**
     * Create a new user
     *
     * @param user The user to create
     * @return true if the user was created successfully, false otherwise
     */
    public boolean createUser(User user) {
        if (user instanceof Student) {
            return createStudent((Student) user);
        } else if (user instanceof AcademicStaff) {
            return createAcademicStaff((AcademicStaff) user);
        } else if (user instanceof ProfessionalStaff) {
            return createProfessionalStaff((ProfessionalStaff) user);
        } else if (user instanceof Administrator) {
            return createAdministrator((Administrator) user);
        }
        return false;
    }

    /**
     * Update a user
     *
     * @param user The user to update
     * @return true if the user was updated successfully, false otherwise
     */
    public boolean updateUser(User user) {
        if (user instanceof Student) {
            return updateStudent((Student) user);
        } else if (user instanceof AcademicStaff) {
            return updateAcademicStaff((AcademicStaff) user);
        } else if (user instanceof ProfessionalStaff) {
            return updateProfessionalStaff((ProfessionalStaff) user);
        } else if (user instanceof Administrator) {
            return updateAdministrator((Administrator) user);
        }
        return false;
    }

    /**
     * Delete a user
     *
     * @param id The ID of the user to delete
     * @return true if the user was deleted successfully, false otherwise
     */
    public boolean deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }

    // Helper methods

    /**
     * Get a user by type from a ResultSet
     * This optimized version avoids making additional database queries by using JOIN in the original query
     *
     * @param rs The ResultSet containing user data
     * @return An Optional containing the User if found, or empty if not found
     * @throws SQLException If a database access error occurs
     */
    private Optional<User> getUserByType(ResultSet rs) throws SQLException {
        String userType = rs.getString("user_type");

        try {
            switch (userType) {
                case "Student":
                    // Check if student-specific columns exist in the result set
                    try {
                        rs.findColumn("student_id");
                        return Optional.of(extractStudentFromResultSet(rs));
                    } catch (SQLException e) {
                        // If columns don't exist, fall back to separate query
                        return Optional.ofNullable(getStudentById(rs.getInt("id")));
                    }
                case "AcademicStaff":
                    try {
                        rs.findColumn("academic_staff_id");
                        return Optional.of(extractAcademicStaffFromResultSet(rs));
                    } catch (SQLException e) {
                        return Optional.ofNullable(getAcademicStaffById(rs.getInt("id")));
                    }
                case "ProfessionalStaff":
                    try {
                        rs.findColumn("professional_staff_id");
                        rs.findColumn("specialization");
                        return Optional.of(extractProfessionalStaffFromResultSet(rs));
                    } catch (SQLException e) {
                        return Optional.ofNullable(getProfessionalStaffById(rs.getInt("id")));
                    }
                case "Administrator":
                    try {
                        rs.findColumn("admin_id");
                        return Optional.of(extractAdministratorFromResultSet(rs));
                    } catch (SQLException e) {
                        return Optional.ofNullable(getAdministratorById(rs.getInt("id")));
                    }
                default:
                    return Optional.empty();
            }
        } catch (SQLException e) {
            System.err.println("Error extracting user from ResultSet: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Get a student by ID
     *
     * @param id The student ID
     * @return The Student if found, or null if not found
     */
    private Student getStudentById(int id) {
        String sql = "SELECT u.*, s.student_id, s.major, s.year " +
                "FROM users u " +
                "JOIN students s ON u.id = s.user_id " +
                "WHERE u.id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractStudentFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting student by ID: " + e.getMessage());
        }

        return null;
    }

    /**
     * Get an academic staff by ID
     *
     * @param id The academic staff ID
     * @return The AcademicStaff if found, or null if not found
     */
    private AcademicStaff getAcademicStaffById(int id) {
        String sql = "SELECT u.*, a.staff_id as academic_staff_id, a.department as academic_department, a.position as academic_position " +
                "FROM users u " +
                "JOIN academic_staff a ON u.id = a.user_id " +
                "WHERE u.id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractAcademicStaffFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting academic staff by ID: " + e.getMessage());
        }

        return null;
    }

    /**
     * Get a professional staff by ID
     *
     * @param id The professional staff ID
     * @return The ProfessionalStaff if found, or null if not found
     */
    private ProfessionalStaff getProfessionalStaffById(int id) {
        String sql = "SELECT u.*, p.staff_id as professional_staff_id, p.department as professional_department, p.position as professional_position, p.specialization " +
                "FROM users u " +
                "JOIN professional_staff p ON u.id = p.user_id " +
                "WHERE u.id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractProfessionalStaffFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting professional staff by ID: " + e.getMessage());
        }

        return null;
    }

    /**
     * Get an administrator by ID
     *
     * @param id The administrator ID
     * @return The Administrator if found, or null if not found
     */
    private Administrator getAdministratorById(int id) {
        String sql = "SELECT u.*, a.admin_id, a.department as admin_department, a.position as admin_position, a.access_level " +
                "FROM users u " +
                "JOIN administrators a ON u.id = a.user_id " +
                "WHERE u.id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractAdministratorFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting administrator by ID: " + e.getMessage());
        }

        return null;
    }

    /**
     * Extract a student from a ResultSet
     *
     * @param rs The ResultSet containing student data
     * @return The extracted Student
     * @throws SQLException If a database access error occurs
     */
    private Student extractStudentFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String username = rs.getString("username");
        String password = rs.getString("password");
        String firstName = rs.getString("first_name");
        String lastName = rs.getString("last_name");
        String email = rs.getString("email");
        String phoneNumber = rs.getString("phone_number");
        Date dateOfBirth = rs.getDate("date_of_birth");
        String address = rs.getString("address");
        String studentId = rs.getString("student_id");
        String major = rs.getString("major");
        int year = rs.getInt("year");

        Student student = new Student(
                id, username, password, firstName, lastName, email, phoneNumber,
                dateOfBirth != null ? dateOfBirth.toLocalDate() : null, address,
                studentId, major, year
        );

        return student;
    }

    /**
     * Extract an academic staff from a ResultSet
     *
     * @param rs The ResultSet containing academic staff data
     * @return The extracted AcademicStaff
     * @throws SQLException If a database access error occurs
     */
    private AcademicStaff extractAcademicStaffFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String username = rs.getString("username");
        String password = rs.getString("password");
        String firstName = rs.getString("first_name");
        String lastName = rs.getString("last_name");
        String email = rs.getString("email");
        String phoneNumber = rs.getString("phone_number");
        Date dateOfBirth = rs.getDate("date_of_birth");
        String address = rs.getString("address");
        String staffId = rs.getString("academic_staff_id");
        String department = rs.getString("academic_department");
        String position = rs.getString("academic_position");

        AcademicStaff academicStaff = new AcademicStaff(
                id, username, password, firstName, lastName, email, phoneNumber,
                dateOfBirth != null ? dateOfBirth.toLocalDate() : null, address,
                staffId, department, position
        );

        return academicStaff;
    }

    /**
     * Extract a professional staff from a ResultSet
     *
     * @param rs The ResultSet containing professional staff data
     * @return The extracted ProfessionalStaff
     * @throws SQLException If a database access error occurs
     */
    private ProfessionalStaff extractProfessionalStaffFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String username = rs.getString("username");
        String password = rs.getString("password");
        String firstName = rs.getString("first_name");
        String lastName = rs.getString("last_name");
        String email = rs.getString("email");
        String phoneNumber = rs.getString("phone_number");
        Date dateOfBirth = rs.getDate("date_of_birth");
        String address = rs.getString("address");
        String staffId = rs.getString("professional_staff_id");
        String department = rs.getString("professional_department");
        String position = rs.getString("professional_position");
        String specialization = rs.getString("specialization");

        ProfessionalStaff professionalStaff = new ProfessionalStaff(
                id, username, password, firstName, lastName, email, phoneNumber,
                dateOfBirth != null ? dateOfBirth.toLocalDate() : null, address,
                staffId, department, position, specialization
        );

        return professionalStaff;
    }

    /**
     * Extract an administrator from a ResultSet
     *
     * @param rs The ResultSet containing administrator data
     * @return The extracted Administrator
     * @throws SQLException If a database access error occurs
     */
    private Administrator extractAdministratorFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String username = rs.getString("username");
        String password = rs.getString("password");
        String firstName = rs.getString("first_name");
        String lastName = rs.getString("last_name");
        String email = rs.getString("email");
        String phoneNumber = rs.getString("phone_number");
        Date dateOfBirth = rs.getDate("date_of_birth");
        String address = rs.getString("address");
        String adminId = rs.getString("admin_id");
        String department = rs.getString("admin_department");
        String position = rs.getString("admin_position");
        String accessLevel = rs.getString("access_level");

        Administrator administrator = new Administrator(
                id, username, password, firstName, lastName, email, phoneNumber,
                dateOfBirth != null ? dateOfBirth.toLocalDate() : null, address,
                adminId, department, position, accessLevel
        );

        return administrator;
    }

    // Placeholder methods for CRUD operations
    private boolean createStudent(Student student) {
        // First insert the base user record
        String userSql = "INSERT INTO users (username, password, first_name, last_name, email, phone_number, date_of_birth, address, user_type) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'Student')";
        
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Insert into users table and get the generated ID
            int userId;
            try (PreparedStatement userStmt = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
                userStmt.setString(1, student.getUsername());
                userStmt.setString(2, student.getPassword());
                userStmt.setString(3, student.getFirstName());
                userStmt.setString(4, student.getLastName());
                userStmt.setString(5, student.getEmail());
                userStmt.setString(6, student.getPhoneNumber());
                
                if (student.getDateOfBirth() != null) {
                    userStmt.setDate(7, Date.valueOf(student.getDateOfBirth()));
                } else {
                    userStmt.setNull(7, Types.DATE);
                }
                
                userStmt.setString(8, student.getAddress());
                
                int userRowsAffected = userStmt.executeUpdate();
                if (userRowsAffected == 0) {
                    throw new SQLException("Creating user failed, no rows affected.");
                }
                
                // Get the generated user ID
                try (ResultSet generatedKeys = userStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        userId = generatedKeys.getInt(1);
                        student.setId(userId); // Update the student object with the new ID
                    } else {
                        throw new SQLException("Creating user failed, no ID obtained.");
                    }
                }
            }
            
            // Now insert into students table with the user ID
            String studentSql = "INSERT INTO students (user_id, student_id, major, year) VALUES (?, ?, ?, ?)";
            try (PreparedStatement studentStmt = conn.prepareStatement(studentSql)) {
                studentStmt.setInt(1, userId);
                studentStmt.setString(2, student.getStudentId());
                studentStmt.setString(3, student.getMajor());
                studentStmt.setInt(4, student.getYear());
                
                int studentRowsAffected = studentStmt.executeUpdate();
                if (studentRowsAffected == 0) {
                    throw new SQLException("Creating student failed, no rows affected.");
                }
            }
            
            conn.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Error creating student: " + e.getMessage());
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException rollbackEx) {
                System.err.println("Error rolling back transaction: " + rollbackEx.getMessage());
            }
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
    
    private boolean createAcademicStaff(AcademicStaff academicStaff) {
        // First insert the base user record
        String userSql = "INSERT INTO users (username, password, first_name, last_name, email, phone_number, date_of_birth, address, user_type) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'AcademicStaff')";
        
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Insert into users table and get the generated ID
            int userId;
            try (PreparedStatement userStmt = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
                userStmt.setString(1, academicStaff.getUsername());
                userStmt.setString(2, academicStaff.getPassword());
                userStmt.setString(3, academicStaff.getFirstName());
                userStmt.setString(4, academicStaff.getLastName());
                userStmt.setString(5, academicStaff.getEmail());
                userStmt.setString(6, academicStaff.getPhoneNumber());
                
                if (academicStaff.getDateOfBirth() != null) {
                    userStmt.setDate(7, Date.valueOf(academicStaff.getDateOfBirth()));
                } else {
                    userStmt.setNull(7, Types.DATE);
                }
                
                userStmt.setString(8, academicStaff.getAddress());
                
                int userRowsAffected = userStmt.executeUpdate();
                if (userRowsAffected == 0) {
                    throw new SQLException("Creating user failed, no rows affected.");
                }
                
                // Get the generated user ID
                try (ResultSet generatedKeys = userStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        userId = generatedKeys.getInt(1);
                        academicStaff.setId(userId); // Update the academicStaff object with the new ID
                    } else {
                        throw new SQLException("Creating user failed, no ID obtained.");
                    }
                }
            }
            
            // Now insert into academic_staff table with the user ID
            String staffSql = "INSERT INTO academic_staff (user_id, staff_id, department, position) VALUES (?, ?, ?, ?)";
            try (PreparedStatement staffStmt = conn.prepareStatement(staffSql)) {
                staffStmt.setInt(1, userId);
                staffStmt.setString(2, academicStaff.getStaffId());
                staffStmt.setString(3, academicStaff.getDepartment());
                staffStmt.setString(4, academicStaff.getPosition());
                
                int staffRowsAffected = staffStmt.executeUpdate();
                if (staffRowsAffected == 0) {
                    throw new SQLException("Creating academic staff failed, no rows affected.");
                }
            }
            
            conn.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Error creating academic staff: " + e.getMessage());
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException rollbackEx) {
                System.err.println("Error rolling back transaction: " + rollbackEx.getMessage());
            }
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
    
    private boolean createProfessionalStaff(ProfessionalStaff professionalStaff) {
        // First insert the base user record
        String userSql = "INSERT INTO users (username, password, first_name, last_name, email, phone_number, date_of_birth, address, user_type) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'ProfessionalStaff')";
        
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Insert into users table and get the generated ID
            int userId;
            try (PreparedStatement userStmt = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
                userStmt.setString(1, professionalStaff.getUsername());
                userStmt.setString(2, professionalStaff.getPassword());
                userStmt.setString(3, professionalStaff.getFirstName());
                userStmt.setString(4, professionalStaff.getLastName());
                userStmt.setString(5, professionalStaff.getEmail());
                userStmt.setString(6, professionalStaff.getPhoneNumber());
                
                if (professionalStaff.getDateOfBirth() != null) {
                    userStmt.setDate(7, Date.valueOf(professionalStaff.getDateOfBirth()));
                } else {
                    userStmt.setNull(7, Types.DATE);
                }
                
                userStmt.setString(8, professionalStaff.getAddress());
                
                int userRowsAffected = userStmt.executeUpdate();
                if (userRowsAffected == 0) {
                    throw new SQLException("Creating user failed, no rows affected.");
                }
                
                // Get the generated user ID
                try (ResultSet generatedKeys = userStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        userId = generatedKeys.getInt(1);
                        professionalStaff.setId(userId); // Update the professionalStaff object with the new ID
                    } else {
                        throw new SQLException("Creating user failed, no ID obtained.");
                    }
                }
            }
            
            // Now insert into professional_staff table with the user ID
            String staffSql = "INSERT INTO professional_staff (user_id, staff_id, department, position, specialization) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement staffStmt = conn.prepareStatement(staffSql)) {
                staffStmt.setInt(1, userId);
                staffStmt.setString(2, professionalStaff.getStaffId());
                staffStmt.setString(3, professionalStaff.getDepartment());
                staffStmt.setString(4, professionalStaff.getPosition());
                staffStmt.setString(5, professionalStaff.getSpecialization());
                
                int staffRowsAffected = staffStmt.executeUpdate();
                if (staffRowsAffected == 0) {
                    throw new SQLException("Creating professional staff failed, no rows affected.");
                }
            }
            
            conn.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Error creating professional staff: " + e.getMessage());
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException rollbackEx) {
                System.err.println("Error rolling back transaction: " + rollbackEx.getMessage());
            }
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
    
    private boolean createAdministrator(Administrator administrator) {
        // First insert the base user record
        String userSql = "INSERT INTO users (username, password, first_name, last_name, email, phone_number, date_of_birth, address, user_type) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'Administrator')";
        
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Insert into users table and get the generated ID
            int userId;
            try (PreparedStatement userStmt = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
                userStmt.setString(1, administrator.getUsername());
                userStmt.setString(2, administrator.getPassword());
                userStmt.setString(3, administrator.getFirstName());
                userStmt.setString(4, administrator.getLastName());
                userStmt.setString(5, administrator.getEmail());
                userStmt.setString(6, administrator.getPhoneNumber());
                
                if (administrator.getDateOfBirth() != null) {
                    userStmt.setDate(7, Date.valueOf(administrator.getDateOfBirth()));
                } else {
                    userStmt.setNull(7, Types.DATE);
                }
                
                userStmt.setString(8, administrator.getAddress());
                
                int userRowsAffected = userStmt.executeUpdate();
                if (userRowsAffected == 0) {
                    throw new SQLException("Creating user failed, no rows affected.");
                }
                
                // Get the generated user ID
                try (ResultSet generatedKeys = userStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        userId = generatedKeys.getInt(1);
                        administrator.setId(userId); // Update the administrator object with the new ID
                    } else {
                        throw new SQLException("Creating user failed, no ID obtained.");
                    }
                }
            }
            
            // Now insert into administrators table with the user ID
            String adminSql = "INSERT INTO administrators (user_id, admin_id, department, position, access_level) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement adminStmt = conn.prepareStatement(adminSql)) {
                adminStmt.setInt(1, userId);
                adminStmt.setString(2, administrator.getAdminId());
                adminStmt.setString(3, administrator.getDepartment());
                adminStmt.setString(4, administrator.getPosition());
                adminStmt.setString(5, administrator.getAccessLevel());
                
                int adminRowsAffected = adminStmt.executeUpdate();
                if (adminRowsAffected == 0) {
                    throw new SQLException("Creating administrator failed, no rows affected.");
                }
            }
            
            conn.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Error creating administrator: " + e.getMessage());
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException rollbackEx) {
                System.err.println("Error rolling back transaction: " + rollbackEx.getMessage());
            }
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    private boolean updateStudent(Student student) {
        // First update the users table
        String userSql = "UPDATE users SET username = ?, password = ?, first_name = ?, last_name = ?, " +
                "email = ?, phone_number = ?, date_of_birth = ?, address = ? WHERE id = ?";
        
        // Then update the students table
        String studentSql = "UPDATE students SET student_id = ?, major = ?, year = ? WHERE user_id = ?";
        
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Update users table
            try (PreparedStatement userStmt = conn.prepareStatement(userSql)) {
                userStmt.setString(1, student.getUsername());
                userStmt.setString(2, student.getPassword());
                userStmt.setString(3, student.getFirstName());
                userStmt.setString(4, student.getLastName());
                userStmt.setString(5, student.getEmail());
                userStmt.setString(6, student.getPhoneNumber());
                
                if (student.getDateOfBirth() != null) {
                    userStmt.setDate(7, Date.valueOf(student.getDateOfBirth()));
                } else {
                    userStmt.setNull(7, Types.DATE);
                }
                
                userStmt.setString(8, student.getAddress());
                userStmt.setInt(9, student.getId());
                
                int userRowsAffected = userStmt.executeUpdate();
                if (userRowsAffected == 0) {
                    throw new SQLException("Updating user failed, no rows affected.");
                }
            }
            
            // Update students table
            try (PreparedStatement studentStmt = conn.prepareStatement(studentSql)) {
                studentStmt.setString(1, student.getStudentId());
                studentStmt.setString(2, student.getMajor());
                studentStmt.setInt(3, student.getYear());
                studentStmt.setInt(4, student.getId());
                
                int studentRowsAffected = studentStmt.executeUpdate();
                if (studentRowsAffected == 0) {
                    throw new SQLException("Updating student failed, no rows affected.");
                }
            }
            
            conn.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Error updating student: " + e.getMessage());
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException rollbackEx) {
                System.err.println("Error rolling back transaction: " + rollbackEx.getMessage());
            }
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
    
    private boolean updateAcademicStaff(AcademicStaff academicStaff) {
        // First update the users table
        String userSql = "UPDATE users SET username = ?, password = ?, first_name = ?, last_name = ?, " +
                "email = ?, phone_number = ?, date_of_birth = ?, address = ? WHERE id = ?";
        
        // Then update the academic_staff table
        String staffSql = "UPDATE academic_staff SET staff_id = ?, department = ?, position = ? WHERE user_id = ?";
        
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Update users table
            try (PreparedStatement userStmt = conn.prepareStatement(userSql)) {
                userStmt.setString(1, academicStaff.getUsername());
                userStmt.setString(2, academicStaff.getPassword());
                userStmt.setString(3, academicStaff.getFirstName());
                userStmt.setString(4, academicStaff.getLastName());
                userStmt.setString(5, academicStaff.getEmail());
                userStmt.setString(6, academicStaff.getPhoneNumber());
                
                if (academicStaff.getDateOfBirth() != null) {
                    userStmt.setDate(7, Date.valueOf(academicStaff.getDateOfBirth()));
                } else {
                    userStmt.setNull(7, Types.DATE);
                }
                
                userStmt.setString(8, academicStaff.getAddress());
                userStmt.setInt(9, academicStaff.getId());
                
                int userRowsAffected = userStmt.executeUpdate();
                if (userRowsAffected == 0) {
                    throw new SQLException("Updating user failed, no rows affected.");
                }
            }
            
            // Update academic_staff table
            try (PreparedStatement staffStmt = conn.prepareStatement(staffSql)) {
                staffStmt.setString(1, academicStaff.getStaffId());
                staffStmt.setString(2, academicStaff.getDepartment());
                staffStmt.setString(3, academicStaff.getPosition());
                staffStmt.setInt(4, academicStaff.getId());
                
                int staffRowsAffected = staffStmt.executeUpdate();
                if (staffRowsAffected == 0) {
                    throw new SQLException("Updating academic staff failed, no rows affected.");
                }
            }
            
            conn.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Error updating academic staff: " + e.getMessage());
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException rollbackEx) {
                System.err.println("Error rolling back transaction: " + rollbackEx.getMessage());
            }
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
    
    private boolean updateProfessionalStaff(ProfessionalStaff professionalStaff) {
        // First update the users table
        String userSql = "UPDATE users SET username = ?, password = ?, first_name = ?, last_name = ?, " +
                "email = ?, phone_number = ?, date_of_birth = ?, address = ? WHERE id = ?";
        
        // Then update the professional_staff table
        String staffSql = "UPDATE professional_staff SET staff_id = ?, department = ?, position = ?, " +
                "specialization = ? WHERE user_id = ?";
        
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Update users table
            try (PreparedStatement userStmt = conn.prepareStatement(userSql)) {
                userStmt.setString(1, professionalStaff.getUsername());
                userStmt.setString(2, professionalStaff.getPassword());
                userStmt.setString(3, professionalStaff.getFirstName());
                userStmt.setString(4, professionalStaff.getLastName());
                userStmt.setString(5, professionalStaff.getEmail());
                userStmt.setString(6, professionalStaff.getPhoneNumber());
                
                if (professionalStaff.getDateOfBirth() != null) {
                    userStmt.setDate(7, Date.valueOf(professionalStaff.getDateOfBirth()));
                } else {
                    userStmt.setNull(7, Types.DATE);
                }
                
                userStmt.setString(8, professionalStaff.getAddress());
                userStmt.setInt(9, professionalStaff.getId());
                
                int userRowsAffected = userStmt.executeUpdate();
                if (userRowsAffected == 0) {
                    throw new SQLException("Updating user failed, no rows affected.");
                }
            }
            
            // Update professional_staff table
            try (PreparedStatement staffStmt = conn.prepareStatement(staffSql)) {
                staffStmt.setString(1, professionalStaff.getStaffId());
                staffStmt.setString(2, professionalStaff.getDepartment());
                staffStmt.setString(3, professionalStaff.getPosition());
                staffStmt.setString(4, professionalStaff.getSpecialization());
                staffStmt.setInt(5, professionalStaff.getId());
                
                int staffRowsAffected = staffStmt.executeUpdate();
                if (staffRowsAffected == 0) {
                    throw new SQLException("Updating professional staff failed, no rows affected.");
                }
            }
            
            conn.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Error updating professional staff: " + e.getMessage());
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException rollbackEx) {
                System.err.println("Error rolling back transaction: " + rollbackEx.getMessage());
            }
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
    
    private boolean updateAdministrator(Administrator administrator) {
        // First update the users table
        String userSql = "UPDATE users SET username = ?, password = ?, first_name = ?, last_name = ?, " +
                "email = ?, phone_number = ?, date_of_birth = ?, address = ? WHERE id = ?";
        
        // Then update the administrators table
        String adminSql = "UPDATE administrators SET admin_id = ?, department = ?, position = ?, access_level = ? WHERE user_id = ?";
        
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Update users table
            try (PreparedStatement userStmt = conn.prepareStatement(userSql)) {
                userStmt.setString(1, administrator.getUsername());
                userStmt.setString(2, administrator.getPassword());
                userStmt.setString(3, administrator.getFirstName());
                userStmt.setString(4, administrator.getLastName());
                userStmt.setString(5, administrator.getEmail());
                userStmt.setString(6, administrator.getPhoneNumber());
                
                if (administrator.getDateOfBirth() != null) {
                    userStmt.setDate(7, Date.valueOf(administrator.getDateOfBirth()));
                } else {
                    userStmt.setNull(7, Types.DATE);
                }
                
                userStmt.setString(8, administrator.getAddress());
                userStmt.setInt(9, administrator.getId());
                
                int userRowsAffected = userStmt.executeUpdate();
                if (userRowsAffected == 0) {
                    throw new SQLException("Updating user failed, no rows affected.");
                }
            }
            
            // Update administrators table
            try (PreparedStatement adminStmt = conn.prepareStatement(adminSql)) {
                adminStmt.setString(1, administrator.getAdminId());
                adminStmt.setString(2, administrator.getDepartment());
                adminStmt.setString(3, administrator.getPosition());
                adminStmt.setString(4, administrator.getAccessLevel());
                adminStmt.setInt(5, administrator.getId());
                
                int adminRowsAffected = adminStmt.executeUpdate();
                if (adminRowsAffected == 0) {
                    throw new SQLException("Updating administrator failed, no rows affected.");
                }
            }
            
            conn.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Error updating administrator: " + e.getMessage());
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException rollbackEx) {
                System.err.println("Error rolling back transaction: " + rollbackEx.getMessage());
            }
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}
