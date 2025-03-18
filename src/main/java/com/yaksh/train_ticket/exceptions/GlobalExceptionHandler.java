package com.yaksh.train_ticket.exceptions;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * GlobalExceptionHandler is a centralized exception handling class for the application.
 * It intercepts exceptions thrown by the application and returns a structured error response.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles exceptions of type CustomException.
     * 
     * @param ex the CustomException thrown by the application
     * @return ResponseEntity containing the error details and the corresponding HTTP status
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorDetails> handleCustomException(CustomException ex) {

        // Creating an ErrorDetails object with relevant information about the exception
        ErrorDetails errorDetails = ErrorDetails.builder()
            .status(ex.getErrorCode().getHttpStatus().name()) // HTTP status as a string
            .errorMessage(ex.getMessage()) // Error message from the exception
            .statusCode(ex.getErrorCode().getHttpStatus().value()) // HTTP status code as an integer
            .errorCode(ex.getErrorCode().name()) // Custom error code
            .timeStamp(LocalDateTime.now()) // Current timestamp
            .build();

        // Returning the ErrorDetails object with the appropriate HTTP status
        return new ResponseEntity<>(errorDetails, ex.getErrorCode().getHttpStatus());
    }

    /**
     * Handles all other exceptions that are not explicitly handled.
     * 
     * @param ex the exception thrown by the application
     * @return ResponseEntity containing the error details and a generic HTTP 500 status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGlobalException(Exception ex) {

        // Creating an ErrorDetails object with generic information for unhandled exceptions
        ErrorDetails errorDetails = ErrorDetails.builder()
            .status(HttpStatus.INTERNAL_SERVER_ERROR.name()) // HTTP 500 status as a string
            .errorMessage(ex.getMessage()) // Error message from the exception
            .statusCode(500) // HTTP 500 status code as an integer
            .errorCode("INTERNAL_ERROR") // Generic error code for internal server errors
            .timeStamp(LocalDateTime.now()) // Current timestamp
            .build();

        // Returning the ErrorDetails object with HTTP 500 status
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}