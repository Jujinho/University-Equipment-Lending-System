/**
 * @author Group 9
 */
package org.example.service;

import org.example.db.CourseRepository;
import org.example.exception.ValidationException;
import org.example.model.AcademicStaff;
import org.example.model.Course;
import org.example.model.Student;

import java.util.List;
import java.util.Optional;

/**
 * Service class for course-related operations.
 */
public class CourseService {

    private final CourseRepository courseRepository;

    /**
     * Constructor
     *
     * @param courseRepository The CourseRepository to use for course operations
     */
    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    /**
     * Get a course by ID
     *
     * @param id The course ID
     * @return An Optional containing the Course if found, or empty if not found
     */
    public Optional<Course> getCourseById(int id) {
        return courseRepository.getCourseById(id);
    }

    /**
     * Get a course by course code
     *
     * @param courseCode The course code
     * @return An Optional containing the Course if found, or empty if not found
     */
    public Optional<Course> getCourseByCourseCode(String courseCode) {
        return courseRepository.getCourseByCourseCode(courseCode);
    }

    /**
     * Get all courses
     *
     * @return A list of all courses
     */
    public List<Course> getAllCourses() {
        return courseRepository.getAllCourses();
    }

    /**
     * Get courses by instructor
     *
     * @param instructorId The ID of the instructor (academic staff)
     * @return A list of courses taught by the instructor
     */
    public List<Course> getCoursesByInstructor(int instructorId) {
        return courseRepository.getCoursesByInstructor(instructorId);
    }

    /**
     * Get courses by student
     *
     * @param studentId The ID of the student
     * @return A list of courses in which the student is enrolled
     */
    public List<Course> getCoursesByStudent(int studentId) {
        return courseRepository.getCoursesByStudent(studentId);
    }

    /**
     * Create a new course with input validation
     *
     * @param course The course to create
     * @return true if the course was created successfully, false otherwise
     * @throws ValidationException If the input data is invalid
     */
    public boolean createCourse(Course course) {
        // Validate course data
        validateCourse(course);

        // Check if course code is already in use
        if (!isCourseCodeAvailable(course.getCourseCode())) {
            throw new ValidationException("Course code is already in use: " + course.getCourseCode());
        }

        try {
            return courseRepository.createCourse(course);
        } catch (Exception e) {
            throw new ValidationException("Error creating course: " + e.getMessage(), e);
        }
    }

    /**
     * Validate course data
     *
     * @param course The course to validate
     * @throws ValidationException If the course data is invalid
     */
    private void validateCourse(Course course) {
        if (course == null) {
            throw new ValidationException("Course cannot be null");
        }

        if (course.getCourseCode() == null || course.getCourseCode().trim().isEmpty()) {
            throw new ValidationException("Course code cannot be empty");
        }

        if (course.getCourseName() == null || course.getCourseName().trim().isEmpty()) {
            throw new ValidationException("Course name cannot be empty");
        }

        if (course.getSemester() < 1 || course.getSemester() > 3) {
            throw new ValidationException("Semester must be between 1 and 3");
        }

        if (course.getYear() < 2000 || course.getYear() > 2100) {
            throw new ValidationException("Year must be between 2000 and 2100");
        }
    }

    /**
     * Update a course with input validation
     *
     * @param course The course to update
     * @return true if the course was updated successfully, false otherwise
     * @throws ValidationException If the input data is invalid
     */
    public boolean updateCourse(Course course) {
        // Validate course data
        validateCourse(course);

        // Check if course exists
        if (!courseRepository.getCourseById(course.getId()).isPresent()) {
            throw new ValidationException("Course not found with ID: " + course.getId());
        }

        // Check if course code is already in use by another course
        Optional<Course> existingCourse = courseRepository.getCourseByCourseCode(course.getCourseCode());
        if (existingCourse.isPresent() && existingCourse.get().getId() != course.getId()) {
            throw new ValidationException("Course code is already in use by another course: " + course.getCourseCode());
        }

        try {
            return courseRepository.updateCourse(course);
        } catch (Exception e) {
            throw new ValidationException("Error updating course: " + e.getMessage(), e);
        }
    }

    /**
     * Delete a course with validation
     *
     * @param id The ID of the course to delete
     * @return true if the course was deleted successfully, false otherwise
     * @throws ValidationException If the course does not exist or cannot be deleted
     */
    public boolean deleteCourse(int id) {
        // Check if course exists
        Optional<Course> courseOpt = courseRepository.getCourseById(id);
        if (!courseOpt.isPresent()) {
            throw new ValidationException("Course not found with ID: " + id);
        }

        // Check if there are students enrolled in the course
        List<Student> enrolledStudents = courseRepository.getStudentsInCourse(id);
        if (!enrolledStudents.isEmpty()) {
            throw new ValidationException("Cannot delete course with enrolled students. Remove all students first.");
        }

        try {
            return courseRepository.deleteCourse(id);
        } catch (Exception e) {
            throw new ValidationException("Error deleting course: " + e.getMessage(), e);
        }
    }

    /**
     * Enroll a student in a course with validation
     *
     * @param studentId The ID of the student
     * @param courseId  The ID of the course
     * @return true if the enrollment was successful, false otherwise
     * @throws ValidationException If the student or course does not exist, or if the student is already enrolled
     */
    public boolean enrollStudent(int studentId, int courseId) {
        // Check if course exists
        Optional<Course> courseOpt = courseRepository.getCourseById(courseId);
        if (!courseOpt.isPresent()) {
            throw new ValidationException("Course not found with ID: " + courseId);
        }

        // Check if student exists (assuming we have access to UserRepository)
        // This would require injecting UserRepository into CourseService
        // For now, we'll just check if the student is already enrolled

        // Check if student is already enrolled
        if (isStudentEnrolledInCourse(studentId, courseId)) {
            throw new ValidationException("Student is already enrolled in this course");
        }

        try {
            return courseRepository.enrollStudent(studentId, courseId);
        } catch (Exception e) {
            throw new ValidationException("Error enrolling student in course: " + e.getMessage(), e);
        }
    }

    /**
     * Remove a student from a course with validation
     *
     * @param studentId The ID of the student
     * @param courseId  The ID of the course
     * @return true if the removal was successful, false otherwise
     * @throws ValidationException If the student or course does not exist, or if the student is not enrolled
     */
    public boolean removeStudent(int studentId, int courseId) {
        // Check if course exists
        Optional<Course> courseOpt = courseRepository.getCourseById(courseId);
        if (!courseOpt.isPresent()) {
            throw new ValidationException("Course not found with ID: " + courseId);
        }

        // Check if student is enrolled
        if (!isStudentEnrolledInCourse(studentId, courseId)) {
            throw new ValidationException("Student is not enrolled in this course");
        }

        try {
            return courseRepository.removeStudent(studentId, courseId);
        } catch (Exception e) {
            throw new ValidationException("Error removing student from course: " + e.getMessage(), e);
        }
    }

    /**
     * Get students enrolled in a course
     *
     * @param courseId The ID of the course
     * @return A list of students enrolled in the course
     */
    public List<Student> getStudentsInCourse(int courseId) {
        return courseRepository.getStudentsInCourse(courseId);
    }

    /**
     * Assign an instructor to a course with validation
     *
     * @param courseId     The ID of the course
     * @param instructorId The ID of the instructor (academic staff)
     * @return true if the assignment was successful, false otherwise
     * @throws ValidationException If the course or instructor does not exist
     */
    public boolean assignInstructor(int courseId, int instructorId) {
        // Check if course exists
        Optional<Course> courseOpt = courseRepository.getCourseById(courseId);
        if (!courseOpt.isPresent()) {
            throw new ValidationException("Course not found with ID: " + courseId);
        }

        // Check if instructor exists (assuming we have access to UserRepository)
        // This would require injecting UserRepository into CourseService
        // For now, we'll just proceed with the assignment

        try {
            return courseRepository.assignInstructor(courseId, instructorId);
        } catch (Exception e) {
            throw new ValidationException("Error assigning instructor to course: " + e.getMessage(), e);
        }
    }

    /**
     * Check if a course code is available
     *
     * @param courseCode The course code to check
     * @return true if the course code is available, false otherwise
     */
    public boolean isCourseCodeAvailable(String courseCode) {
        return !courseRepository.getCourseByCourseCode(courseCode).isPresent();
    }

    /**
     * Check if a student is enrolled in a course
     *
     * @param studentId The ID of the student
     * @param courseId  The ID of the course
     * @return true if the student is enrolled in the course, false otherwise
     */
    public boolean isStudentEnrolledInCourse(int studentId, int courseId) {
        Optional<Course> courseOpt = courseRepository.getCourseById(courseId);
        if (!courseOpt.isPresent()) {
            return false;
        }

        List<Student> students = courseRepository.getStudentsInCourse(courseId);
        return students.stream().anyMatch(student -> student.getId() == studentId);
    }

    /**
     * Check if an academic staff is the instructor of a course
     *
     * @param academicStaffId The ID of the academic staff
     * @param courseId        The ID of the course
     * @return true if the academic staff is the instructor of the course, false otherwise
     */
    public boolean isInstructorOfCourse(int academicStaffId, int courseId) {
        Optional<Course> courseOpt = courseRepository.getCourseById(courseId);
        if (!courseOpt.isPresent()) {
            return false;
        }

        Course course = courseOpt.get();
        AcademicStaff instructor = course.getInstructor();
        return instructor != null && instructor.getId() == academicStaffId;
    }
}
