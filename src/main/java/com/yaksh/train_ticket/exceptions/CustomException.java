package com.yaksh.train_ticket.exceptions;

import com.yaksh.train_ticket.enums.ResponseStatus;

/**
 * CustomException is a custom runtime exception class that encapsulates
 * an error message and a specific error code of type ResponseStatus.
 */
public class CustomException extends RuntimeException {
    // Holds the error code associated with this exception
    private ResponseStatus errorCode;
    private String message;

    /**
     * Constructs a new CustomException with the specified detail message
     * and error code.
     *
     * @param message   The detail message for the exception.
     * @param errorCode The error code of type ResponseStatus associated with the exception.
     */
    public CustomException(ResponseStatus errorCode) {
      this.errorCode = errorCode; // Initialize the error code
    }
    
    public CustomException(String message, ResponseStatus errorCode) {
      this.message = message;
      this.errorCode = errorCode; 
    }

    /**
     * Retrieves the error code associated with this exception.
     *
     * @return The error code of type ResponseStatus.
     */
    public ResponseStatus getErrorCode() {
      return errorCode; // Return the stored error code
    }
    
    public String getMessage() {
      return message;
    }
}