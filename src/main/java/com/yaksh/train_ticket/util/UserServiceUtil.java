package com.yaksh.train_ticket.util;

import com.yaksh.train_ticket.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


public interface UserServiceUtil {

    String hashPassword(String password);
    boolean checkPassword(String password, String hashedPassword);

    Optional<User> findUserByName(String userName);
}
