package com.yaksh.train_ticket.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yaksh.train_ticket.DTO.ResponseDataDTO;
import com.yaksh.train_ticket.model.Ticket;
import com.yaksh.train_ticket.model.Train;
import com.yaksh.train_ticket.model.User;
import com.yaksh.train_ticket.repository.UserRepository;
import com.yaksh.train_ticket.util.HelperFunctions;
import com.yaksh.train_ticket.util.UserServiceUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
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

    @Override
    public ResponseDataDTO bookTicket(String trainPrn, String source, String destination,String dateOfTravel,int numberOfSeats){
        Ticket ticket = new Ticket(
                UUID.randomUUID().toString(),
                loggedInUser.getUserId(),
                new Train(),
                dateOfTravel,
                source,
                destination
        );
        loggedInUser.getTicketsBooked().add(ticket);
        try{
            userRepository.saveUserToFile();
        }catch(Exception e){
            return new ResponseDataDTO(false,"Ticket Not Booked",null);
        }
        return new ResponseDataDTO(true,"Ticket Booked with ID: "+ ticket.getTicketId(),ticket);
    }

    @Override
    public ResponseDataDTO fetchAllTickets() {
        return new ResponseDataDTO(true,"Tickets fetched",loggedInUser.getTicketsBooked());
    }


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
