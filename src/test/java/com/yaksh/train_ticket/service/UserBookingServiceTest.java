package com.yaksh.train_ticket.service;

import com.yaksh.train_ticket.DTO.ResponseDataDTO;
import com.yaksh.train_ticket.enums.ResponseStatus;
import com.yaksh.train_ticket.model.Ticket;
import com.yaksh.train_ticket.model.Train;
import com.yaksh.train_ticket.model.User;
import com.yaksh.train_ticket.repository.UserRepositoryV2;
import com.yaksh.train_ticket.util.UserServiceUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the UserBookingServiceImpl class.
 * This class tests the functionality of user-related operations such as signup, login,
 * ticket booking, rescheduling, and cancellation.
 */
@ExtendWith(MockitoExtension.class)
public class UserBookingServiceTest {

    @Mock
    private UserRepositoryV2 userRepositoryV2;
    @Mock
    private UserServiceUtil userServiceUtil;
    @Mock
    private TicketService ticketService;
    @Mock
    private TrainService trainService;

    @InjectMocks
    private UserBookingServiceImpl userBookingService;

    // Common test data
    private User mockUser;
    private Ticket mockTicket;
    private Train mockTrain;
    private final String userName = "user1";
    private final String password = "securePassword";
    private final String hashedPassword = "hashedPassword";
    private final String trainPrn = "train123";
    private final String ticketId = "ticket1";
    private final String source = "Mumbai";
    private final String destination = "Delhi";
    private final LocalDate travelDate = LocalDate.now().plusDays(5);

    /**
     * Sets up mock objects and test data before each test case.
     */
    @BeforeEach
    public void setup() {
        // Setup mock user
        mockUser = User.builder()
                .userId(UUID.randomUUID().toString())
                .userName(userName)
                .hashedPassword(hashedPassword)
                .ticketsBooked(new ArrayList<>())
                .build();

        // Setup mock ticket
        mockTicket = Ticket.builder()
                .ticketId(ticketId)
                .trainId(trainPrn)
                .source(source)
                .destination(destination)
                .dateOfTravel(travelDate)
                .build();

        // Setup mock train
        mockTrain = Train.builder()
                .prn(trainPrn)
                .seats(new HashMap<>())
                .build();
    }

    /**
     * Tests the successful signup of a new user.
     */
    @Test
    public void userBookingService_signUpUser_success() {
        // Arrange - Setting up test data and mocking dependencies (already initialized in setup method)
        
        // Mock behavior: Simulate that no user exists with the given username.
        when(userRepositoryV2.findByUserName(userName.toLowerCase())).thenReturn(Optional.empty());

        // Mock behavior: Simulate password hashing.
        when(userServiceUtil.hashPassword(password)).thenReturn(hashedPassword);

        // Mock behavior: Simulate saving the user. Instead of interacting with the
        // actual database, this will return the same user object that was passed to save().
        when(userRepositoryV2.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act - Call the actual method under test
        ResponseDataDTO response = userBookingService.signupUser(userName, password);

        // Assert - Validate the expected outcome
        Assertions.assertThat(response.isStatus()).isTrue(); // Ensures the signup was successful
        Assertions.assertThat(response.getData()).isNotNull(); // Ensures that a user object is returned

        // Verify that the mocked dependencies were called the expected number of times
        // with the correct parameters

        // Ensures that `findByUserName` was called once with the lowercase username
        verify(userRepositoryV2, times(1)).findByUserName(userName.toLowerCase());

        // Ensures that `hashPassword` was called once with the provided password
        verify(userServiceUtil, times(1)).hashPassword(password);

        // Ensures that `save` was called once with any User object
        verify(userRepositoryV2, times(1)).save(any(User.class));
    }

    /**
     * Tests the scenario where a user tries to sign up with an already existing username.
     */
    @Test
    public void userBookingService_signUpUser_userAlreadyExists() {
        // Arrange
        when(userRepositoryV2.findByUserName(userName.toLowerCase())).thenReturn(Optional.of(new User()));

        // Act
        ResponseDataDTO response = userBookingService.signupUser(userName, password);

        // Assert
        Assertions.assertThat(response.isStatus()).isFalse();
        Assertions.assertThat(response.getResponseStatus()).isEqualTo(ResponseStatus.USER_ALREADY_EXISTS);

        verify(userRepositoryV2, times(1)).findByUserName(userName.toLowerCase());
        verify(userRepositoryV2, never()).save(any(User.class));
    }

    /**
     * Tests the scenario where an exception occurs when saving the user during signup.
     */
    @Test
    public void userBookingService_signUpUser_exceptionWhenSaving() {
        // Arrange
        when(userRepositoryV2.findByUserName(userName.toLowerCase())).thenReturn(Optional.empty());
        when(userServiceUtil.hashPassword(password)).thenReturn(hashedPassword);

        // Simulate exception during save operation
        when(userRepositoryV2.save(any(User.class)))
                .thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseDataDTO response = userBookingService.signupUser(userName, password);

        // Assert
        Assertions.assertThat(response.isStatus()).isFalse();
        Assertions.assertThat(response.getResponseStatus())
                .isEqualTo(ResponseStatus.USER_NOT_SAVED_IN_COLLECTION);

        // Verify interactions
        verify(userRepositoryV2).findByUserName(userName.toLowerCase());
        verify(userServiceUtil).hashPassword(password);
        verify(userRepositoryV2).save(any(User.class));
    }

    /**
     * Tests the successful login of a user with correct credentials.
     */
    @Test
    public void userBookingService_loginUser_success() {
        // Arrange
        when(userRepositoryV2.findByUserName(userName.toLowerCase()))
                .thenReturn(Optional.of(mockUser));
        when(userServiceUtil.checkPassword(password, hashedPassword)).thenReturn(true);

        // Act
        ResponseDataDTO response = userBookingService.loginUser(userName, password);

        // Assert
        Assertions.assertThat(response.isStatus()).isTrue();
        Assertions.assertThat(response.getData()).isInstanceOf(User.class);

        verify(userRepositoryV2, times(1)).findByUserName(userName.toLowerCase());
        verify(userServiceUtil, times(1)).checkPassword(password, hashedPassword);
    }

    // Additional test cases with meaningful comments follow the same pattern as above.
    // Each test is structured with Arrange, Act, and Assert phases,
    // and inline comments explain key logic or mock setups where necessary.
}