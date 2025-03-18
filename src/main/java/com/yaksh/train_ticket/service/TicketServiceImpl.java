package com.yaksh.train_ticket.service;

import com.yaksh.train_ticket.DTO.ResponseDataDTO;
import com.yaksh.train_ticket.enums.ResponseStatus;
import com.yaksh.train_ticket.exceptions.CustomException;
import com.yaksh.train_ticket.model.Ticket;
import com.yaksh.train_ticket.repository.TicketRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * This service class handles all ticket-related operations, such as saving, retrieving, deleting, 
 * and creating new tickets. It acts as the business logic layer for managing train tickets.
 */
@Service
@RequiredArgsConstructor // Automatically generates a constructor for final fields.
@Slf4j // Provides logging capabilities for this class.
public class TicketServiceImpl implements TicketService {

    // Repository for accessing and manipulating ticket data in the database.
    private final TicketRepositoryV2 ticketRepositoryV2;

    /**
     * Saves a ticket to the database.
     * 
     * @param ticketToSave The ticket object to be saved.
     * @return The saved ticket object if successful, or null if an error occurs.
     */
    @Override
    public Ticket saveTicket(Ticket ticketToSave) {
        try {
            // Attempt to save the ticket to the database.
            Ticket ticket = ticketRepositoryV2.save(ticketToSave);

            // Log success if the ticket is saved successfully.
            log.info("Ticket saved successfully with id: {}", ticket.getTicketId());
            return ticket;
        } catch (Exception e) {
            // Log any exceptions that occur during the save operation.
            log.error("Error while saving ticket: {}", e.getMessage());
            throw new CustomException("Error while saving ticket: "+e.getMessage(),
                    ResponseStatus.TICKET_NOT_SAVED_IN_COLLECTION);
        }
    }

    /**
     * Finds a ticket by its unique ID.
     * 
     * @param idOfTicketToFind The unique ID of the ticket to find.
     * @return An Optional containing the ticket if found, or empty if not found.
     */
    @Override
    public Optional<Ticket> findTicketById(String idOfTicketToFind) {
        // Directly delegates to the repository's findById method.
        return ticketRepositoryV2.findById(idOfTicketToFind);
    }

    /**
     * Deletes a ticket by its unique ID.
     * 
     * @param idOfTicketToDelete The unique ID of the ticket to delete.
     */
    @Override
    public void deleteTicketById(String idOfTicketToDelete) {
        // Deletes the ticket with the given ID from the database.
        ticketRepositoryV2.deleteById(idOfTicketToDelete);
    }

    /**
     * Creates a new ticket with the given details and saves it to the database.
     * 
     * @param userId                  The ID of the user booking the ticket.
     * @param trainPrn                The train's PRN (Passenger Reservation Number).
     * @param dateOfTravel            The date of travel for the ticket.
     * @param source                  The source station.
     * @param destination             The destination station.
     * @param availableSeatsList      A list of available seats for the journey.
     * @param arrivalTimeAtSource     The arrival time of the train at the source station.
     * @param reachingTimeAtDestination The reaching time of the train at the destination station.
     * @return The saved ticket object.
     */
    @Override
    public Ticket createNewTicket(
            String userId,
            String trainPrn,
            LocalDate dateOfTravel,
            String source,
            String destination,
            List<List<Integer>> availableSeatsList,
            LocalDateTime arrivalTimeAtSource,
            LocalDateTime reachingTimeAtDestination
    ) {
        // Generate a new ticket object with the provided details.
        Ticket ticket = new Ticket(
                UUID.randomUUID().toString(), // Generate a unique ticket ID using UUID.
                userId,
                trainPrn,
                dateOfTravel,
                source,
                arrivalTimeAtSource,
                destination,
                reachingTimeAtDestination,
                availableSeatsList
        );

        // Save the newly created ticket to the database and return it.
        return saveTicket(ticket);
    }
}