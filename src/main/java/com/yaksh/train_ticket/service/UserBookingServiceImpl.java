package com.yaksh.train_ticket.service;
import com.yaksh.train_ticket.DTO.CommonResponsesDTOs;
import com.yaksh.train_ticket.DTO.ResponseDataDTO;
import com.yaksh.train_ticket.enums.ResponseStatus;
import com.yaksh.train_ticket.model.Ticket;
import com.yaksh.train_ticket.model.Train;
import com.yaksh.train_ticket.model.User;
import com.yaksh.train_ticket.repository.UserRepositoryV2;
import com.yaksh.train_ticket.util.HelperFunctions;
import com.yaksh.train_ticket.util.UserServiceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserBookingServiceImpl implements UserBookingService {
    private User loggedInUser;
    private final UserServiceUtil userServiceUtil;

    private final UserRepositoryV2 userRepositoryV2;

    // services
    private final TicketService ticketService;
    private final TrainService trainService;


    @Override
    // Setter method to assign logged-in user
    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
        log.info("User logged in as: {}", user.getUserName());
    }

    @Override
    // getter method to get logged in user
    public User getLoggedInUser() {
        log.debug("Retrieving logged in user");
        return this.loggedInUser;
    }

    @Override
    public List<User> getUserList() {
        log.info("Retrieving all users from repository");
        return userRepositoryV2.findAll();
    }

    @Override
    public ResponseDataDTO loginUser(String userName, String password) {
        log.info("Login attempt for user: {}", userName);
        return userRepositoryV2.findByUserName(userName.toLowerCase())
                .map(user -> userServiceUtil.checkPassword(password, user.getHashedPassword())
                        ? new ResponseDataDTO(true, "User Found", user)
                        // user is found but password not correct
                        : new ResponseDataDTO(false, ResponseStatus.PASSWORD_INCORRECT, "Password Incorrect"))
                .orElse(new ResponseDataDTO(false, ResponseStatus.USER_NOT_FOUND, "User Not Found"));
    }

    @Override
    public ResponseDataDTO signupUser(String userName, String password) {
        log.info("Signup attempt for user: {}", userName);
        // checking if user already exists with the username provided
        Optional<User> userFound = userRepositoryV2.findByUserName(userName.toLowerCase());
        // if user already exists return false
        if (userFound.isPresent()) {
            log.warn("Signup failed - user already exists: {}", userName);
            return new ResponseDataDTO(false, ResponseStatus.USER_ALREADY_EXISTS, "User Already Exists");
        }
        try {
            User user = new User(UUID.randomUUID().toString(), userName,
                    userServiceUtil.hashPassword(password), new ArrayList<>());
            // save user
            User savedUser = userRepositoryV2.save(user);

            return new ResponseDataDTO(true, "User Saved in the collection", savedUser);
        } catch (Exception e) {
            log.error("Error while saving user in the collection: {}", e.getMessage(), e);
            return new ResponseDataDTO(false, ResponseStatus.USER_NOT_SAVED_IN_COLLECTION,
                    "Error while saving user in the collection: " + e.getMessage());
        }
    }

    // OLD LOGIC: user entered list of seat indexes where he wanted to book the seats,
    // and in was checking if that indexes are available in the train seats
    // but in real world we just enter the number of seats and seats get assigned to us based
    // on availability

    // Thus NEW LOGIC(V1): user enters number of seats he wants to book
    // , and we book those seats even if they are in different rows

    // NEW LOGIC (V2): user enters number of seats to book, first we check if seats
    // are available in contiguous manner (eg. 10 seats together), if not then
    // only we book seats even if they are not together

    @Override
    public ResponseDataDTO bookTicket(String trainPrn, String source, String destination,
                                      LocalDate dateOfTravel, int numberOfSeatsToBeBooked) {
        log.info("Booking attempt - Train: {}, Seats: {}", trainPrn, numberOfSeatsToBeBooked);

        // if user is not logged in then false
        if (loggedInUser == null) {
            log.warn("Unauthorized booking attempt - no logged in user");
            return CommonResponsesDTOs.userNotLoggedInDTO();
        }

        // if date selected is in the past
        if (HelperFunctions.isDateInThePast(dateOfTravel)) {
            return CommonResponsesDTOs.dateIsInThePastDTO();
        }


        ResponseDataDTO canBeBooked = trainService.canBeBooked(trainPrn,source,destination,dateOfTravel);
        // if train cannot be booked then return false
        if (!canBeBooked.isStatus()) {
            log.warn("Booking failed for train {}: {}", trainPrn, canBeBooked.getMessage());
            return canBeBooked;
        }

        //get train data
        Train train = (Train) canBeBooked.getData();

        // Are seats available
        ResponseDataDTO availableSeatsDTO = trainService.areSeatsAvailable(train, numberOfSeatsToBeBooked,dateOfTravel);

        // if seats are not available then return
        if (!availableSeatsDTO.isStatus()) {
            log.warn("Not enough seats available in train {}: {}", trainPrn, availableSeatsDTO.getMessage());
            return availableSeatsDTO;
        }

        List<List<Integer>> allSeats = train.getSeats().get(HelperFunctions.localDateToString(dateOfTravel));

        List<List<Integer>> availableSeatsList;
        Object data = availableSeatsDTO.getData();
        if (data instanceof List<?>) {
            availableSeatsList = (List<List<Integer>>) data;
        } else {
            log.warn("Unexpected data type received for available seats: {}", data.getClass());
            return new ResponseDataDTO(false, ResponseStatus.INVALID_DATA, "Invalid seat data received.");
        }


        // book seats (get row and col from availableSeatsLists and make it 1)
        trainService.bookSeats(availableSeatsList,allSeats);

        try {
            // save ticket in the ticket database
            log.info("Saving ticket in the DB");
            Ticket ticket = ticketService.createNewTicket(
                    loggedInUser.getUserId(),
                    train.getPrn(),
                    dateOfTravel,
                    source,
                    destination,
                    availableSeatsList,
                    trainService.getArrivalAtSourceTime(train,source,dateOfTravel),
                    trainService.getArrivalAtSourceTime(train,destination,dateOfTravel)
                    );
            if(ticket==null){
                return new ResponseDataDTO(false, ResponseStatus.TICKET_NOT_BOOKED, "Error while booking ticket: not saved in the DB");
            }
            // update the user ticket list
            loggedInUser.getTicketsBooked().add(ticket);
            log.info("Updating logged in user ticket list");
            // save user in the user database
            userRepositoryV2.save(loggedInUser);
            log.info("Saving user in the DB");
            // update train in the train database
            trainService.updateTrain(train);
            log.info("updating train in the DB");
            return new ResponseDataDTO(true, "Ticket Booked with ID: " + ticket.getTicketId(), ticket);
        } catch (Exception e) {
            log.error("Error while booking ticket: {}", e.getMessage(), e);
            return new ResponseDataDTO(false, ResponseStatus.TICKET_NOT_BOOKED, "Error while booking ticket: " + e.getMessage());
        }
    }

    @Override
    public ResponseDataDTO fetchAllTickets() {
        log.info("Fetching all tickets for logged in user");
        // if user is not logged in then false
        if (loggedInUser == null) {
            log.warn("Unauthorized ticket fetch attempt - no logged in user");
            return CommonResponsesDTOs.userNotLoggedInDTO();
        }
        return new ResponseDataDTO(true, "Tickets fetched", loggedInUser.getTicketsBooked());
    }


    @Override
    public ResponseDataDTO cancelTicket(String idOfTicketToCancel) {

        // If user is not logged in, return an error
        if (loggedInUser == null) {
            log.warn("Unauthorized ticket cancellation attempt - no logged in user");
            return CommonResponsesDTOs.userNotLoggedInDTO();
        }

        try {
            Iterator<Ticket> iterator = loggedInUser.getTicketsBooked().iterator();

            while (iterator.hasNext()) {
                Ticket ticket = iterator.next(); // Store the ticket once

                if (ticket.getTicketId().equals(idOfTicketToCancel)) {
                    log.info("Found ticket: {}", ticket);

                    // Get the train from the ticket and free up the seats
                    String bookedTrainPrn = ticket.getTrainId();
                    Train train = trainService.findTrainByPrn(bookedTrainPrn).get();
                    log.info("Associated train: {}", train);
                    // Iterate over the booked seats and mark them as available (0)
                    List<List<Integer>> bookedSeats = ticket.getBookedSeatsIndex();
                    log.info("Booked seats before freeing: {}", bookedSeats.toString());
                    trainService.freeTheBookedSeats(bookedSeats,train,ticket.getDateOfTravel());
                    log.info("Seats successfully freed, saving data...");
                    // updating train in the DB
                    trainService.updateTrain(train);
                    log.info("Updating train in the DB");
                    // deleting ticket from the DB
                    ticketService.deleteTicketById(idOfTicketToCancel);
                    log.info("Deleting ticket in the DB");
                    // Removing the ticket from the user's booked list
                    iterator.remove();
                    log.info("Updating logged in user ticket list");
                    // updating user in the DB
                    userRepositoryV2.save(loggedInUser);
                    log.info("Updating user in the DB");
                    return new ResponseDataDTO(true, String.format("Ticket ID: %s has been deleted.", idOfTicketToCancel));
                }
            }

            log.warn("Ticket not found: {}", idOfTicketToCancel);
            return CommonResponsesDTOs.ticketNotFoundDTO(idOfTicketToCancel);
        } catch (Exception e) {
            log.error("Error while canceling ticket: {}", e.getMessage(), e);
            return new ResponseDataDTO(false, ResponseStatus.TICKET_NOT_CANCELLED, "Error while canceling ticket: " + e.getMessage());
        }
    }

    @Override
    public ResponseDataDTO fetchTicketById(String idOfTicketToFind) {
        log.info("Fetching ticket by ID: {}", idOfTicketToFind);
        // if user is not logged in then false
        if (loggedInUser == null) {
            log.warn("Unauthorized ticket fetch attempt - no logged in user");
            return CommonResponsesDTOs.userNotLoggedInDTO();
        }

        Ticket ticketFound = ticketService.findTicketById(idOfTicketToFind).orElse(null);
        if (ticketFound == null) {
            log.warn("Ticket not found: {}", idOfTicketToFind);
            return CommonResponsesDTOs.ticketNotFoundDTO(idOfTicketToFind);
        }
        return new ResponseDataDTO(true, "Ticket found", ticketFound);
    }

    @Override
    public ResponseDataDTO rescheduleTicket(String ticketId, LocalDate updatedTravelDate) {
        // If user is not logged in, return an error
        if (loggedInUser == null) {
            log.warn("Unauthorized ticket rescheduling attempt - no logged in user");
            return CommonResponsesDTOs.userNotLoggedInDTO();
        }
        if (HelperFunctions.isDateInThePast(updatedTravelDate)) {
            return CommonResponsesDTOs.dateIsInThePastDTO();
        }
        Ticket ticketFound = ticketService.findTicketById(ticketId).orElse(null);
        if (ticketFound == null) {
            return CommonResponsesDTOs.ticketNotFoundDTO(ticketId);
        }
        // update the date in the ticket
        ResponseDataDTO canBeBooked = trainService.canBeBooked(ticketFound.getTrainId(), ticketFound.getSource(),
                ticketFound.getDestination(), updatedTravelDate);
        if (!canBeBooked.isStatus()) {
            log.warn("travel date update failed for train {}: {}", ticketFound.getTrainId(), canBeBooked.getMessage());
            return canBeBooked;
        }
        log.info("Updating the travel date in the ticket: {}", updatedTravelDate);
        ticketFound.setDateOfTravel(updatedTravelDate);
        // save the ticket in the DB
        log.info("Saving the ticket in the database");
        ticketService.saveTicket(ticketFound);
        return new ResponseDataDTO(true, "Travel date updated successfully");
    }
}
