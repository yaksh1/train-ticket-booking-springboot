package com.yaksh.train_ticket.service;

import com.yaksh.train_ticket.DTO.ResponseDataDTO;
import com.yaksh.train_ticket.model.Ticket;
import com.yaksh.train_ticket.model.Train;
import com.yaksh.train_ticket.model.User;
import org.springframework.stereotype.Service;


public interface UserBookingService {
    void setLoggedInUser(User user);
    User getLoggedInUser();
    ResponseDataDTO loginUser(String userName, String Password);
    ResponseDataDTO signupUSer(User user);
    ResponseDataDTO bookTicket(Train train,String source,String destination);
    ResponseDataDTO FetchAllTickets();
    ResponseDataDTO cancelTicket(Ticket ticket);
}
