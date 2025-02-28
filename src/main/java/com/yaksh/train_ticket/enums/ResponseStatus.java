package com.yaksh.train_ticket.enums;

public enum ResponseStatus {
    // users
    USER_NOT_FOUND,
    USER_ALREADY_EXISTS,
    USER_NOT_SAVED_IN_FILE,
    PASSWORD_INCORRECT,

    // trains
    NOT_ENOUGH_SEATS,
    TRAIN_DOES_NOT_EXISTS,
    TRAIN_NOT_SAVED_IN_FILE,
    TRAIN_UPDATING_FAILED,

    // tickets
    TICKET_NOT_FOUND,
    TICKET_NOT_BOOKED,
    TICKET_NOT_CANCELLED, INVALID_DATA,

}
