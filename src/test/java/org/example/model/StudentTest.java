/**
 * @author Group 9
 */
package org.example.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Student class.
 */
public class StudentTest {
    
    private Student student;
    
    @BeforeEach
    public void setUp() {
        // Create a new student instance before each test
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
    }
    
    @Test
    public void testStudentConstructor() {
        // Test that the student is created with the correct values
        assertEquals(1, student.getId());
        assertEquals("jsmith", student.getUsername());
        assertEquals("password123", student.getPassword());
        assertEquals("John", student.getFirstName());
        assertEquals("Smith", student.getLastName());
        assertEquals("john.smith@example.com", student.getEmail());
        assertEquals("1234567890", student.getPhoneNumber());
        assertEquals(LocalDate.of(2000, 1, 15), student.getDateOfBirth());
        assertEquals("123 Main St", student.getAddress());
        assertEquals("S12345", student.getStudentId());
        assertEquals("Computer Science", student.getMajor());
        assertEquals(2, student.getYear());
        assertEquals(0, student.getEnrolledCourses().size());
    }
    
    @Test
    public void testStudentSetters() {
        // Test the setters
        student.setId(2);
        student.setUsername("johnsmith");
        student.setPassword("newpassword");
        student.setFirstName("Johnny");
        student.setLastName("Smithson");
        student.setEmail("johnny.smithson@example.com");
        student.setPhoneNumber("0987654321");
        student.setDateOfBirth(LocalDate.of(2001, 2, 20));
        student.setAddress("456 Oak St");
        student.setStudentId("S67890");
        student.setMajor("Information Technology");
        student.setYear(3);
        
        // Verify the values were set correctly
        assertEquals(2, student.getId());
        assertEquals("johnsmith", student.getUsername());
        assertEquals("newpassword", student.getPassword());
        assertEquals("Johnny", student.getFirstName());
        assertEquals("Smithson", student.getLastName());
        assertEquals("johnny.smithson@example.com", student.getEmail());
        assertEquals("0987654321", student.getPhoneNumber());
        assertEquals(LocalDate.of(2001, 2, 20), student.getDateOfBirth());
        assertEquals("456 Oak St", student.getAddress());
        assertEquals("S67890", student.getStudentId());
        assertEquals("Information Technology", student.getMajor());
        assertEquals(3, student.getYear());
    }
    
    @Test
    public void testEnrollInCourse() {
        // Create a course
        Course course1 = new Course(1, "CS101", "Introduction to Programming", "Basic programming concepts", 1, 2023);
        Course course2 = new Course(2, "CS102", "Data Structures", "Advanced programming concepts", 1, 2023);
        
        // Initially, the student should not be enrolled in any courses
        assertEquals(0, student.getEnrolledCourses().size());
        
        // Enroll in the first course
        assertTrue(student.enrollInCourse(course1));
        assertEquals(1, student.getEnrolledCourses().size());
        assertTrue(student.isEnrolledIn(course1));
        
        // Enroll in the second course
        assertTrue(student.enrollInCourse(course2));
        assertEquals(2, student.getEnrolledCourses().size());
        assertTrue(student.isEnrolledIn(course2));
        
        // Try to enroll in the first course again (should fail)
        assertFalse(student.enrollInCourse(course1));
        assertEquals(2, student.getEnrolledCourses().size());
    }
    
    @Test
    public void testWithdrawFromCourse() {
        // Create a course
        Course course1 = new Course(1, "CS101", "Introduction to Programming", "Basic programming concepts", 1, 2023);
        Course course2 = new Course(2, "CS102", "Data Structures", "Advanced programming concepts", 1, 2023);
        
        // Enroll in both courses
        student.enrollInCourse(course1);
        student.enrollInCourse(course2);
        assertEquals(2, student.getEnrolledCourses().size());
        
        // Withdraw from the first course
        assertTrue(student.withdrawFromCourse(course1));
        assertEquals(1, student.getEnrolledCourses().size());
        assertFalse(student.isEnrolledIn(course1));
        assertTrue(student.isEnrolledIn(course2));
        
        // Withdraw from the second course
        assertTrue(student.withdrawFromCourse(course2));
        assertEquals(0, student.getEnrolledCourses().size());
        assertFalse(student.isEnrolledIn(course2));
        
        // Try to withdraw from a course the student is not enrolled in
        assertFalse(student.withdrawFromCourse(course1));
    }
    
    @Test
    public void testIsEnrolledIn() {
        // Create a course
        Course course1 = new Course(1, "CS101", "Introduction to Programming", "Basic programming concepts", 1, 2023);
        Course course2 = new Course(2, "CS102", "Data Structures", "Advanced programming concepts", 1, 2023);
        
        // Initially, the student should not be enrolled in any courses
        assertFalse(student.isEnrolledIn(course1));
        assertFalse(student.isEnrolledIn(course2));
        
        // Enroll in the first course
        student.enrollInCourse(course1);
        assertTrue(student.isEnrolledIn(course1));
        assertFalse(student.isEnrolledIn(course2));
        
        // Enroll in the second course
        student.enrollInCourse(course2);
        assertTrue(student.isEnrolledIn(course1));
        assertTrue(student.isEnrolledIn(course2));
        
        // Withdraw from the first course
        student.withdrawFromCourse(course1);
        assertFalse(student.isEnrolledIn(course1));
        assertTrue(student.isEnrolledIn(course2));
    }
    
    @Test
    public void testSetEnrolledCourses() {
        // Create a list of courses
        List<Course> courses = new ArrayList<>();
        courses.add(new Course(1, "CS101", "Introduction to Programming", "Basic programming concepts", 1, 2023));
        courses.add(new Course(2, "CS102", "Data Structures", "Advanced programming concepts", 1, 2023));
        
        // Set the enrolled courses
        student.setEnrolledCourses(courses);
        
        // Verify the enrolled courses
        assertEquals(2, student.getEnrolledCourses().size());
        assertEquals(courses, student.getEnrolledCourses());
    }
    
    @Test
    public void testGetRole() {
        // Test the getRole method
        assertEquals("Student", student.getRole());
    }
    
    @Test
    public void testToString() {
        // Test the toString method
        String expectedString = "Student{" +
                "id=1" +
                ", username='jsmith'" +
                ", firstName='John'" +
                ", lastName='Smith'" +
                ", email='john.smith@example.com'" +
                ", studentId='S12345'" +
                ", major='Computer Science'" +
                ", year=2" +
                ", enrolledCourses=0" +
                '}';
        
        assertEquals(expectedString, student.toString());
    }
}