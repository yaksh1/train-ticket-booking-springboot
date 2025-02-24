package com.yaksh.train_ticket.util;

import org.springframework.stereotype.Service;


public interface UserServiceUtil {
    String hashPassword(String password);
    boolean checkPassword(String password, String hashedPassword);

}
