/**
 * @author Group 9
 */
package org.example.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a student user in the system.
 * Students can view and update their personal information, retrieve lending history,
 * and borrow/return equipment with approval from academic staff.
 */
public class Student extends User {
    private String studentId;
    private String major;
    private int year;
    private List<Course> enrolledCourses;

    /**
     * Default constructor
     */
    public Student() {
        this.enrolledCourses = new ArrayList<>();
    }

    /**
     * Parameterized constructor
     *
     * @param id          Unique identifier for the user
     * @param username    Username for login
     * @param password    Password for login
     * @param firstName   User's first name
     * @param lastName    User's last name
     * @param email       User's email address
     * @param phoneNumber User's phone number
     * @param dateOfBirth User's date of birth
     * @param address     User's address
     * @param studentId   Student's unique ID
     * @param major       Student's major
     * @param year        Student's year of study
     */
    public Student(int id, String username, String password, String firstName, String lastName,
                  String email, String phoneNumber, LocalDate dateOfBirth, String address,
                  String studentId, String major, int year) {
        super(id, username, password, firstName, lastName, email, phoneNumber, dateOfBirth, address);
        this.studentId = studentId;
        this.major = major;
        this.year = year;
        this.enrolledCourses = new ArrayList<>();
    }

    // Getters and Setters
    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public List<Course> getEnrolledCourses() {
        return enrolledCourses;
    }

    public void setEnrolledCourses(List<Course> enrolledCourses) {
        this.enrolledCourses = enrolledCourses;
    }

    /**
     * Enroll the student in a course
     *
     * @param course The course to enroll in
     * @return true if enrollment was successful, false otherwise
     */
    public boolean enrollInCourse(Course course) {
        if (!enrolledCourses.contains(course)) {
            enrolledCourses.add(course);
            return true;
        }
        return false;
    }

    /**
     * Remove the student from a course
     *
     * @param course The course to withdraw from
     * @return true if withdrawal was successful, false otherwise
     */
    public boolean withdrawFromCourse(Course course) {
        return enrolledCourses.remove(course);
    }

    /**
     * Check if the student is enrolled in a specific course
     *
     * @param course The course to check enrollment for
     * @return true if the student is enrolled, false otherwise
     */
    public boolean isEnrolledIn(Course course) {
        return enrolledCourses.contains(course);
    }

    @Override
    public String getRole() {
        return "Student";
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + getId() +
                ", username='" + getUsername() + '\'' +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", studentId='" + studentId + '\'' +
                ", major='" + major + '\'' +
                ", year=" + year +
                ", enrolledCourses=" + enrolledCourses.size() +
                '}';
    }
}