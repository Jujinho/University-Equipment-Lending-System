/**
 * @author Group 9
 */
package org.example.model;

import java.time.LocalDate;

/**
 * Base class for all users in the system.
 * This class represents common attributes and behaviors for all users.
 */
public abstract class User {
    private int id;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String address;

    /**
     * Default constructor
     */
    public User() {
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
     */
    public User(int id, String username, String password, String firstName, String lastName,
                String email, String phoneNumber, LocalDate dateOfBirth, String address) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Get the full name of the user
     *
     * @return Full name (first name + last name)
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Abstract method to get the role of the user
     *
     * @return Role of the user
     */
    public abstract String getRole();

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", address='" + address + '\'' +
                '}';
    }
}