package com.yaksh.train_ticket.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yaksh.train_ticket.DTO.ResponseDataDTO;
import com.yaksh.train_ticket.model.Ticket;
import com.yaksh.train_ticket.model.Train;
import com.yaksh.train_ticket.model.User;
import com.yaksh.train_ticket.repository.TrainRepository;
import com.yaksh.train_ticket.repository.UserRepository;
import com.yaksh.train_ticket.util.HelperFunctions;
import com.yaksh.train_ticket.util.TrainServiceUtil;
import com.yaksh.train_ticket.util.UserServiceUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserBookingServiceImpl implements UserBookingService {
    private User loggedInUser;
    private final UserRepository userRepository;
    private final UserServiceUtil userServiceUtil;
    private final TrainService trainService;

    @Override
    // Setter method to assign logged-in user
    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
        log.info("User logged in as: " + user.getUserName());
    }

    @Override
    // getter method to get logged in user
    public User getLoggedInUser() {
        return this.loggedInUser;
    }

    @Override
    public List<User> getUserList() {
        return userRepository.getUserList();
    }

    @PostConstruct
    // Runs after the bean is initialized
    public void init() {
        try {
            userRepository.loadUsers();
        } catch (IOException e) {
            log.error("Error loading users: {}", e.getMessage());
        }
    }

    @Override
    public ResponseDataDTO loginUser(String userName,String password) {
        return userRepository.findUserByName(userName)
                // checks if user entered correct password
                .map(user -> userServiceUtil.checkPassword(password, user.getHashedPassword())
                        ? new ResponseDataDTO(true, "User Found", user)
                        // user is found but password not correct
                        : new ResponseDataDTO(false, "Password Incorrect", null))
                .orElse(new ResponseDataDTO(false, "User Not Found", null));
    }

    @Override
    public ResponseDataDTO signupUser(String userName,String password) {
        Optional<User> userFound = userRepository.findUserByName(userName);
        if(userFound.isPresent()){
            return new ResponseDataDTO(false,"User Already Exists",userFound.get());
        }
        try{
            User user = new User(UUID.randomUUID().toString(),userName,password, userServiceUtil.hashPassword(password),new ArrayList<>());
            boolean userAdded = userRepository.addUser(user);
            return userAdded ? new ResponseDataDTO(true,"User Saved in the file",user):
                    new ResponseDataDTO(false,"User could not be Saved in the file",user);

        }catch (Exception e){
            log.error("User could not be saved: {}", e.getMessage());
            return new ResponseDataDTO(false,"User Could not be Saved",e.getMessage());
        }
    }

    // OLD LOGIC: user entered list of seat indexes where he wanted to book the seats,
    // and in was checking if that indexes are available in the train seats
    // but in real world we just enter the number of seats and seats get assigned to us based
    // on availability

    // Thus NEW LOGIC: user enters number of seats he wants to book
    // , and we book those seats even if they are in different rows
    @Override
    public ResponseDataDTO bookTicket(String trainPrn, String source, String destination,String dateOfTravel,int numberOfSeatsToBeBooked) {
        // if user is not logged in then false
        if(loggedInUser==null){
            return new ResponseDataDTO(false,"Please log in to book the ticket ",null);
        }
        ResponseDataDTO canBeBooked = trainService.canBeBooked(trainPrn);
        if(!canBeBooked.isStatus()){
            return canBeBooked;
        }
        Train train =(Train) canBeBooked.getData();

        ResponseDataDTO availableSeatsDTO =trainService.areSeatsAvailable(train,numberOfSeatsToBeBooked);
        // Are seats available
        if(!availableSeatsDTO.isStatus()){
            return new ResponseDataDTO(false,availableSeatsDTO.getMessage(),null);
        }

        List<List<Integer>> allSeats = train.getSeats();
        List<int[]> availableSeatsList =(List<int[]>) availableSeatsDTO.getData();

        // book seats (get row and col from availableSeatsLists and make it 1)
        availableSeatsList.forEach(seat -> allSeats.get(seat[0]).set(seat[1],1));

        try{
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
            return new ResponseDataDTO(true,"Ticket Booked with ID: "+ ticket.getTicketId(),ticket);
        }catch (Exception e){
            return new ResponseDataDTO(false,"Ticket Not Booked",e.getMessage());
        }
    }


    @Override
    public ResponseDataDTO fetchAllTickets() {
        return new ResponseDataDTO(true,"Tickets fetched",loggedInUser.getTicketsBooked());
    }


    // TO DO: remove the booked seats from train
    @Override
    public ResponseDataDTO cancelTicket(String IdOfTicketToCancel) {
        try{

            Iterator<Ticket> iterator = loggedInUser.getTicketsBooked().iterator();
            while (iterator.hasNext()) {
                if (iterator.next().getTicketId().equals(IdOfTicketToCancel)) {
                    iterator.remove();
                    userRepository.saveUserToFile();
                    return new ResponseDataDTO(true,String.format("Ticket ID: %s has been deleted.",IdOfTicketToCancel));
                }
            }
            return new ResponseDataDTO(false,String.format("Ticket ID: %s not found",IdOfTicketToCancel),null);
        }catch (Exception e){
            return new ResponseDataDTO(false,e.getMessage(),null);
        }
    }


}
