/**
 * @author Group 9
 */
package org.example.db;

import org.example.model.AcademicStaff;
import org.example.model.Course;
import org.example.model.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository class for Course entities.
 * Handles database operations for courses, including enrollment of students and assignment of academic staff.
 */
public class CourseRepository {
    
    private final UserRepository userRepository;
    
    /**
     * Constructor
     *
     * @param userRepository The UserRepository to use for retrieving user information
     */
    public CourseRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    /**
     * Get a course by ID
     *
     * @param id The course ID
     * @return An Optional containing the Course if found, or empty if not found
     */
    public Optional<Course> getCourseById(int id) {
        String sql = "SELECT * FROM courses WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(extractCourseFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting course by ID: " + e.getMessage());
        }
        
        return Optional.empty();
    }
    
    /**
     * Get a course by course code
     *
     * @param courseCode The course code
     * @return An Optional containing the Course if found, or empty if not found
     */
    public Optional<Course> getCourseByCourseCode(String courseCode) {
        String sql = "SELECT * FROM courses WHERE course_code = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, courseCode);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(extractCourseFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting course by course code: " + e.getMessage());
        }
        
        return Optional.empty();
    }
    
    /**
     * Get all courses
     *
     * @return A list of all courses
     */
    public List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                courses.add(extractCourseFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all courses: " + e.getMessage());
        }
        
        return courses;
    }
    
    /**
     * Get courses by instructor
     *
     * @param instructorId The ID of the instructor (academic staff)
     * @return A list of courses taught by the instructor
     */
    public List<Course> getCoursesByInstructor(int instructorId) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses WHERE instructor_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, instructorId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    courses.add(extractCourseFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting courses by instructor: " + e.getMessage());
        }
        
        return courses;
    }
    
    /**
     * Get courses by student
     *
     * @param studentId The ID of the student
     * @return A list of courses in which the student is enrolled
     */
    public List<Course> getCoursesByStudent(int studentId) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT c.* FROM courses c " +
                "JOIN student_courses sc ON c.id = sc.course_id " +
                "WHERE sc.student_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, studentId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    courses.add(extractCourseFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting courses by student: " + e.getMessage());
        }
        
        return courses;
    }
    
    /**
     * Create a new course
     *
     * @param course The course to create
     * @return true if the course was created successfully, false otherwise
     */
    public boolean createCourse(Course course) {
        String sql = "INSERT INTO courses (course_code, course_name, description, semester, year, instructor_id) " +
                "VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, course.getCourseCode());
            stmt.setString(2, course.getCourseName());
            stmt.setString(3, course.getDescription());
            stmt.setInt(4, course.getSemester());
            stmt.setInt(5, course.getYear());
            
            if (course.getInstructor() != null) {
                stmt.setInt(6, course.getInstructor().getId());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating course failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int courseId = generatedKeys.getInt(1);
                    course.setId(courseId);
                    return true;
                } else {
                    throw new SQLException("Creating course failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating course: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Update a course
     *
     * @param course The course to update
     * @return true if the course was updated successfully, false otherwise
     */
    public boolean updateCourse(Course course) {
        String sql = "UPDATE courses SET course_code = ?, course_name = ?, description = ?, " +
                "semester = ?, year = ?, instructor_id = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, course.getCourseCode());
            stmt.setString(2, course.getCourseName());
            stmt.setString(3, course.getDescription());
            stmt.setInt(4, course.getSemester());
            stmt.setInt(5, course.getYear());
            
            if (course.getInstructor() != null) {
                stmt.setInt(6, course.getInstructor().getId());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }
            
            stmt.setInt(7, course.getId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating course: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Delete a course
     *
     * @param id The ID of the course to delete
     * @return true if the course was deleted successfully, false otherwise
     */
    public boolean deleteCourse(int id) {
        String sql = "DELETE FROM courses WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting course: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Enroll a student in a course
     *
     * @param studentId The ID of the student
     * @param courseId  The ID of the course
     * @return true if the enrollment was successful, false otherwise
     */
    public boolean enrollStudent(int studentId, int courseId) {
        String sql = "INSERT INTO student_courses (student_id, course_id) VALUES (?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, studentId);
            stmt.setInt(2, courseId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error enrolling student in course: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Remove a student from a course
     *
     * @param studentId The ID of the student
     * @param courseId  The ID of the course
     * @return true if the removal was successful, false otherwise
     */
    public boolean removeStudent(int studentId, int courseId) {
        String sql = "DELETE FROM student_courses WHERE student_id = ? AND course_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, studentId);
            stmt.setInt(2, courseId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error removing student from course: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get students enrolled in a course
     *
     * @param courseId The ID of the course
     * @return A list of students enrolled in the course
     */
    public List<Student> getStudentsInCourse(int courseId) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT s.user_id FROM student_courses sc " +
                "JOIN students s ON sc.student_id = s.user_id " +
                "WHERE sc.course_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, courseId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int studentId = rs.getInt("user_id");
                    userRepository.getUserById(studentId)
                            .filter(user -> user instanceof Student)
                            .map(user -> (Student) user)
                            .ifPresent(students::add);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting students in course: " + e.getMessage());
        }
        
        return students;
    }
    
    /**
     * Assign an instructor to a course
     *
     * @param courseId     The ID of the course
     * @param instructorId The ID of the instructor (academic staff)
     * @return true if the assignment was successful, false otherwise
     */
    public boolean assignInstructor(int courseId, int instructorId) {
        String sql = "UPDATE courses SET instructor_id = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, instructorId);
            stmt.setInt(2, courseId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error assigning instructor to course: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Extract a course from a ResultSet
     *
     * @param rs The ResultSet containing course data
     * @return The extracted Course
     * @throws SQLException If a database access error occurs
     */
    private Course extractCourseFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String courseCode = rs.getString("course_code");
        String courseName = rs.getString("course_name");
        String description = rs.getString("description");
        int semester = rs.getInt("semester");
        int year = rs.getInt("year");
        int instructorId = rs.getInt("instructor_id");
        
        Course course = new Course(id, courseCode, courseName, description, semester, year);
        
        if (instructorId > 0) {
            userRepository.getUserById(instructorId)
                    .filter(user -> user instanceof AcademicStaff)
                    .map(user -> (AcademicStaff) user)
                    .ifPresent(course::setInstructor);
        }
        
        // Load enrolled students
        List<Student> enrolledStudents = getStudentsInCourse(id);
        for (Student student : enrolledStudents) {
            course.enrollStudent(student);
        }
        
        return course;
    }
}