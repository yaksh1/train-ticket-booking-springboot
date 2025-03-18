package com.yaksh.train_ticket.DTO;

import com.yaksh.train_ticket.enums.ResponseStatus;
import com.yaksh.train_ticket.exceptions.CustomException;

/**
 * CommonResponsesDTOs is a utility class that provides methods to throw
 * specific exceptions with predefined messages and response statuses.
 * These methods are used to handle various error scenarios in the application.
 */
public class CommonResponsesDTOs {

    /**
     * Throws an exception indicating that the user is not logged in.
     * This is used when a user attempts to perform an action without being authenticated.
     */
    public static void userNotLoggedInDTO() {
        // Throwing a custom exception with a specific message and response status
        throw new CustomException("Please log in to book the ticket", ResponseStatus.USER_NOT_FOUND);
    }

    /**
     * Throws an exception indicating that there are not enough seats available.
     * This is used when a user tries to book more seats than are available.
     */
    public static void notEnoughSeatsDTO() {
        // Throwing a custom exception with a specific message and response status
        throw new CustomException("Not enough seats available", ResponseStatus.NOT_ENOUGH_SEATS);
    }

    /**
     * Throws an exception indicating that the train with the specified PRN does not exist.
     *
     * @param prn The PRN (Passenger Reservation Number) of the train that was not found.
     */
    public static void trainDoesNotExistDTO(String prn) {
        // Throwing a custom exception with a dynamic message including the PRN
        throw new CustomException("Train does not exist with PRN: " + prn, ResponseStatus.TRAIN_NOT_FOUND);
    }

    /**
     * Throws an exception indicating that the ticket with the specified ID was not found.
     *
     * @param ticketId The ID of the ticket that was not found.
     */
    public static void ticketNotFoundDTO(String ticketId) {
        // Throwing a custom exception with a formatted message including the ticket ID
        throw new CustomException(String.format("Ticket ID: %s not found", ticketId), ResponseStatus.TICKET_NOT_FOUND);
    }

    /**
     * Throws an exception indicating that there was an error while saving the train to the collection.
     *
     * @param message A detailed message describing the error.
     */
    public static void trainNotAddedToCollectionDTO(String message) {
        // Throwing a custom exception with a dynamic message including the error details
        throw new CustomException("Error while saving the train: " + message,
                ResponseStatus.TRAIN_NOT_SAVED_IN_COLLECTION);
    }

    /**
     * Throws an exception indicating that the date of travel is in the past.
     * This is used to ensure that users cannot book tickets for past dates.
     */
    public static void dateIsInThePastDTO() {
        // Throwing a custom exception with a specific message and response status
        throw new CustomException("Date of travel cannot be in the past", ResponseStatus.INVALID_DATA);
    }
}