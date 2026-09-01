/**
 * @author Group 9
 */
package org.example.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an academic staff member in the university.
 * Academic staff can manage their personal information, manage lending records related to their courses,
 * facilitate borrowing by students, borrow equipment for themselves, and access statistics.
 */
public class AcademicStaff extends User {
    private String staffId;
    private String department;
    private String position;
    private List<Course> courses;

    /**
     * Default constructor
     */
    public AcademicStaff() {
        this.courses = new ArrayList<>();
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
     * @param staffId     Staff's unique ID
     * @param department  Department the staff belongs to
     * @param position    Position/title of the staff
     */
    public AcademicStaff(int id, String username, String password, String firstName, String lastName,
                        String email, String phoneNumber, LocalDate dateOfBirth, String address,
                        String staffId, String department, String position) {
        super(id, username, password, firstName, lastName, email, phoneNumber, dateOfBirth, address);
        this.staffId = staffId;
        this.department = department;
        this.position = position;
        this.courses = new ArrayList<>();
    }

    // Getters and Setters
    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    /**
     * Add a course to the academic staff's list of courses
     *
     * @param course The course to add
     * @return true if the course was added successfully, false otherwise
     */
    public boolean addCourse(Course course) {
        if (!courses.contains(course)) {
            courses.add(course);
            return true;
        }
        return false;
    }

    /**
     * Remove a course from the academic staff's list of courses
     *
     * @param course The course to remove
     * @return true if the course was removed successfully, false otherwise
     */
    public boolean removeCourse(Course course) {
        return courses.remove(course);
    }

    @Override
    public String getRole() {
        return "Academic Staff";
    }

    @Override
    public String toString() {
        return "AcademicStaff{" +
                "id=" + getId() +
                ", username='" + getUsername() + '\'' +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", staffId='" + staffId + '\'' +
                ", department='" + department + '\'' +
                ", position='" + position + '\'' +
                ", courses=" + courses.size() +
                '}';
    }
}