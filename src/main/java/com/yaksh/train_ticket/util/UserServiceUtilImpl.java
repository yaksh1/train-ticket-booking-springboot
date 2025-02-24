package com.yaksh.train_ticket.util;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

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
