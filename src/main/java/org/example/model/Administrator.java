/**
 * @author Group 9
 */
package org.example.model;

import java.time.LocalDate;

/**
 * Represents an administrator in the system.
 * Administrators have full access to all functionalities, including CRUD operations on all entities.
 * They can upload images for equipment and access system-level statistics.
 */
public class Administrator extends User {
    private String adminId;
    private String department;
    private String position;
    private String accessLevel;

    /**
     * Default constructor
     */
    public Administrator() {
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
     * @param adminId     Administrator's unique ID
     * @param department  Department the administrator belongs to
     * @param position    Position/title of the administrator
     * @param accessLevel Access level of the administrator (e.g., "Full", "Limited")
     */
    public Administrator(int id, String username, String password, String firstName, String lastName,
                        String email, String phoneNumber, LocalDate dateOfBirth, String address,
                        String adminId, String department, String position, String accessLevel) {
        super(id, username, password, firstName, lastName, email, phoneNumber, dateOfBirth, address);
        this.adminId = adminId;
        this.department = department;
        this.position = position;
        this.accessLevel = accessLevel;
    }

    // Getters and Setters
    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
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

    public String getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(String accessLevel) {
        this.accessLevel = accessLevel;
    }

    @Override
    public String getRole() {
        return "Administrator";
    }

    @Override
    public String toString() {
        return "Administrator{" +
                "id=" + getId() +
                ", username='" + getUsername() + '\'' +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", adminId='" + adminId + '\'' +
                ", department='" + department + '\'' +
                ", position='" + position + '\'' +
                ", accessLevel='" + accessLevel + '\'' +
                '}';
    }
}