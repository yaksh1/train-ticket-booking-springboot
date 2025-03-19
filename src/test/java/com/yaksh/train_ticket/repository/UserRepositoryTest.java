package com.yaksh.train_ticket.repository;

import com.yaksh.train_ticket.model.Ticket;
import com.yaksh.train_ticket.model.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.*;

@DataMongoTest
public class UserRepositoryTest {
    @Autowired
    private UserRepositoryV2 userRepositoryV2;

    @BeforeEach
    void setup(){
        userRepositoryV2.deleteAll();
    }
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // NOTE: naming convention of test methods: classIAmTesting_functionIamTesting_whatItReturns
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    @Test
    public void userRepository_save_success(){
        // Arrange
        User user = User.builder()
                .userId(UUID.randomUUID().toString())
                .userEmail("test username")
                .hashedPassword("hashedPassword")
                .ticketsBooked(new ArrayList<>())
                .build();

        // Act
        User savedUser = userRepositoryV2.save(user);

        //Assert
        Assertions.assertThat(savedUser).isNotNull();
        Assertions.assertThat(savedUser.getUserId()).isNotBlank();
        Assertions.assertThat(savedUser.getUserEmail()).isEqualTo("test username");
    }

    @Test
    public void userRepository_findAll_success(){
        // Arrange
        User user1 = User.builder()
                .userId(UUID.randomUUID().toString())
                .userEmail("user1")
                .hashedPassword("hashedPassword")
                .ticketsBooked(new ArrayList<>())
                .build();
        User user2 = User.builder()
                .userId(UUID.randomUUID().toString())
                .userEmail("user2")
                .hashedPassword("hashedPassword")
                .ticketsBooked(new ArrayList<>())
                .build();
        List<User> userList = Arrays.asList(user2,user1);

        // Act
        userRepositoryV2.saveAll(userList);
        List<User> savedUserList = userRepositoryV2.findAll();

        // Assert
        Assertions.assertThat(savedUserList).hasSize(userList.size());
        Assertions.assertThat(savedUserList).isNotNull();
    }

    @Test
    public void userRepository_findByUserEmail_success(){
        // Arrange
        User user1 = User.builder()
                .userId(UUID.randomUUID().toString())
                .userEmail("user1")
                .hashedPassword("hashedPassword")
                .ticketsBooked(new ArrayList<>())
                .build();
        userRepositoryV2.save(user1);

        // Act
        Optional<User> userFound = userRepositoryV2.findByUserEmail(user1.getUserEmail());

        // Assert
        Assertions.assertThat(userFound).isPresent();
        Assertions.assertThat(userFound.get().getUserEmail()).isEqualTo("user1");
    }

    @Test
    public void userRepository_updateUserAfterBookingTicket_success(){
        // Arrange
        User user1 = User.builder()
                .userId(UUID.randomUUID().toString())
                .userEmail("user1")
                .hashedPassword("hashedPassword")
                .ticketsBooked(new ArrayList<>())
                .build();
        userRepositoryV2.save(user1);


        // create ticket
        Ticket ticket = Ticket.builder()
                .ticketId(UUID.randomUUID().toString())
                .userId(user1.getUserId())
                .trainId("123456")
                .build();

        // retrieve the user and book the ticket
        Optional<User> savedUserOptional = userRepositoryV2.findByUserEmail(user1.getUserEmail());
        Assertions.assertThat(savedUserOptional).isPresent();

        User savedUser = savedUserOptional.get();
        savedUser.getTicketsBooked().add(ticket);

        // Act
        User updatedUser = userRepositoryV2.save(savedUser);

        // Assert
        Assertions.assertThat(updatedUser).isNotNull();
        Assertions.assertThat(updatedUser.getTicketsBooked()).hasSize(1);
    }
}
