package com.yaksh.train_ticket.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yaksh.train_ticket.DTO.ResponseDataDTO;
import com.yaksh.train_ticket.model.Ticket;
import com.yaksh.train_ticket.model.Train;
import com.yaksh.train_ticket.model.User;
import com.yaksh.train_ticket.util.UserServiceUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserBookingServiceImpl implements UserBookingService {
    private User user;
    private List<User> userList;
    private static final String USERS_PATH = "C:\\Users\\91635\\Desktop\\Projects\\train-ticket\\src\\main\\java\\com\\yaksh\\train_ticket\\repository\\users.json";

    @Autowired
    private UserServiceUtil userServiceUtil;

    // to serialize/deserialize the data (userName -> user_name)
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    // Setter method to assign logged-in user
    public void setLoggedInUser(User user) {
        this.user = user;
        System.out.println("User logged in: " + user.getUserName());
    }

    @Override
    public User getLoggedInUser() {
        return this.user;
    }

    @Override
    public List<User> getUserList() {
        return userList;
    }


    @PostConstruct  // Runs after the bean is initialized
    public void init() {
        try {
            loadUsers();
        } catch (IOException e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
    }

    public void loadUsers() throws IOException{
        File usersData = new File(USERS_PATH);
        userList = objectMapper.readValue(usersData, new TypeReference<List<User>>() {});
        System.out.println(userList);
    }

    @Override
    public ResponseDataDTO loginUser(String userName,String password) {
        Optional<User> userFound = userList.stream()
                .filter(user1 ->
                        user1.getUserName().equals(userName)
                        && userServiceUtil.checkPassword(password,user1.getHashedPassword()))
                .findFirst();
        return userFound.isPresent() ?
                new ResponseDataDTO(true,"User Found",userFound.get()) :
                new ResponseDataDTO(false,"User Not Found",null) ;
    }

    @Override
    public ResponseDataDTO signupUSer(User user) {
        Optional<User> userFound = userServiceUtil.findUserByName(user.getUserName());
        if(userFound.isPresent()){
            return new ResponseDataDTO(false,"User Already Exists",userFound.get());
        }
        try{
            userList.add(user);
            saveUserToFile();
            return new ResponseDataDTO(true,"User Saved in the file",user);
        }catch (Exception e){
            return new ResponseDataDTO(false,"User Could not be Saved",e.getMessage());
        }
    }

    private void saveUserToFile() throws IOException{
        File usersFile = new File(USERS_PATH);
        objectMapper.writeValue(usersFile,userList);
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
