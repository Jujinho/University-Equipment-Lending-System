/**
 * @author Group 9
 */
package org.example.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a course in the university.
 * Each course is assigned to one academic staff and maintains a list of enrolled students.
 * Students can only borrow equipment for courses they are enrolled in.
 */
public class Course {
    private int id;
    private String courseCode;
    private String courseName;
    private String description;
    private int semester;
    private int year;
    private AcademicStaff instructor;
    private List<Student> enrolledStudents;

    /**
     * Default constructor
     */
    public Course() {
        this.enrolledStudents = new ArrayList<>();
    }

    /**
     * Parameterized constructor without instructor
     *
     * @param id          Unique identifier for the course
     * @param courseCode  Course code (e.g., CS101)
     * @param courseName  Name of the course
     * @param description Description of the course
     * @param semester    Semester in which the course is offered
     * @param year        Year in which the course is offered
     */
    public Course(int id, String courseCode, String courseName, String description, 
                 int semester, int year) {
        this.id = id;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.description = description;
        this.semester = semester;
        this.year = year;
        this.enrolledStudents = new ArrayList<>();
    }

    /**
     * Parameterized constructor with instructor
     *
     * @param id          Unique identifier for the course
     * @param courseCode  Course code (e.g., CS101)
     * @param courseName  Name of the course
     * @param description Description of the course
     * @param semester    Semester in which the course is offered
     * @param year        Year in which the course is offered
     * @param instructor  Academic staff teaching the course
     */
    public Course(int id, String courseCode, String courseName, String description, 
                 int semester, int year, AcademicStaff instructor) {
        this.id = id;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.description = description;
        this.semester = semester;
        this.year = year;
        this.instructor = instructor;
        this.enrolledStudents = new ArrayList<>();
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public AcademicStaff getInstructor() {
        return instructor;
    }

    public void setInstructor(AcademicStaff instructor) {
        this.instructor = instructor;
    }

    public List<Student> getEnrolledStudents() {
        return enrolledStudents;
    }

    public void setEnrolledStudents(List<Student> enrolledStudents) {
        this.enrolledStudents = enrolledStudents;
    }

    /**
     * Enroll a student in this course
     *
     * @param student The student to enroll
     * @return true if enrollment was successful, false otherwise
     */
    public boolean enrollStudent(Student student) {
        if (!enrolledStudents.contains(student)) {
            enrolledStudents.add(student);
            student.enrollInCourse(this);
            return true;
        }
        return false;
    }

    /**
     * Remove a student from this course
     *
     * @param student The student to remove
     * @return true if removal was successful, false otherwise
     */
    public boolean removeStudent(Student student) {
        if (enrolledStudents.remove(student)) {
            student.withdrawFromCourse(this);
            return true;
        }
        return false;
    }

    /**
     * Check if a student is enrolled in this course
     *
     * @param student The student to check
     * @return true if the student is enrolled, false otherwise
     */
    public boolean isStudentEnrolled(Student student) {
        return enrolledStudents.contains(student);
    }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", courseCode='" + courseCode + '\'' +
                ", courseName='" + courseName + '\'' +
                ", semester=" + semester +
                ", year=" + year +
                ", instructor=" + (instructor != null ? instructor.getUsername() : "None") +
                ", enrolledStudents=" + enrolledStudents.size() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return id == course.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
