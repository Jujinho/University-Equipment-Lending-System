/**
 * @author Group 9
 */
package org.example.service;

import org.example.db.UserRepository;
import org.example.model.*;

import java.util.List;
import java.util.Optional;

/**
 * Service class for user-related operations.
 */
public class UserService {

    private final UserRepository userRepository;

    /**
     * Constructor
     *
     * @param userRepository The UserRepository to use for user operations
     */
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Get a user by ID
     *
     * @param id The user ID
     * @return An Optional containing the User if found, or empty if not found
     */
    public Optional<User> getUserById(int id) {
        return userRepository.getUserById(id);
    }

    /**
     * Get a user by username
     *
     * @param username The username
     * @return An Optional containing the User if found, or empty if not found
     */
    public Optional<User> getUserByUsername(String username) {
        return userRepository.getUserByUsername(username);
    }

    /**
     * Get all users
     *
     * @return A list of all users
     */
    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    /**
     * Get all students
     *
     * @return A list of all students
     */
    public List<Student> getAllStudents() {
        return userRepository.getAllStudents();
    }

    /**
     * Get all academic staff
     *
     * @return A list of all academic staff
     */
    public List<AcademicStaff> getAllAcademicStaff() {
        return userRepository.getAllAcademicStaff();
    }

    /**
     * Get all professional staff
     *
     * @return A list of all professional staff
     */
    public List<ProfessionalStaff> getAllProfessionalStaff() {
        return userRepository.getAllProfessionalStaff();
    }

    /**
     * Get all administrators
     *
     * @return A list of all administrators
     */
    public List<Administrator> getAllAdministrators() {
        return userRepository.getAllAdministrators();
    }

    /**
     * Create a new user
     * The password will be hashed before storing
     *
     * @param user The user to create
     * @return true if the user was created successfully, false otherwise
     */
    public boolean createUser(User user) {
        // Hash the password before creating the user
        String hashedPassword = org.example.util.PasswordHasher.hashPassword(user.getPassword());
        user.setPassword(hashedPassword);
        return userRepository.createUser(user);
    }

    /**
     * Update a user
     * If the password has been changed, it will be hashed before storing
     *
     * @param user The user to update
     * @return true if the user was updated successfully, false otherwise
     */
    public boolean updateUser(User user) {
        // Get the existing user to check if the password has changed
        Optional<User> existingUserOpt = userRepository.getUserById(user.getId());
        if (!existingUserOpt.isPresent()) {
            return false;
        }

        User existingUser = existingUserOpt.get();
        String currentPassword = user.getPassword();

        // Check if the password has changed and is not already hashed
        if (currentPassword != null && !currentPassword.equals(existingUser.getPassword()) && !currentPassword.contains("$")) {
            // Hash the new password
            String hashedPassword = org.example.util.PasswordHasher.hashPassword(currentPassword);
            user.setPassword(hashedPassword);
        }

        return userRepository.updateUser(user);
    }

    /**
     * Delete a user
     *
     * @param id The ID of the user to delete
     * @return true if the user was deleted successfully, false otherwise
     */
    public boolean deleteUser(int id) {
        return userRepository.deleteUser(id);
    }

    /**
     * Check if a username is available
     *
     * @param username The username to check
     * @return true if the username is available, false otherwise
     */
    public boolean isUsernameAvailable(String username) {
        return !userRepository.getUserByUsername(username).isPresent();
    }

    /**
     * Check if an email is available
     *
     * @param email The email to check
     * @return true if the email is available, false otherwise
     */
    public boolean isEmailAvailable(String email) {
        // This would require a new method in the repository to check by email
        // For now, we'll check all users manually
        List<User> users = userRepository.getAllUsers();
        return users.stream().noneMatch(user -> user.getEmail().equalsIgnoreCase(email));
    }

    /**
     * Update a user's personal information
     *
     * @param user The user with updated information
     * @return true if the update was successful, false otherwise
     */
    public boolean updatePersonalInfo(User user) {
        // Get the existing user
        Optional<User> existingUserOpt = userRepository.getUserById(user.getId());
        if (!existingUserOpt.isPresent()) {
            return false;
        }

        User existingUser = existingUserOpt.get();

        // Update only personal information fields
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setEmail(user.getEmail());
        existingUser.setPhoneNumber(user.getPhoneNumber());
        existingUser.setDateOfBirth(user.getDateOfBirth());
        existingUser.setAddress(user.getAddress());

        return userRepository.updateUser(existingUser);
    }

    /**
     * Change a user's password
     * Verifies the old password and hashes the new password before storing
     *
     * @param userId      The ID of the user
     * @param oldPassword The old password
     * @param newPassword The new password
     * @return true if the password was changed successfully, false otherwise
     */
    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        // Get the user
        Optional<User> userOpt = userRepository.getUserById(userId);
        if (!userOpt.isPresent()) {
            return false;
        }

        User user = userOpt.get();
        String storedPassword = user.getPassword();

        // Check if the old password is correct
        boolean passwordMatches;
        if (storedPassword.contains("$")) {
            // Verify using PasswordHasher
            passwordMatches = org.example.util.PasswordHasher.verifyPassword(oldPassword, storedPassword);
        } else {
            // Legacy plain text comparison (for backward compatibility)
            passwordMatches = oldPassword.equals(storedPassword);
        }

        if (!passwordMatches) {
            return false;
        }

        // Hash the new password
        String hashedPassword = org.example.util.PasswordHasher.hashPassword(newPassword);

        // Update the password
        user.setPassword(hashedPassword);
        return userRepository.updateUser(user);
    }
}
