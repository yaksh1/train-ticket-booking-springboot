package com.yaksh.train_ticket.service;

import com.yaksh.train_ticket.DTO.ResponseDataDTO;
import com.yaksh.train_ticket.model.Ticket;
import com.yaksh.train_ticket.model.Train;
import com.yaksh.train_ticket.model.User;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;


public interface UserBookingService {
    void setLoggedInUser(User user);
    User getLoggedInUser();
    List<User> getUserList();
    ResponseDataDTO loginUser(String userName, String Password);
    ResponseDataDTO signupUser(String userName,String password);
    ResponseDataDTO bookTicket(String trainPrn, String source, String destination, LocalDate dateOfTravel, int numberOfSeatsToBeBooked) ;
    ResponseDataDTO fetchAllTickets();
    ResponseDataDTO cancelTicket(String IdOfTicketToCancel);
    ResponseDataDTO fetchTicketById(String IdOfTicketToFind);
    ResponseDataDTO rescheduleTicket(String ticketId, LocalDate updatedTravelDate);
;
    // also update booking logic with dates (can book at different dates with date specified train seats)

}
