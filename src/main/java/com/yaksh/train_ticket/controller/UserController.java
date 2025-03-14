package com.yaksh.train_ticket.controller;

import com.yaksh.train_ticket.DTO.ResponseDataDTO;
import com.yaksh.train_ticket.model.User;
import com.yaksh.train_ticket.service.UserBookingService;
import com.yaksh.train_ticket.util.UserServiceUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/user")
@Slf4j
public class UserController {

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
        return service.signupUser(userName,password);
    }
    @PostMapping("/bookTicket")
    public ResponseDataDTO bookTicket(
            @RequestParam String trainPrn,
            @RequestParam String source,
            @RequestParam String destination,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateOfTravel,
            @RequestParam int numberOfSeatsToBeBooked){
        return service.bookTicket(trainPrn,source,destination,dateOfTravel,numberOfSeatsToBeBooked);
    }
    @GetMapping("/fetchTickets")
    public ResponseDataDTO fetchAllTickets(){
        return service.fetchAllTickets();
    }

    @PostMapping("/cancelTicket")
    public ResponseDataDTO cancelTicket(@RequestParam String ticketId){
        return service.cancelTicket(ticketId);
    }
    @GetMapping("/fetchTicketById")
    public ResponseDataDTO fetchTicketById(@RequestParam String ticketId){
        return service.fetchTicketById(ticketId);
    }

    @PostMapping("/rescheduleTicket")
    public ResponseDataDTO rescheduleTicket(
            @RequestParam String ticketId,
            @RequestParam  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate updatedDateOfTravel){
        return service.rescheduleTicket(ticketId,updatedDateOfTravel);
    }
}
