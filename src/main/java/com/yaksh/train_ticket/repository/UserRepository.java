package com.yaksh.train_ticket.repository;

import com.yaksh.train_ticket.model.Ticket;
import com.yaksh.train_ticket.model.User;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
    void loadUsers() throws IOException;
    List<User> getUserList();
    boolean saveUserToFile() throws IOException;
    boolean addUser(User user) throws IOException;
    Optional<User> findUserByName(String userName);
}
