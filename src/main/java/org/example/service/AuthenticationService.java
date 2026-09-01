/**
 * @author Group 9
 */
package org.example.service;

import org.example.db.UserRepository;
import org.example.model.*;

import java.util.Optional;

/**
 * Service class for user authentication and session management.
 */
public class AuthenticationService {
    
    private final UserRepository userRepository;
    private User currentUser;
    
    /**
     * Constructor
     *
     * @param userRepository The UserRepository to use for retrieving user information
     */
    public AuthenticationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    /**
     * Authenticate a user by username and password
     *
     * @param username The username
     * @param password The password
     * @return true if authentication was successful, false otherwise
     */
    public boolean authenticate(String username, String password) {
        Optional<User> userOpt = userRepository.authenticate(username, password);
        if (userOpt.isPresent()) {
            currentUser = userOpt.get();
            return true;
        }
        return false;
    }
    
    /**
     * Log out the current user
     */
    public void logout() {
        currentUser = null;
    }
    
    /**
     * Get the current user
     *
     * @return The current user, or null if no user is logged in
     */
    public User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Check if a user is logged in
     *
     * @return true if a user is logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    /**
     * Check if the current user is a student
     *
     * @return true if the current user is a student, false otherwise
     */
    public boolean isStudent() {
        return isLoggedIn() && currentUser instanceof Student;
    }
    
    /**
     * Check if the current user is an academic staff
     *
     * @return true if the current user is an academic staff, false otherwise
     */
    public boolean isAcademicStaff() {
        return isLoggedIn() && currentUser instanceof AcademicStaff;
    }
    
    /**
     * Check if the current user is a professional staff
     *
     * @return true if the current user is a professional staff, false otherwise
     */
    public boolean isProfessionalStaff() {
        return isLoggedIn() && currentUser instanceof ProfessionalStaff;
    }
    
    /**
     * Check if the current user is an administrator
     *
     * @return true if the current user is an administrator, false otherwise
     */
    public boolean isAdministrator() {
        return isLoggedIn() && currentUser instanceof Administrator;
    }
    
    /**
     * Get the current user as a student
     *
     * @return The current user as a student, or null if the current user is not a student
     */
    public Student getCurrentStudent() {
        return isStudent() ? (Student) currentUser : null;
    }
    
    /**
     * Get the current user as an academic staff
     *
     * @return The current user as an academic staff, or null if the current user is not an academic staff
     */
    public AcademicStaff getCurrentAcademicStaff() {
        return isAcademicStaff() ? (AcademicStaff) currentUser : null;
    }
    
    /**
     * Get the current user as a professional staff
     *
     * @return The current user as a professional staff, or null if the current user is not a professional staff
     */
    public ProfessionalStaff getCurrentProfessionalStaff() {
        return isProfessionalStaff() ? (ProfessionalStaff) currentUser : null;
    }
    
    /**
     * Get the current user as an administrator
     *
     * @return The current user as an administrator, or null if the current user is not an administrator
     */
    public Administrator getCurrentAdministrator() {
        return isAdministrator() ? (Administrator) currentUser : null;
    }
}