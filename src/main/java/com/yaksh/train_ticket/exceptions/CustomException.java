package com.yaksh.train_ticket.exceptions;

import com.yaksh.train_ticket.enums.ResponseStatus;

/**
 * CustomException is a custom runtime exception class that encapsulates
 * an error message and a specific error code of type ResponseStatus.
 */
public class CustomException extends RuntimeException {
    // Holds the error code associated with this exception
    private ResponseStatus errorCode;
    // Holds the detailed error message associated with this exception
    private String message;

    /**
     * Constructs a new CustomException with the specified error code.
     *
     * @param errorCode The error code of type ResponseStatus associated with the exception.
     */
    public CustomException(ResponseStatus errorCode) {
        this.errorCode = errorCode; // Initialize the error code
    }
    
    /**
     * Constructs a new CustomException with the specified detail message
     * and error code.
     *
     * @param message   The detail message for the exception.
     * @param errorCode The error code of type ResponseStatus associated with the exception.
     */
    public CustomException(String message, ResponseStatus errorCode) {
        this.message = message; // Initialize the detailed error message
        this.errorCode = errorCode; // Initialize the error code
    }

    /**
     * Retrieves the error code associated with this exception.
     *
     * @return The error code of type ResponseStatus.
     */
    public ResponseStatus getErrorCode() {
        return errorCode; // Return the stored error code
    }
    
    /**
     * Retrieves the detailed error message associated with this exception.
     *
     * @return The detailed error message as a String.
     */
    public String getMessage() {
        return message; // Return the stored error message
    }
}