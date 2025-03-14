package com.yaksh.train_ticket.service;

import com.yaksh.train_ticket.DTO.ResponseDataDTO;
import com.yaksh.train_ticket.model.Ticket;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface TicketService {
    Ticket saveTicket(Ticket ticketToSave);
    Optional<Ticket> findTicketById(String idOfTicketToFind);
    void deleteTicketById(String idOfTicketToDelete);
    Ticket createNewTicket(String userId, String trainPrn, LocalDate dateOfTravel, String source, String destination, List<List<Integer>> availableSeatsList, LocalDateTime arrivalTimeAtSource, LocalDateTime reachingTimeAtDestination);
}
