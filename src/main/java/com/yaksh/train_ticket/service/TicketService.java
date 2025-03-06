package com.yaksh.train_ticket.service;

import com.yaksh.train_ticket.model.Ticket;

import java.util.Optional;

public interface TicketService {
    Ticket saveTicket(Ticket ticketToSave);
    Optional<Ticket> findTicketById(String idOfTicketToFind);
    void deleteTicketById(String idOfTicketToDelete);
}
