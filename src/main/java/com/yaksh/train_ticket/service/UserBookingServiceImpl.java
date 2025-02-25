package com.yaksh.train_ticket.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yaksh.train_ticket.DTO.ResponseDataDTO;
import com.yaksh.train_ticket.model.Ticket;
import com.yaksh.train_ticket.model.Train;
import com.yaksh.train_ticket.model.User;
import com.yaksh.train_ticket.repository.UserRepository;
import com.yaksh.train_ticket.util.UserServiceUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
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
        System.out.println("User logged in: " + user.getUserName());
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
        Optional<User> userFound = userServiceUtil.findUserByName(userName);
        // if user is present and password is also matching with hashed password of found user -> login successful
        return (userFound.isPresent() && userServiceUtil.checkPassword(password,userFound.get().getHashedPassword())) ?
                new ResponseDataDTO(true,"User Found",userFound.get()) :
                new ResponseDataDTO(false,"User Not Found",null);
    }

    @Override
    public ResponseDataDTO signupUser(User user) {
        Optional<User> userFound = userServiceUtil.findUserByName(user.getUserName());
        if(userFound.isPresent()){
            return new ResponseDataDTO(false,"User Already Exists",userFound.get());
        }
        try{
            boolean userAdded = userRepository.addUser(user);
            return userAdded ? new ResponseDataDTO(true,"User Saved in the file",user):
                    new ResponseDataDTO(false,"User could not be Saved in the file",user);

        }catch (Exception e){
            return new ResponseDataDTO(false,"User Could not be Saved",e.getMessage());
        }
    }

    @Override
    public ResponseDataDTO bookTicket(Train train, String source, String destination) {
        return null;
    }

    @Override
    public ResponseDataDTO FetchAllTickets() {
        return null;
    }

    @Override
    public ResponseDataDTO cancelTicket(Ticket ticket) {
        return null;
    }
}
