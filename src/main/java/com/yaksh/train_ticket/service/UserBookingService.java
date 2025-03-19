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

/**
 * Interface representing the user booking service for train ticketing.
 * It provides methods for user authentication, ticket booking, ticket management, and user management.
 */
public interface UserBookingService {

    /**
     * Sets the currently logged-in user.
     * This is used to maintain the session state of the user interacting with the system.
     * @param user The user to set as the logged-in user.
     */
    void setLoggedInUser(User user);

    /**
     * Retrieves the currently logged-in user.
     * Useful for accessing user-specific information during the session.
     * @return The logged-in user.
     */
    User getLoggedInUser();

    /**
     * Retrieves the list of all users.
     * This can be used for administrative purposes or to display user information.
     * @return A list of users.
     */
    List<User> getUserList();

    /**
     * Authenticates a user using their email and password.
     * Validates the credentials and returns a response indicating success or failure.
     * @param userEmail The email of the user.
     * @param Password The password of the user.
     * @return A ResponseDataDTO containing the result of the login operation.
     */
    ResponseDataDTO loginUser(String userEmail, String Password);

    /**
     * Registers a new user with the given email and password.
     * Ensures that the email is unique and stores user information securely.
     * @param userEmail The email of the new user.
     * @param password The password of the new user.
     * @return A ResponseDataDTO containing the result of the signup operation.
     */
    ResponseDataDTO signupUser(String userEmail, String password);

    /**
     * Books a train ticket for the specified train, source, destination, date of travel, and number of seats.
     * Checks for seat availability and processes the booking if possible.
     * @param trainPrn The PRN (Passenger Reservation Number) of the train.
     * @param source The source station.
     * @param destination The destination station.
     * @param dateOfTravel The date of travel.
     * @param numberOfSeatsToBeBooked The number of seats to book.
     * @return A ResponseDataDTO containing the result of the booking operation.
     */
    ResponseDataDTO bookTicket(String trainPrn, String source, String destination, LocalDate dateOfTravel, int numberOfSeatsToBeBooked);

    /**
     * Fetches all tickets booked by the logged-in user.
     * This provides a history of all bookings made by the user.
     * @return A ResponseDataDTO containing the list of all tickets.
     */
    ResponseDataDTO fetchAllTickets();

    /**
     * Cancels a ticket with the given ticket ID.
     * The cancellation process might involve refunding the user based on the cancellation policy.
     * @param IdOfTicketToCancel The ID of the ticket to cancel.
     * @return A ResponseDataDTO containing the result of the cancellation operation.
     */
    ResponseDataDTO cancelTicket(String IdOfTicketToCancel);

    /**
     * Fetches a ticket by its ID.
     * Useful for retrieving specific ticket details for the logged-in user.
     * @param IdOfTicketToFind The ID of the ticket to fetch.
     * @return A ResponseDataDTO containing the ticket details.
     */
    ResponseDataDTO fetchTicketById(String IdOfTicketToFind);

    /**
     * Reschedules a ticket to a new travel date.
     * Ensures that the new date is valid and that seats are available for rescheduling.
     * @param ticketId The ID of the ticket to reschedule.
     * @param updatedTravelDate The new travel date.
     * @return A ResponseDataDTO containing the result of the rescheduling operation.
     */
    ResponseDataDTO rescheduleTicket(String ticketId, LocalDate updatedTravelDate);

    // Note: Booking logic should support booking tickets for different dates, with train seat availability checked for the specified date.
}