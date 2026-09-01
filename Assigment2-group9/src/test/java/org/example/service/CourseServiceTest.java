/**
 * @author Group 9
 */
package org.example.service;

import org.example.db.CourseRepository;
import org.example.exception.ValidationException;
import org.example.model.Course;
import org.example.model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the CourseService class.
 */
public class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    private CourseService courseService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        courseService = new CourseService(courseRepository);
    }

    @Test
    public void testCreateCourse_ValidCourse() {
        // Create a valid course
        Course course = new Course(0, "CS101", "Introduction to Computer Science", "An introductory course", 1, 2023);

        // Mock the repository methods
        when(courseRepository.getCourseByCourseCode("CS101")).thenReturn(Optional.empty());
        when(courseRepository.createCourse(course)).thenReturn(true);

        // Call the service method
        boolean result = courseService.createCourse(course);

        // Verify the result
        assertTrue(result);

        // Verify that the repository methods were called
        verify(courseRepository).getCourseByCourseCode("CS101");
        verify(courseRepository).createCourse(course);
    }

    @Test
    public void testCreateCourse_NullCourse() {
        // Call the service method with a null course
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            courseService.createCourse(null);
        });

        // Verify the exception message
        assertEquals("Course cannot be null", exception.getMessage());

        // Verify that the repository methods were not called
        verify(courseRepository, never()).getCourseByCourseCode(anyString());
        verify(courseRepository, never()).createCourse(any(Course.class));
    }

    @Test
    public void testCreateCourse_EmptyCourseCode() {
        // Create a course with an empty course code
        Course course = new Course(0, "", "Introduction to Computer Science", "An introductory course", 1, 2023);

        // Call the service method
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            courseService.createCourse(course);
        });

        // Verify the exception message
        assertEquals("Course code cannot be empty", exception.getMessage());

        // Verify that the repository methods were not called
        verify(courseRepository, never()).getCourseByCourseCode(anyString());
        verify(courseRepository, never()).createCourse(any(Course.class));
    }

    @Test
    public void testCreateCourse_EmptyCourseName() {
        // Create a course with an empty course name
        Course course = new Course(0, "CS101", "", "An introductory course", 1, 2023);

        // Call the service method
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            courseService.createCourse(course);
        });

        // Verify the exception message
        assertEquals("Course name cannot be empty", exception.getMessage());

        // Verify that the repository methods were not called
        verify(courseRepository, never()).getCourseByCourseCode(anyString());
        verify(courseRepository, never()).createCourse(any(Course.class));
    }

    @Test
    public void testCreateCourse_InvalidSemester() {
        // Create a course with an invalid semester
        Course course = new Course(0, "CS101", "Introduction to Computer Science", "An introductory course", 4, 2023);

        // Call the service method
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            courseService.createCourse(course);
        });

        // Verify the exception message
        assertEquals("Semester must be between 1 and 3", exception.getMessage());

        // Verify that the repository methods were not called
        verify(courseRepository, never()).getCourseByCourseCode(anyString());
        verify(courseRepository, never()).createCourse(any(Course.class));
    }

    @Test
    public void testCreateCourse_InvalidYear() {
        // Create a course with an invalid year
        Course course = new Course(0, "CS101", "Introduction to Computer Science", "An introductory course", 1, 1999);

        // Call the service method
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            courseService.createCourse(course);
        });

        // Verify the exception message
        assertEquals("Year must be between 2000 and 2100", exception.getMessage());

        // Verify that the repository methods were not called
        verify(courseRepository, never()).getCourseByCourseCode(anyString());
        verify(courseRepository, never()).createCourse(any(Course.class));
    }

    @Test
    public void testCreateCourse_DuplicateCourseCode() {
        // Create a valid course
        Course course = new Course(0, "CS101", "Introduction to Computer Science", "An introductory course", 1, 2023);

        // Mock the repository methods
        when(courseRepository.getCourseByCourseCode("CS101")).thenReturn(Optional.of(new Course()));

        // Call the service method
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            courseService.createCourse(course);
        });

        // Verify the exception message
        assertEquals("Course code is already in use: CS101", exception.getMessage());

        // Verify that the repository methods were called
        verify(courseRepository).getCourseByCourseCode("CS101");
        verify(courseRepository, never()).createCourse(any(Course.class));
    }

    @Test
    public void testUpdateCourse_ValidCourse() {
        // Create a valid course
        Course course = new Course(1, "CS101", "Introduction to Computer Science", "An introductory course", 1, 2023);

        // Mock the repository methods
        when(courseRepository.getCourseById(1)).thenReturn(Optional.of(course));
        when(courseRepository.getCourseByCourseCode("CS101")).thenReturn(Optional.of(course));
        when(courseRepository.updateCourse(course)).thenReturn(true);

        // Call the service method
        boolean result = courseService.updateCourse(course);

        // Verify the result
        assertTrue(result);

        // Verify that the repository methods were called
        verify(courseRepository).getCourseById(1);
        verify(courseRepository).getCourseByCourseCode("CS101");
        verify(courseRepository).updateCourse(course);
    }

    @Test
    public void testDeleteCourse_ValidCourse() {
        // Create a valid course
        Course course = new Course(1, "CS101", "Introduction to Computer Science", "An introductory course", 1, 2023);

        // Mock the repository methods
        when(courseRepository.getCourseById(1)).thenReturn(Optional.of(course));
        when(courseRepository.getStudentsInCourse(1)).thenReturn(new ArrayList<>());
        when(courseRepository.deleteCourse(1)).thenReturn(true);

        // Call the service method
        boolean result = courseService.deleteCourse(1);

        // Verify the result
        assertTrue(result);

        // Verify that the repository methods were called
        verify(courseRepository).getCourseById(1);
        verify(courseRepository).getStudentsInCourse(1);
        verify(courseRepository).deleteCourse(1);
    }

    @Test
    public void testDeleteCourse_WithEnrolledStudents() {
        // Create a valid course
        Course course = new Course(1, "CS101", "Introduction to Computer Science", "An introductory course", 1, 2023);

        // Create a list of enrolled students
        List<Student> enrolledStudents = new ArrayList<>();
        enrolledStudents.add(new Student());

        // Mock the repository methods
        when(courseRepository.getCourseById(1)).thenReturn(Optional.of(course));
        when(courseRepository.getStudentsInCourse(1)).thenReturn(enrolledStudents);

        // Call the service method
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            courseService.deleteCourse(1);
        });

        // Verify the exception message
        assertEquals("Cannot delete course with enrolled students. Remove all students first.", exception.getMessage());

        // Verify that the repository methods were called
        verify(courseRepository).getCourseById(1);
        verify(courseRepository).getStudentsInCourse(1);
        verify(courseRepository, never()).deleteCourse(anyInt());
    }

    @Test
    public void testEnrollStudent_ValidEnrollment() {
        // Create a valid course
        Course course = new Course(1, "CS101", "Introduction to Computer Science", "An introductory course", 1, 2023);

        // Create an empty list of students (student not enrolled)
        List<Student> students = new ArrayList<>();

        // Mock the repository methods
        when(courseRepository.getCourseById(1)).thenReturn(Optional.of(course));
        when(courseRepository.getStudentsInCourse(1)).thenReturn(students);
        when(courseRepository.enrollStudent(2, 1)).thenReturn(true);

        // Call the service method
        boolean result = courseService.enrollStudent(2, 1);

        // Verify the result
        assertTrue(result);

        // Verify that the repository methods were called
        verify(courseRepository, times(2)).getCourseById(1);
        verify(courseRepository).getStudentsInCourse(1);
        verify(courseRepository).enrollStudent(2, 1);
    }

    @Test
    public void testRemoveStudent_ValidRemoval() {
        // Create a valid course
        Course course = new Course(1, "CS101", "Introduction to Computer Science", "An introductory course", 1, 2023);

        // Create a list with the student (student is enrolled)
        List<Student> students = new ArrayList<>();
        Student student = new Student();
        student.setId(2);
        students.add(student);

        // Mock the repository methods
        when(courseRepository.getCourseById(1)).thenReturn(Optional.of(course));
        when(courseRepository.getStudentsInCourse(1)).thenReturn(students);
        when(courseRepository.removeStudent(2, 1)).thenReturn(true);

        // Call the service method
        boolean result = courseService.removeStudent(2, 1);

        // Verify the result
        assertTrue(result);

        // Verify that the repository methods were called
        verify(courseRepository, times(2)).getCourseById(1);
        verify(courseRepository).getStudentsInCourse(1);
        verify(courseRepository).removeStudent(2, 1);
    }

    @Test
    public void testAssignInstructor_ValidAssignment() {
        // Create a valid course
        Course course = new Course(1, "CS101", "Introduction to Computer Science", "An introductory course", 1, 2023);

        // Mock the repository methods
        when(courseRepository.getCourseById(1)).thenReturn(Optional.of(course));
        when(courseRepository.assignInstructor(1, 2)).thenReturn(true);

        // Call the service method
        boolean result = courseService.assignInstructor(1, 2);

        // Verify the result
        assertTrue(result);

        // Verify that the repository methods were called
        verify(courseRepository).getCourseById(1);
        verify(courseRepository).assignInstructor(1, 2);
    }
}
