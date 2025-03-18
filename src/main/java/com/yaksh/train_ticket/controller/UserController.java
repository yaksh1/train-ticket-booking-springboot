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
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ResponseDataDTO> loginUser(@RequestParam  String userName,@RequestParam String password){
        ResponseDataDTO responseDataDTO = service.loginUser(userName,password);

        if(responseDataDTO.isStatus()){
            service.setLoggedInUser((User) responseDataDTO.getData());
        }
        return ResponseEntity.ok(responseDataDTO);
    }

    @PostMapping("/signupUser")
    public ResponseEntity<ResponseDataDTO> signupUser(@RequestParam  String userName,@RequestParam String password){
        return ResponseEntity.ok(service.signupUser(userName, password));
    }
    @PostMapping("/bookTicket")
    public ResponseEntity<ResponseDataDTO> bookTicket(
            @RequestParam String trainPrn,
            @RequestParam String source,
            @RequestParam String destination,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateOfTravel,
            @RequestParam int numberOfSeatsToBeBooked){
        return ResponseEntity.ok(
                service.bookTicket(trainPrn, source, destination, dateOfTravel, numberOfSeatsToBeBooked));
    }
    @GetMapping("/fetchTickets")
    public ResponseEntity<ResponseDataDTO> fetchAllTickets(){
        return ResponseEntity.ok(service.fetchAllTickets());
    }

    @PostMapping("/cancelTicket")
    public ResponseEntity<ResponseDataDTO> cancelTicket(@RequestParam String ticketId){
        return ResponseEntity.ok(service.cancelTicket(ticketId));
    }
    @GetMapping("/fetchTicketById")
    public ResponseEntity<ResponseDataDTO> fetchTicketById(@RequestParam String ticketId){
        return ResponseEntity.ok(service.fetchTicketById(ticketId));
    }

    @PostMapping("/rescheduleTicket")
    public ResponseEntity<ResponseDataDTO> rescheduleTicket(
            @RequestParam String ticketId,
            @RequestParam  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate updatedDateOfTravel){
        return ResponseEntity.ok(service.rescheduleTicket(ticketId, updatedDateOfTravel));
    }
}
