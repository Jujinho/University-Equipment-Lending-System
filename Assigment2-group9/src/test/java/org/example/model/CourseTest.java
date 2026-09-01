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
 * Unit tests for the Course class.
 */
public class CourseTest {
    
    private Course course;
    private AcademicStaff instructor;
    
    @BeforeEach
    public void setUp() {
        // Create an instructor
        instructor = new AcademicStaff(
                1,
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
        
        // Create a new course instance before each test
        course = new Course(
                1,
                "CS101",
                "Introduction to Programming",
                "A course that introduces basic programming concepts",
                1,
                2023,
                instructor
        );
    }
    
    @Test
    public void testCourseConstructor() {
        // Test that the course is created with the correct values
        assertEquals(1, course.getId());
        assertEquals("CS101", course.getCourseCode());
        assertEquals("Introduction to Programming", course.getCourseName());
        assertEquals("A course that introduces basic programming concepts", course.getDescription());
        assertEquals(1, course.getSemester());
        assertEquals(2023, course.getYear());
        assertEquals(instructor, course.getInstructor());
        assertEquals(0, course.getEnrolledStudents().size());
    }
    
    @Test
    public void testCourseConstructorWithoutInstructor() {
        // Create a course without an instructor
        Course courseWithoutInstructor = new Course(
                2,
                "CS102",
                "Data Structures",
                "A course on data structures and algorithms",
                2,
                2023
        );
        
        // Test that the course is created with the correct values
        assertEquals(2, courseWithoutInstructor.getId());
        assertEquals("CS102", courseWithoutInstructor.getCourseCode());
        assertEquals("Data Structures", courseWithoutInstructor.getCourseName());
        assertEquals("A course on data structures and algorithms", courseWithoutInstructor.getDescription());
        assertEquals(2, courseWithoutInstructor.getSemester());
        assertEquals(2023, courseWithoutInstructor.getYear());
        assertNull(courseWithoutInstructor.getInstructor());
        assertEquals(0, courseWithoutInstructor.getEnrolledStudents().size());
    }
    
    @Test
    public void testCourseSetters() {
        // Create a new instructor
        AcademicStaff newInstructor = new AcademicStaff(
                2,
                "asmith",
                "password456",
                "Alice",
                "Smith",
                "alice.smith@example.com",
                "0987654321",
                LocalDate.of(1975, 10, 20),
                "456 Pine St",
                "AS67890",
                "Mathematics",
                "Associate Professor"
        );
        
        // Test the setters
        course.setId(3);
        course.setCourseCode("CS103");
        course.setCourseName("Advanced Programming");
        course.setDescription("A course on advanced programming techniques");
        course.setSemester(3);
        course.setYear(2024);
        course.setInstructor(newInstructor);
        
        // Verify the values were set correctly
        assertEquals(3, course.getId());
        assertEquals("CS103", course.getCourseCode());
        assertEquals("Advanced Programming", course.getCourseName());
        assertEquals("A course on advanced programming techniques", course.getDescription());
        assertEquals(3, course.getSemester());
        assertEquals(2024, course.getYear());
        assertEquals(newInstructor, course.getInstructor());
    }
    
    @Test
    public void testEnrollStudent() {
        // Create students
        Student student1 = new Student(
                2,
                "bwhite",
                "password123",
                "Bob",
                "White",
                "bob.white@example.com",
                "1234567890",
                LocalDate.of(2000, 1, 15),
                "123 Main St",
                "S12345",
                "Computer Science",
                2
        );
        
        Student student2 = new Student(
                3,
                "cgreen",
                "password456",
                "Carol",
                "Green",
                "carol.green@example.com",
                "0987654321",
                LocalDate.of(2001, 2, 20),
                "456 Oak St",
                "S67890",
                "Information Technology",
                3
        );
        
        // Initially, there should be no enrolled students
        assertEquals(0, course.getEnrolledStudents().size());
        
        // Enroll the first student
        assertTrue(course.enrollStudent(student1));
        assertEquals(1, course.getEnrolledStudents().size());
        assertTrue(course.isStudentEnrolled(student1));
        assertTrue(student1.isEnrolledIn(course)); // Verify bidirectional relationship
        
        // Enroll the second student
        assertTrue(course.enrollStudent(student2));
        assertEquals(2, course.getEnrolledStudents().size());
        assertTrue(course.isStudentEnrolled(student2));
        assertTrue(student2.isEnrolledIn(course)); // Verify bidirectional relationship
        
        // Try to enroll the first student again (should fail)
        assertFalse(course.enrollStudent(student1));
        assertEquals(2, course.getEnrolledStudents().size());
    }
    
    @Test
    public void testRemoveStudent() {
        // Create students
        Student student1 = new Student(
                2,
                "bwhite",
                "password123",
                "Bob",
                "White",
                "bob.white@example.com",
                "1234567890",
                LocalDate.of(2000, 1, 15),
                "123 Main St",
                "S12345",
                "Computer Science",
                2
        );
        
        Student student2 = new Student(
                3,
                "cgreen",
                "password456",
                "Carol",
                "Green",
                "carol.green@example.com",
                "0987654321",
                LocalDate.of(2001, 2, 20),
                "456 Oak St",
                "S67890",
                "Information Technology",
                3
        );
        
        // Enroll both students
        course.enrollStudent(student1);
        course.enrollStudent(student2);
        assertEquals(2, course.getEnrolledStudents().size());
        
        // Remove the first student
        assertTrue(course.removeStudent(student1));
        assertEquals(1, course.getEnrolledStudents().size());
        assertFalse(course.isStudentEnrolled(student1));
        assertTrue(course.isStudentEnrolled(student2));
        assertFalse(student1.isEnrolledIn(course)); // Verify bidirectional relationship
        
        // Remove the second student
        assertTrue(course.removeStudent(student2));
        assertEquals(0, course.getEnrolledStudents().size());
        assertFalse(course.isStudentEnrolled(student2));
        assertFalse(student2.isEnrolledIn(course)); // Verify bidirectional relationship
        
        // Try to remove a student who is not enrolled
        assertFalse(course.removeStudent(student1));
    }
    
    @Test
    public void testIsStudentEnrolled() {
        // Create students
        Student student1 = new Student(
                2,
                "bwhite",
                "password123",
                "Bob",
                "White",
                "bob.white@example.com",
                "1234567890",
                LocalDate.of(2000, 1, 15),
                "123 Main St",
                "S12345",
                "Computer Science",
                2
        );
        
        Student student2 = new Student(
                3,
                "cgreen",
                "password456",
                "Carol",
                "Green",
                "carol.green@example.com",
                "0987654321",
                LocalDate.of(2001, 2, 20),
                "456 Oak St",
                "S67890",
                "Information Technology",
                3
        );
        
        // Initially, no students should be enrolled
        assertFalse(course.isStudentEnrolled(student1));
        assertFalse(course.isStudentEnrolled(student2));
        
        // Enroll the first student
        course.enrollStudent(student1);
        assertTrue(course.isStudentEnrolled(student1));
        assertFalse(course.isStudentEnrolled(student2));
        
        // Enroll the second student
        course.enrollStudent(student2);
        assertTrue(course.isStudentEnrolled(student1));
        assertTrue(course.isStudentEnrolled(student2));
        
        // Remove the first student
        course.removeStudent(student1);
        assertFalse(course.isStudentEnrolled(student1));
        assertTrue(course.isStudentEnrolled(student2));
    }
    
    @Test
    public void testSetEnrolledStudents() {
        // Create a list of students
        List<Student> students = new ArrayList<>();
        students.add(new Student(
                2,
                "bwhite",
                "password123",
                "Bob",
                "White",
                "bob.white@example.com",
                "1234567890",
                LocalDate.of(2000, 1, 15),
                "123 Main St",
                "S12345",
                "Computer Science",
                2
        ));
        students.add(new Student(
                3,
                "cgreen",
                "password456",
                "Carol",
                "Green",
                "carol.green@example.com",
                "0987654321",
                LocalDate.of(2001, 2, 20),
                "456 Oak St",
                "S67890",
                "Information Technology",
                3
        ));
        
        // Set the enrolled students
        course.setEnrolledStudents(students);
        
        // Verify the enrolled students
        assertEquals(2, course.getEnrolledStudents().size());
        assertEquals(students, course.getEnrolledStudents());
    }
    
    @Test
    public void testToString() {
        // Test the toString method
        String expectedString = "Course{" +
                "id=1" +
                ", courseCode='CS101'" +
                ", courseName='Introduction to Programming'" +
                ", semester=1" +
                ", year=2023" +
                ", instructor=" + instructor.getUsername() +
                ", enrolledStudents=0" +
                '}';
        
        assertEquals(expectedString, course.toString());
    }
    
    @Test
    public void testEquals() {
        // Create a course with the same ID but different attributes
        Course sameCourse = new Course(
                1, // Same ID
                "CS999",
                "Different Course",
                "Different description",
                3,
                2025
        );
        
        // Create a course with a different ID
        Course differentCourse = new Course(
                2, // Different ID
                "CS101",
                "Introduction to Programming",
                "A course that introduces basic programming concepts",
                1,
                2023,
                instructor
        );
        
        // Test equals
        assertEquals(course, course); // Same object
        assertEquals(course, sameCourse); // Same ID
        assertNotEquals(course, differentCourse); // Different ID
        assertNotEquals(course, null); // Null
        assertNotEquals(course, "Not a Course"); // Different class
    }
    
    @Test
    public void testHashCode() {
        // Create a course with the same ID but different attributes
        Course sameCourse = new Course(
                1, // Same ID
                "CS999",
                "Different Course",
                "Different description",
                3,
                2025
        );
        
        // Create a course with a different ID
        Course differentCourse = new Course(
                2, // Different ID
                "CS101",
                "Introduction to Programming",
                "A course that introduces basic programming concepts",
                1,
                2023,
                instructor
        );
        
        // Test hashCode
        assertEquals(course.hashCode(), sameCourse.hashCode()); // Same ID
        assertNotEquals(course.hashCode(), differentCourse.hashCode()); // Different ID
    }
}