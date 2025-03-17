package com.yaksh.train_ticket.service;

import com.yaksh.train_ticket.DTO.ResponseDataDTO;
import com.yaksh.train_ticket.enums.ResponseStatus;
import com.yaksh.train_ticket.model.Ticket;
import com.yaksh.train_ticket.model.Train;
import com.yaksh.train_ticket.model.User;
import com.yaksh.train_ticket.repository.UserRepositoryV2;
import com.yaksh.train_ticket.util.HelperFunctions;
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
 * Unit tests for the UserBookingService class.
 * This class tests various functionalities of the user booking service, including user signup, login,
 * ticket management, and train-related operations.
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
     * Setup method executed before each test.
     * Initializes mock objects and test data.
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
     * Test case for successful user signup.
     * Verifies that a new user is created and saved successfully.
     */
    @Test
    public void userBookingService_signUpUser_success() {
        // Arrange - Setting up test data and mocking dependencies
        when(userRepositoryV2.findByUserName(userName.toLowerCase())).thenReturn(Optional.empty());
        when(userServiceUtil.hashPassword(password)).thenReturn(hashedPassword);
        when(userRepositoryV2.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act - Call the actual method under test
        ResponseDataDTO response = userBookingService.signupUser(userName, password);

        // Assert - Validate the expected outcome
        Assertions.assertThat(response.isStatus()).isTrue();
        Assertions.assertThat(response.getData()).isNotNull();

        // Verify that the mocked dependencies were called the expected number of times
        verify(userRepositoryV2, times(1)).findByUserName(userName.toLowerCase());
        verify(userServiceUtil, times(1)).hashPassword(password);
        verify(userRepositoryV2, times(1)).save(any(User.class));
    }

    /**
     * Test case for user signup when the user already exists.
     * Verifies that the signup fails with the appropriate response.
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
     * Test case for an exception during user signup.
     * Verifies that the appropriate response is returned when saving the user fails.
     */
    @Test
    public void userBookingService_signUpUser_exceptionWhenSaving() {
        // Arrange
        when(userRepositoryV2.findByUserName(userName.toLowerCase())).thenReturn(Optional.empty());
        when(userServiceUtil.hashPassword(password)).thenReturn(hashedPassword);
        when(userRepositoryV2.save(any(User.class))).thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseDataDTO response = userBookingService.signupUser(userName, password);

        // Assert
        Assertions.assertThat(response.isStatus()).isFalse();
        Assertions.assertThat(response.getResponseStatus()).isEqualTo(ResponseStatus.USER_NOT_SAVED_IN_COLLECTION);

        // Verify interactions
        verify(userRepositoryV2).findByUserName(userName.toLowerCase());
        verify(userServiceUtil).hashPassword(password);
        verify(userRepositoryV2).save(any(User.class));
    }

    /**
     * Test case for successful user login.
     * Verifies that the user can log in with valid credentials.
     */
    @Test
    public void userBookingService_loginUser_success() {
        // Arrange
        when(userRepositoryV2.findByUserName(userName.toLowerCase())).thenReturn(Optional.of(mockUser));
        when(userServiceUtil.checkPassword(password, hashedPassword)).thenReturn(true);

        // Act
        ResponseDataDTO response = userBookingService.loginUser(userName, password);

        // Assert
        Assertions.assertThat(response.isStatus()).isTrue();
        Assertions.assertThat(response.getData()).isInstanceOf(User.class);

        verify(userRepositoryV2, times(1)).findByUserName(userName.toLowerCase());
        verify(userServiceUtil, times(1)).checkPassword(password, hashedPassword);
    }

    /**
     * Test case for user login with an incorrect password.
     * Verifies that the login fails with the appropriate response.
     */
    @Test
    public void userBookingService_loginUser_incorrectPassword() {
        // Arrange
        when(userRepositoryV2.findByUserName(userName.toLowerCase())).thenReturn(Optional.of(mockUser));
        when(userServiceUtil.checkPassword(password, hashedPassword)).thenReturn(false);

        // Act
        ResponseDataDTO response = userBookingService.loginUser(userName, password);

        // Assert
        Assertions.assertThat(response.isStatus()).isFalse();
        Assertions.assertThat(response.getResponseStatus()).isEqualTo(ResponseStatus.PASSWORD_INCORRECT);

        verify(userRepositoryV2, times(1)).findByUserName(userName.toLowerCase());
        verify(userServiceUtil, times(1)).checkPassword(password, hashedPassword);
    }

    /**
     * Test case for user login when the user is not found.
     * Verifies that the login fails with the appropriate response.
     */
    @Test
    public void userBookingService_loginUser_userNotFound() {
        // Arrange
        when(userRepositoryV2.findByUserName(userName.toLowerCase())).thenReturn(Optional.empty());

        // Act
        ResponseDataDTO response = userBookingService.loginUser(userName, password);

        // Assert
        Assertions.assertThat(response.isStatus()).isFalse();
        Assertions.assertThat(response.getResponseStatus()).isEqualTo(ResponseStatus.USER_NOT_FOUND);

        verify(userRepositoryV2, times(1)).findByUserName(userName.toLowerCase());
        verify(userServiceUtil, never()).checkPassword(password, hashedPassword);
    }

    // Additional tests continue below with similar documentation...
}