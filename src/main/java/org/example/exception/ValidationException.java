/**
 * @author Group 9
 */
package org.example.exception;

/**
 * Exception thrown when validation of input data fails.
 */
public class ValidationException extends RuntimeException {
    
    /**
     * Constructs a new validation exception with the specified detail message.
     *
     * @param message The detail message
     */
    public ValidationException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new validation exception with the specified detail message and cause.
     *
     * @param message The detail message
     * @param cause   The cause
     */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}