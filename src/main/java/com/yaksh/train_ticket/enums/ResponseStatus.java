package com.yaksh.train_ticket.enums;

import org.springframework.http.HttpStatus;

public enum ResponseStatus {
    // users
    USER_NOT_FOUND(HttpStatus.NOT_FOUND),
    USER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST),
    USER_NOT_SAVED_IN_COLLECTION(HttpStatus.INTERNAL_SERVER_ERROR),
    PASSWORD_INCORRECT(HttpStatus.BAD_REQUEST),

    // trains
    NOT_ENOUGH_SEATS(HttpStatus.INTERNAL_SERVER_ERROR),
    TRAIN_NOT_FOUND(HttpStatus.NOT_FOUND),
    TRAIN_ALREADY_EXISTS(HttpStatus.BAD_REQUEST),
    TRAIN_NOT_SAVED_IN_COLLECTION(HttpStatus.INTERNAL_SERVER_ERROR),
    TRAIN_UPDATING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR),

    // tickets
    TICKET_NOT_FOUND(HttpStatus.NOT_FOUND),
    TICKET_NOT_BOOKED(HttpStatus.INTERNAL_SERVER_ERROR),
    TICKET_NOT_CANCELLED(HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_DATA(HttpStatus.BAD_REQUEST),;

    
    private final HttpStatus httpStatus;

    ResponseStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

}
