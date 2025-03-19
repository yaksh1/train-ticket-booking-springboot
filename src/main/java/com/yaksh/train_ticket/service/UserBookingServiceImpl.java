package com.yaksh.train_ticket.service;

import com.yaksh.train_ticket.DTO.ResponseDataDTO;
import com.yaksh.train_ticket.enums.ResponseStatus;
import com.yaksh.train_ticket.exceptions.CustomException;
import com.yaksh.train_ticket.model.Ticket;
import com.yaksh.train_ticket.model.Train;
import com.yaksh.train_ticket.model.User;
import com.yaksh.train_ticket.repository.UserRepositoryV2;
import com.yaksh.train_ticket.util.UserServiceUtil;
import com.yaksh.train_ticket.util.ValidationChecks;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

/**
 * Service implementation for user booking-related operations.
 * Handles user login, signup, ticket booking, ticket cancellation,
 * fetching tickets, and rescheduling tickets.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserBookingServiceImpl implements UserBookingService {
    private User loggedInUser; // Currently logged-in user
    private final UserServiceUtil userServiceUtil;
    private final ValidationChecks validationChecks;
    private final UserRepositoryV2 userRepositoryV2;

    // Dependencies for ticket and train services
    private final TicketService ticketService;
    private final TrainService trainService;

    /**
     * Sets the logged-in user.
     *
     * @param user The user object to set as logged-in.
     */
    @Override
    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
        log.info("User logged in as: {}", user.getUserEmail());
    }

    /**
     * Gets the currently logged-in user.
     *
     * @return The logged-in user object.
     */
    @Override
    public User getLoggedInUser() {
        log.debug("Retrieving logged in user");
        return this.loggedInUser;
    }

    /**
     * Retrieves the list of all users.
     *
     * @return List of all users.
     */
    @Override
    public List<User> getUserList() {
        log.info("Retrieving all users from repository");
        return userRepositoryV2.findAll();
    }

    /**
     * Logs in a user with the provided username and password.
     *
     * @param userEmail The username of the user.
     * @param password The password of the user.
     * @return ResponseDataDTO containing login result.
     */
    @Override
    public ResponseDataDTO loginUser(String userEmail, String password) {
        log.info("Login attempt for user: {}", userEmail);
        // check if email is valid
        if (!validationChecks.isValidEmail(userEmail)) {
            log.warn("Signup failed - email is not valid: {}", userEmail);
            throw new CustomException(ResponseStatus.EMAIL_NOT_VALID);
        }
        return userRepositoryV2.findByUserEmail(userEmail.toLowerCase())
                .map(user -> {
                    if (!userServiceUtil.checkPassword(password, user.getHashedPassword())) {
                        throw new CustomException(ResponseStatus.PASSWORD_INCORRECT);
                    }
                    return new ResponseDataDTO(true, "User Found", user);
                })
                .orElseThrow(() -> new CustomException(ResponseStatus.USER_NOT_FOUND));
    }

    /**
     * Signs up a new user with the provided username and password.
     *
     * @param userEmail The username of the new user.
     * @param password The password of the new user.
     * @return ResponseDataDTO containing signup result.
     */
    @Override
    public ResponseDataDTO signupUser(String userEmail, String password) {
        log.info("Signup attempt for user: {}", userEmail);
        // check if email is valid
        if (!validationChecks.isValidEmail(userEmail)) {
            log.warn("Signup failed - email is not valid: {}", userEmail);
            throw new CustomException(ResponseStatus.EMAIL_NOT_VALID);
        }
        // Check if the user already exists
        if (validationChecks.isUserPresent(userEmail)) {
            log.warn("Signup failed - user already exists: {}", userEmail);
            throw new CustomException(ResponseStatus.USER_ALREADY_EXISTS);
        }
        try {
            // Create a new user and hash the password
            User user = new User(UUID.randomUUID().toString(), userEmail,
                    userServiceUtil.hashPassword(password), new ArrayList<>());

            // Save the user in the repository
            User savedUser = userRepositoryV2.save(user);

            return new ResponseDataDTO(true, "User Saved in the collection", savedUser);
        } catch (Exception e) {
            log.error("Error while saving user in the collection: {}", e.getMessage(), e);
            throw new CustomException("Error while saving user in the collection: " + e.getMessage(),
                    ResponseStatus.USER_NOT_SAVED_IN_COLLECTION);
        }
    }

    /**
     * Books a ticket for the logged-in user.
     *
     * @param trainPrn              The train's PRN.
     * @param source                The source station.
     * @param destination           The destination station.
     * @param dateOfTravel          The travel date.
     * @param numberOfSeatsToBeBooked The number of seats to be booked.
     * @return ResponseDataDTO containing booking result.
     */
    @Override
    public ResponseDataDTO bookTicket(String trainPrn, String source, String destination,
                                      LocalDate dateOfTravel, int numberOfSeatsToBeBooked) {
        log.info("Booking attempt - Train: {}, Seats: {}", trainPrn, numberOfSeatsToBeBooked);

        // Ensure the user is logged in
        if (loggedInUser == null) {
            log.warn("Unauthorized booking attempt - no logged in user");
            throw new CustomException("Please log in to book the ticket", ResponseStatus.USER_NOT_FOUND);
        }

        // Ensure the travel date is not in the past
        if (dateOfTravel.isBefore(LocalDate.now())) {
            throw new CustomException("Date of travel cannot be in the past", ResponseStatus.INVALID_DATA);
        }

        // Check if the train can be booked
        ResponseDataDTO canBeBooked = trainService.canBeBooked(trainPrn, source, destination, dateOfTravel);

        // Retrieve train details
        Train train = (Train) canBeBooked.getData();

        // Check seat availability
        ResponseDataDTO availableSeatsDTO = trainService.areSeatsAvailable(train, numberOfSeatsToBeBooked, dateOfTravel);

        // Retrieve seat availability data
        List<List<Integer>> allSeats = train.getSeats().get(dateOfTravel.toString());
        List<List<Integer>> availableSeatsList;
        Object data = availableSeatsDTO.getData();
        if (data instanceof List<?>) {
            availableSeatsList = (List<List<Integer>>) data;
        } else {
            log.warn("Unexpected data type received for available seats: {}", data.getClass());
            throw new CustomException("Invalid seat data received.", ResponseStatus.INVALID_DATA);
        }

        // Book seats by marking them as booked in the train's seat map
        trainService.bookSeats(availableSeatsList, allSeats);

        try {
            // Save the ticket in the ticket database
            log.info("Saving ticket in the DB");
            Ticket ticket = ticketService.createNewTicket(
                    loggedInUser.getUserId(),
                    train.getPrn(),
                    dateOfTravel,
                    source,
                    destination,
                    availableSeatsList,
                    trainService.getArrivalAtSourceTime(train, source, dateOfTravel),
                    trainService.getArrivalAtSourceTime(train, destination, dateOfTravel)
            );
            if (ticket == null) {
                throw new CustomException("Error while booking ticket: not saved in the DB",
                        ResponseStatus.TICKET_NOT_BOOKED);
            }

            // Update the user's booked ticket list
            loggedInUser.getTicketsBooked().add(ticket);
            log.info("Updating logged in user ticket list");

            // Save the updated user in the user database
            userRepositoryV2.save(loggedInUser);
            log.info("Saving user in the DB");

            // Update the train in the train database
            trainService.updateTrain(train);
            log.info("Updating train in the DB");

            return new ResponseDataDTO(true, "Ticket Booked with ID: " + ticket.getTicketId(), ticket);
        } catch (Exception e) {
            log.error("Error while booking ticket: {}", e.getMessage(), e);
            throw new CustomException("Error while booking ticket: " + e.getMessage(), ResponseStatus.TICKET_NOT_BOOKED);
        }
    }

    /**
     * Fetches all tickets booked by the logged-in user.
     *
     * @return ResponseDataDTO containing the list of tickets.
     */
    @Override
    public ResponseDataDTO fetchAllTickets() {
        log.info("Fetching all tickets for logged in user");

        // Ensure the user is logged in
        if (loggedInUser == null) {
            log.warn("Unauthorized ticket fetch attempt - no logged in user");
            throw new CustomException("Please log in to book the ticket", ResponseStatus.USER_NOT_FOUND);
        }

        return new ResponseDataDTO(true, "Tickets fetched", loggedInUser.getTicketsBooked());
    }

    /**
     * Cancels a ticket for the logged-in user.
     *
     * @param idOfTicketToCancel The ID of the ticket to cancel.
     * @return ResponseDataDTO containing cancellation result.
     */
    @Override
    public ResponseDataDTO cancelTicket(String idOfTicketToCancel) {
        // Ensure the user is logged in
        if (loggedInUser == null) {
            log.warn("Unauthorized ticket cancellation attempt - no logged in user");
            throw new CustomException("Please log in to book the ticket", ResponseStatus.USER_NOT_FOUND);
        }

        try {
            Iterator<Ticket> iterator = loggedInUser.getTicketsBooked().iterator();

            // Iterate through the user's booked tickets
            while (iterator.hasNext()) {
                Ticket ticket = iterator.next();

                // Check if the ticket ID matches
                if (ticket.getTicketId().equals(idOfTicketToCancel)) {
                    log.info("Found ticket: {}", ticket);

                    // Free up the booked seats on the train
                    String bookedTrainPrn = ticket.getTrainId();
                    Train train = trainService.findTrainByPrn(bookedTrainPrn).get();
                    log.info("Associated train: {}", train);

                    List<List<Integer>> bookedSeats = ticket.getBookedSeatsIndex();
                    log.info("Booked seats before freeing: {}", bookedSeats.toString());
                    trainService.freeTheBookedSeats(bookedSeats, train, ticket.getDateOfTravel());
                    log.info("Seats successfully freed, saving data...");

                    // Update the train in the database
                    trainService.updateTrain(train);
                    log.info("Updating train in the DB");

                    // Delete the ticket from the ticket database
                    ticketService.deleteTicketById(idOfTicketToCancel);
                    log.info("Deleting ticket in the DB");

                    // Remove the ticket from the user's booked list
                    iterator.remove();
                    log.info("Updating logged in user ticket list");

                    // Save the updated user in the database
                    userRepositoryV2.save(loggedInUser);
                    log.info("Updating user in the DB");

                    return new ResponseDataDTO(true, String.format("Ticket ID: %s has been deleted.", idOfTicketToCancel));
                }
            }

            log.warn("Ticket not found: {}", idOfTicketToCancel);
            throw new CustomException(String.format("Ticket ID: %s not found", idOfTicketToCancel),
                    ResponseStatus.TICKET_NOT_FOUND);

        } catch (Exception e) {
            log.error("Error while canceling ticket: {}", e.getMessage(), e);
            throw new CustomException("Error while canceling ticket: " + e.getMessage(), ResponseStatus.TICKET_NOT_CANCELLED);
        }
    }

    /**
     * Fetches a ticket by its ID for the logged-in user.
     *
     * @param idOfTicketToFind The ID of the ticket to find.
     * @return ResponseDataDTO containing the ticket details.
     */
    @Override
    public ResponseDataDTO fetchTicketById(String idOfTicketToFind) {
        log.info("Fetching ticket by ID: {}", idOfTicketToFind);

        // Ensure the user is logged in
        if (loggedInUser == null) {
            log.warn("Unauthorized ticket fetch attempt - no logged in user");
            throw new CustomException("Please log in to book the ticket", ResponseStatus.USER_NOT_FOUND);
        }

        // Find the ticket by its ID
        Ticket ticketFound = ticketService.findTicketById(idOfTicketToFind).orElse(null);
        if (ticketFound == null) {
            log.warn("Ticket not found: {}", idOfTicketToFind);
            throw new CustomException(String.format("Ticket ID: %s not found", idOfTicketToFind),
                    ResponseStatus.TICKET_NOT_FOUND);
        }

        return new ResponseDataDTO(true, "Ticket found", ticketFound);
    }

    /**
     * Reschedules a ticket to a new travel date.
     *
     * @param ticketId          The ID of the ticket to reschedule.
     * @param updatedTravelDate The new travel date.
     * @return ResponseDataDTO containing rescheduling result.
     */
    @Override
    public ResponseDataDTO rescheduleTicket(String ticketId, LocalDate updatedTravelDate) {
        // Ensure the user is logged in
        if (loggedInUser == null) {
            log.warn("Unauthorized ticket rescheduling attempt - no logged in user");
            throw new CustomException("Please log in to book the ticket", ResponseStatus.USER_NOT_FOUND);
        }

        // Ensure the new travel date is not in the past
        if (updatedTravelDate.isBefore(LocalDate.now())) {
            throw new CustomException("Date of travel cannot be in the past", ResponseStatus.INVALID_DATA);
        }

        // Find the ticket by its ID
        Ticket ticketFound = ticketService.findTicketById(ticketId).orElse(null);
        if (ticketFound == null) {
            throw new CustomException(String.format("Ticket ID: %s not found", ticketId),
                    ResponseStatus.TICKET_NOT_FOUND);
        }

        // Check if the train can be booked for the new date
        trainService.canBeBooked(ticketFound.getTrainId(), ticketFound.getSource(),
                ticketFound.getDestination(), updatedTravelDate);

        // Update the ticket's travel date
        log.info("Updating the travel date in the ticket: {}", updatedTravelDate);
        ticketFound.setDateOfTravel(updatedTravelDate);

        // Save the updated ticket in the database
        log.info("Saving the ticket in the database");
        ticketService.saveTicket(ticketFound);

        return new ResponseDataDTO(true, "Travel date updated successfully");
    }
}