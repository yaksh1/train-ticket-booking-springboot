package com.yaksh.train_ticket.enums;

import org.springframework.http.HttpStatus;

public enum ResponseStatus {
    // Users
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not found"),
    USER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "User already exists"),
    USER_NOT_SAVED_IN_COLLECTION(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to save user in collection"),
    PASSWORD_INCORRECT(HttpStatus.BAD_REQUEST, "Incorrect password"),

    // Trains
    NOT_ENOUGH_SEATS(HttpStatus.INTERNAL_SERVER_ERROR, "Not enough seats available"),
    TRAIN_NOT_FOUND(HttpStatus.NOT_FOUND, "Train not found"),
    TRAIN_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "Train already exists"),
    TRAIN_NOT_SAVED_IN_COLLECTION(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to save train in collection"),
    TRAIN_UPDATING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Train update failed"),

    // Tickets
    TICKET_NOT_FOUND(HttpStatus.NOT_FOUND, "Ticket not found"),
    TICKET_NOT_BOOKED(HttpStatus.INTERNAL_SERVER_ERROR, "Ticket booking failed"),
    TICKET_NOT_CANCELLED(HttpStatus.INTERNAL_SERVER_ERROR, "Ticket cancellation failed"),
    TICKET_NOT_SAVED_IN_COLLECTION(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to save ticket in collection"),
    INVALID_DATA(HttpStatus.BAD_REQUEST, "Invalid input data");

    private final HttpStatus httpStatus;
    private final String message;

    ResponseStatus(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }
}