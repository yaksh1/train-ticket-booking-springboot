package com.yaksh.train_ticket.exceptions;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(CustomException.class)
  public ResponseEntity<ErrorDetails> handleCustomException(CustomException ex) {

    ErrorDetails errorDetails = ErrorDetails.builder()
        .status(ex.getErrorCode().getHttpStatus().name())
        .errorMessage(ex.getMessage())
        .statusCode(ex.getErrorCode().getHttpStatus().value())
        .errorCode(ex.getErrorCode().name())
        .timeStamp(LocalDateTime.now())
        .build();

    return new ResponseEntity<>(errorDetails, ex.getErrorCode().getHttpStatus());
  }
@ExceptionHandler(Exception.class)
public ResponseEntity<ErrorDetails> handleGlobalException(Exception ex) {
    ErrorDetails errorDetails = ErrorDetails.builder()
        .status(
            HttpStatus.INTERNAL_SERVER_ERROR.name())
        .errorMessage(ex.getMessage())
        .statusCode(500)
        .errorCode("INTERNAL_ERROR")
        .timeStamp(LocalDateTime.now())
        .build();

    return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
}

}
