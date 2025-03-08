package com.yaksh.train_ticket.DTO;

import com.yaksh.train_ticket.enums.ResponseStatus;

public class CommonResponsesDTOs {
    public static ResponseDataDTO userNotLoggedInDTO(){
        return new ResponseDataDTO(false, ResponseStatus.USER_NOT_FOUND,"Please log in to book the ticket");
    }

    public static ResponseDataDTO notEnoughSeatsDTO(){
        return new ResponseDataDTO(false, ResponseStatus.NOT_ENOUGH_SEATS,"Not enough seats available");
    }

    public static ResponseDataDTO trainDoesNotExistDTO(String prn){
        return new ResponseDataDTO(false,ResponseStatus.TRAIN_DOES_NOT_EXISTS,"Train does not exist with prn: " + prn);
    }
    public static ResponseDataDTO ticketNotFoundDTO(String ticketId){
        return new ResponseDataDTO(false,ResponseStatus.TICKET_NOT_FOUND,String.format("Ticket ID: %s not found",ticketId));
    }
    public static ResponseDataDTO trainNotAddedToCollectionDTO(String message){
        return new ResponseDataDTO(false,ResponseStatus.TRAIN_NOT_SAVED_IN_COLLECTION,"Error while saving the train: "+message);
    }



}
