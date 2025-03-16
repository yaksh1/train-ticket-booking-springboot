package com.yaksh.train_ticket.repository;

import com.yaksh.train_ticket.model.Ticket;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@DataMongoTest
public class TicketRepositoryTest {
    @Autowired
    private TicketRepositoryV2 ticketRepositoryV2;

    @BeforeEach
    void setup(){
        ticketRepositoryV2.deleteAll();
    }

    @Test
    public void ticketRepository_save_success(){
        //Arrange
        Ticket ticket = Ticket.builder()
                .ticketId(UUID.randomUUID().toString())
                .build();

        //Act
        Ticket savedTicket = ticketRepositoryV2.save(ticket);

        //Assert
        Assertions.assertThat(savedTicket).isNotNull();
        Assertions.assertThat(savedTicket.getTicketId()).isNotBlank();
    }

    @Test
    public void ticketRepository_findById_success(){
        //Arrange
        Ticket ticket = Ticket.builder()
                .ticketId(UUID.randomUUID().toString())
                .build();
        ticketRepositoryV2.save(ticket);

        //Act
        Optional<Ticket> savedTicket = ticketRepositoryV2.findById(ticket.getTicketId());

        //Assert
        Assertions.assertThat(savedTicket).isPresent();
    }
    @Test
    public void ticketRepository_deleteById_success(){
        //Arrange
        Ticket ticket = Ticket.builder()
                .ticketId(UUID.randomUUID().toString())
                .build();
        ticketRepositoryV2.save(ticket);

        //Act
        ticketRepositoryV2.deleteById(ticket.getTicketId());
        Optional<Ticket> savedTicket = ticketRepositoryV2.findById(ticket.getTicketId());


        //Assert
        Assertions.assertThat(savedTicket).isNotPresent();
    }

}
