package com.yaksh.train_ticket.util;

import com.yaksh.train_ticket.model.User;
import com.yaksh.train_ticket.service.UserBookingService;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceUtilImpl implements UserServiceUtil{




    @Override
    public String hashPassword(String password) {
         return BCrypt.hashpw(password,BCrypt.gensalt());
    }

    @Override
    public boolean checkPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password,hashedPassword);
    }


}
