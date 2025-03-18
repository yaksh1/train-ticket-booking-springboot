package com.yaksh.train_ticket.exceptions;

import com.yaksh.train_ticket.enums.ResponseStatus;
public class CustomException extends RuntimeException {
    private final ResponseStatus errorCode;

    public CustomException(String message,ResponseStatus errorCode) {
      super(message);
      this.errorCode = errorCode;
    }

    public ResponseStatus getErrorCode() {
      return errorCode;
    }
  
    
    
}
