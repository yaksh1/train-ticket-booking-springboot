package com.yaksh.train_ticket.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yaksh.train_ticket.model.Ticket;
import com.yaksh.train_ticket.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class UserRepositoryImpl implements UserRepository{
    private static final String USERS_PATH = "C:\\Users\\91635\\Desktop\\Projects\\train-ticket\\src\\main\\java\\com\\yaksh\\train_ticket\\repository\\users.json";
    // to serialize/deserialize the data (userName -> user_name)
    private ObjectMapper objectMapper = new ObjectMapper();
    private List<User> userList;

    @Override
    public void loadUsers() throws IOException {
        File usersData = new File(USERS_PATH);
        if(usersData.exists()){
            userList = objectMapper.readValue(usersData, new TypeReference<List<User>>() {});
            log.info("Users loaded successfully.");
        } else {
            log.warn("Users file not found, initializing empty user list.");
        }

    }

    @Override
    public List<User> getUserList() {
        return userList;
    }

    @Override
    public boolean saveUserToFile() {
        File usersFile = new File(USERS_PATH);
        long initialSize = usersFile.exists() ? usersFile.length() : 0;
        try {
            objectMapper.writeValue(usersFile, userList);
            long newSize = usersFile.length();
            return newSize > initialSize; // Returns true if file size increased
        } catch (IOException e) {
            log.error("Error writing to file: {}", e.getMessage());
            return false; // Return false if an exception occurs
        }
    }

    @Override
    public boolean addUser(User userToAdd) throws IOException{
        userList.add(userToAdd);
        return saveUserToFile();
    }

    @Override
    public Optional<User> findUserByName(String userName) {
        return userList.stream()
                .filter(user1 ->
                        user1.getUserName().equals(userName))
                .findFirst();
    }


}
