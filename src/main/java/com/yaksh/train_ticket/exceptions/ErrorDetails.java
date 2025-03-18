package com.yaksh.train_ticket.exceptions;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the structure for error details in the application.
 * This class is used to encapsulate error information such as status, message, code, and timestamp.
 * It is annotated with Lombok annotations to reduce boilerplate code.
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDetails {
  
  /** 
   * The status of the error (e.g., "FAILURE" or "ERROR").
   */
  private String status;
  
  /**
   * A descriptive error message providing details about the error.
   */
  private String errorMessage;
  
  /**
   * The HTTP status code associated with the error (e.g., 404, 500).
   */
  private int statusCode;
  
  /**
   * A specific error code that uniquely identifies the error type.
   */
  private String errorCode;
  
  /**
   * The timestamp indicating when the error occurred.
   */
  private LocalDateTime timeStamp;
}