package com.yaksh.train_ticket.service;
import com.yaksh.train_ticket.DTO.CommonResponsesDTOs;
import com.yaksh.train_ticket.DTO.ResponseDataDTO;
import com.yaksh.train_ticket.enums.ResponseStatus;
import com.yaksh.train_ticket.model.Ticket;
import com.yaksh.train_ticket.model.Train;
import com.yaksh.train_ticket.model.User;
import com.yaksh.train_ticket.repository.TicketRepositoryV2;
import com.yaksh.train_ticket.repository.UserRepository;
import com.yaksh.train_ticket.repository.UserRepositoryV2;
import com.yaksh.train_ticket.util.UserServiceUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserBookingServiceImpl implements UserBookingService {
    private User loggedInUser;
    private final UserRepository userRepository;
    private final UserServiceUtil userServiceUtil;
    private final TrainService trainService;

    private final UserRepositoryV2 userRepositoryV2;
    private final TicketRepositoryV2 ticketRepositoryV2;

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
//        return userRepository.getUserList();
    }

    @PostConstruct
    // Runs after the bean is initialized
    public void init() {
        try {
            log.info("Initializing user repository data");
            userRepository.loadUsers();
        } catch (IOException e) {
            log.error("Error loading users: {}", e.getMessage(), e);
        }
    }

    @Override
    public ResponseDataDTO loginUser(String userName, String password) {
        log.info("Login attempt for user: {}", userName);
        userRepositoryV2.findByUserName(userName)
                .map(user -> userServiceUtil.checkPassword(password, user.getHashedPassword())
                        ? new ResponseDataDTO(true, "User Found", user)
                        // user is found but password not correct
                        : new ResponseDataDTO(false, ResponseStatus.PASSWORD_INCORRECT, "Password Incorrect"))
                .orElse(new ResponseDataDTO(false, ResponseStatus.USER_NOT_FOUND, "User Not Found"));
//TO DELETE
                return userRepository.findUserByName(userName)
                // checks if user entered correct password
                .map(user -> userServiceUtil.checkPassword(password, user.getHashedPassword())
                        ? new ResponseDataDTO(true, "User Found", user)
                        // user is found but password not correct
                        : new ResponseDataDTO(false, ResponseStatus.PASSWORD_INCORRECT, "Password Incorrect"))
                .orElse(new ResponseDataDTO(false, ResponseStatus.USER_NOT_FOUND, "User Not Found"));
    }

    @Override
    public ResponseDataDTO signupUser(String userName, String password) {
        log.info("Signup attempt for user: {}", userName);
//        Optional<User> userFound = userRepository.findUserByName(userName);
        Optional<User> userFound = userRepositoryV2.findByUserName(userName);

        if (userFound.isPresent()) {
            log.warn("Signup failed - user already exists: {}", userName);
            return new ResponseDataDTO(false, ResponseStatus.USER_ALREADY_EXISTS, "User Already Exists", userFound.get());
        }
        try {
            User user = new User(UUID.randomUUID().toString(), userName,
                    userServiceUtil.hashPassword(password), new ArrayList<>());
            boolean userAdded = userRepository.addUser(user);
            User savedUser = userRepositoryV2.save(user);

            return new ResponseDataDTO(true, "User Saved in the collection", savedUser);
        } catch (Exception e) {
            log.error("Error while saving user in the file: {}", e.getMessage(), e);
            return new ResponseDataDTO(false, ResponseStatus.USER_NOT_SAVED_IN_FILE,
                    "Error while saving user in the file: " + e.getMessage());
        }
    }

    // OLD LOGIC: user entered list of seat indexes where he wanted to book the seats,
    // and in was checking if that indexes are available in the train seats
    // but in real world we just enter the number of seats and seats get assigned to us based
    // on availability

    // Thus NEW LOGIC: user enters number of seats he wants to book
    // , and we book those seats even if they are in different rows
    @Override
    public ResponseDataDTO bookTicket(String trainPrn, String source, String destination,
                                      String dateOfTravel, int numberOfSeatsToBeBooked) {
        log.info("Booking attempt - Train: {}, Seats: {}", trainPrn, numberOfSeatsToBeBooked);

        // if user is not logged in then false
        if (loggedInUser == null) {
            log.warn("Unauthorized booking attempt - no logged in user");
            return CommonResponsesDTOs.userNotLoggedInDTO();
        }

        ResponseDataDTO canBeBooked = trainService.canBeBooked(trainPrn);
        if (!canBeBooked.isStatus()) {
            log.warn("Booking failed for train {}: {}", trainPrn, canBeBooked.getMessage());
            return canBeBooked;
        }

        Train train = (Train) canBeBooked.getData();

        ResponseDataDTO availableSeatsDTO = trainService.areSeatsAvailable(train, numberOfSeatsToBeBooked);
        // Are seats available
        if (!availableSeatsDTO.isStatus()) {
            log.warn("Not enough seats available in train {}: {}", trainPrn, availableSeatsDTO.getMessage());
            return availableSeatsDTO;
        }

        List<List<Integer>> allSeats = train.getSeats();

        List<List<Integer>> availableSeatsList;
        Object data = availableSeatsDTO.getData();
        if (data instanceof List<?>) {
            availableSeatsList = (List<List<Integer>>) data;
        } else {
            log.warn("Unexpected data type received for available seats: {}", data.getClass());
            return new ResponseDataDTO(false, ResponseStatus.INVALID_DATA, "Invalid seat data received.");
        }


        // book seats (get row and col from availableSeatsLists and make it 1)
        availableSeatsList.forEach(seat -> allSeats.get(seat.get(0)).set(seat.get(1), 1));

        try {
            trainService.saveTrainToFile();
            Ticket ticket = new Ticket(
                    UUID.randomUUID().toString(),
                    loggedInUser.getUserId(),
                    train,
                    dateOfTravel,
                    source,
                    destination,
                    availableSeatsList
            );

            loggedInUser.getTicketsBooked().add(ticket);

            userRepository.saveUserToFile();
            ticketRepositoryV2.save(ticket);
            userRepositoryV2.save(loggedInUser);
            log.info("Ticket booked successfully - Ticket ID: {}", ticket.getTicketId());
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
    public ResponseDataDTO cancelTicket(String IdOfTicketToCancel) {

        // If user is not logged in, return an error
        if (loggedInUser == null) {
            log.warn("Unauthorized ticket cancellation attempt - no logged in user");
            return CommonResponsesDTOs.userNotLoggedInDTO();
        }

        try {
            Iterator<Ticket> iterator = loggedInUser.getTicketsBooked().iterator();

            while (iterator.hasNext()) {
                Ticket ticket = iterator.next(); // Store the ticket once

                if (ticket.getTicketId().equals(IdOfTicketToCancel)) {
                    log.info("Found ticket: {}", ticket);

                    // Get the train from the ticket and free up the seats
                    Train train = ticket.getTrain();
                    log.info("Associated train: {}", train);

                    // Iterate over the booked seats and mark them as available (0)
                    List<List<Integer>> bookedSeats = ticket.getBookedSeatsIndex();
                    log.info("Booked seats before freeing: {}", bookedSeats.toString());
                    bookedSeats.forEach(seat -> train.getSeats().get(seat.get(0)).set(seat.get(1), 0));

                    log.info("Seats successfully freed, saving data...");

                    trainService.updateTrain(train);  // Ensure the train state is updated
                    trainService.saveTrainToFile();   // Persist train data
                    iterator.remove();                // Remove the ticket from the user's booked list
                    userRepository.saveUserToFile();  // Persist user data


                    return new ResponseDataDTO(true, String.format("Ticket ID: %s has been deleted.", IdOfTicketToCancel));
                }
            }

            log.warn("Ticket not found: {}", IdOfTicketToCancel);
            return CommonResponsesDTOs.ticketNotFoundDTO(IdOfTicketToCancel);
        } catch (Exception e) {
            log.error("Error while canceling ticket: {}", e.getMessage(), e);
            return new ResponseDataDTO(false, ResponseStatus.TICKET_NOT_CANCELLED, "Error while canceling ticket: " + e.getMessage());
        }
    }

    @Override
    public ResponseDataDTO fetchTicketById(String IdOfTicketToFind) {
        log.info("Fetching ticket by ID: {}", IdOfTicketToFind);
        // if user is not logged in then false
        if (loggedInUser == null) {
            log.warn("Unauthorized ticket fetch attempt - no logged in user");
            return CommonResponsesDTOs.userNotLoggedInDTO();
        }
        Ticket ticketFound = loggedInUser.getTicketsBooked()
                .stream()
                .filter(ticket -> ticket.getTicketId().equalsIgnoreCase(IdOfTicketToFind))
                .findFirst()
                .orElse(null);
        if (ticketFound == null) {
            log.warn("Ticket not found: {}", IdOfTicketToFind);
            return CommonResponsesDTOs.ticketNotFoundDTO(IdOfTicketToFind);
        }
        return new ResponseDataDTO(true, "Ticket found", ticketFound);
    }
}
