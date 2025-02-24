package com.yaksh.train_ticket;

import com.yaksh.train_ticket.service.UserBookingService;
import com.yaksh.train_ticket.service.UserBookingServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class TrainTicketApplication {

	public static void main(String[] args) {

		SpringApplication.run(TrainTicketApplication.class, args);

	}

}
