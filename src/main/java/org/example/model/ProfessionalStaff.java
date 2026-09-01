/**
 * @author Group 9
 */
package org.example.model;

import java.time.LocalDate;

/**
 * Represents a professional staff member in the university.
 * Professional staff can manage their personal information, borrow equipment for themselves,
 * and retrieve their borrowing history with filters.
 */
public class ProfessionalStaff extends User {
    private String staffId;
    private String department;
    private String position;
    private String specialization;

    /**
     * Default constructor
     */
    public ProfessionalStaff() {
    }

    /**
     * Parameterized constructor
     *
     * @param id             Unique identifier for the user
     * @param username       Username for login
     * @param password       Password for login
     * @param firstName      User's first name
     * @param lastName       User's last name
     * @param email          User's email address
     * @param phoneNumber    User's phone number
     * @param dateOfBirth    User's date of birth
     * @param address        User's address
     * @param staffId        Staff's unique ID
     * @param department     Department the staff belongs to
     * @param position       Position/title of the staff
     * @param specialization Staff's area of specialization
     */
    public ProfessionalStaff(int id, String username, String password, String firstName, String lastName,
                            String email, String phoneNumber, LocalDate dateOfBirth, String address,
                            String staffId, String department, String position, String specialization) {
        super(id, username, password, firstName, lastName, email, phoneNumber, dateOfBirth, address);
        this.staffId = staffId;
        this.department = department;
        this.position = position;
        this.specialization = specialization;
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

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    @Override
    public String getRole() {
        return "Professional Staff";
    }

    @Override
    public String toString() {
        return "ProfessionalStaff{" +
                "id=" + getId() +
                ", username='" + getUsername() + '\'' +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", staffId='" + staffId + '\'' +
                ", department='" + department + '\'' +
                ", position='" + position + '\'' +
                ", specialization='" + specialization + '\'' +
                '}';
    }
}