package com.yaksh.train_ticket.DTO;

import com.yaksh.train_ticket.enums.ResponseStatus;
import com.yaksh.train_ticket.exceptions.CustomException;

public class CommonResponsesDTOs {
    public static void userNotLoggedInDTO() {
        throw new CustomException("Please log in to book the ticket", ResponseStatus.USER_NOT_FOUND);
    }

    public static void notEnoughSeatsDTO() {
        throw new CustomException("Not enough seats available", ResponseStatus.NOT_ENOUGH_SEATS);
    }

    public static void trainDoesNotExistDTO(String prn) {
        throw new CustomException("Train does not exist with PRN: " + prn, ResponseStatus.TRAIN_NOT_FOUND);
    }

    public static void ticketNotFoundDTO(String ticketId) {
        throw new CustomException(String.format("Ticket ID: %s not found", ticketId), ResponseStatus.TICKET_NOT_FOUND);
    }

    public static void trainNotAddedToCollectionDTO(String message) {
        throw new CustomException("Error while saving the train: " + message,
                ResponseStatus.TRAIN_NOT_SAVED_IN_COLLECTION);
    }

    public static void dateIsInThePastDTO() {
        throw new CustomException("Date of travel cannot be in the past", ResponseStatus.INVALID_DATA);
    }



}
