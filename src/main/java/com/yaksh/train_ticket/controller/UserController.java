package com.yaksh.train_ticket.controller;

import com.yaksh.train_ticket.DTO.ResponseDataDTO;
import com.yaksh.train_ticket.model.User;
import com.yaksh.train_ticket.service.UserBookingService;
import com.yaksh.train_ticket.util.UserServiceUtil;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.UUID;

@RestController
@RequestMapping("/v1/user")
public class UserController {

    @Autowired
    private UserServiceUtil userServiceUtil;
    @Autowired
    private UserBookingService service;

    @PostMapping("/loginUser")
    public ResponseDataDTO loginUser(@RequestParam  String userName,@RequestParam String password){
        ResponseDataDTO responseDataDTO = service.loginUser(userName,password);
        if(responseDataDTO.isStatus()){
            service.setLoggedInUser((User) responseDataDTO.getData());
        }
        return responseDataDTO;
    }

    @PostMapping("/signupUser")
    public ResponseDataDTO signupUser(@RequestParam  String userName,@RequestParam String password){
        User user = new User(UUID.randomUUID().toString(),userName,password, userServiceUtil.hashPassword(password),new ArrayList<>());
        return service.signupUSer(user);
    }
}
