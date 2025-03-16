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
import static org.mockito.Mockito.*;

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

        @Test
        public void userBookingService_signUpUser_success() {
                // Arrange - Setting up test data and mocking dependencies (already initialized in setup method)
                
                // Mock behavior: Simulate that no user exists with the given username.
                when(userRepositoryV2.findByUserName(userName.toLowerCase())).thenReturn(Optional.empty());

                // Mock behavior: Simulate password hashing.
                when(userServiceUtil.hashPassword(password)).thenReturn(hashedPassword);

                // Mock behavior: Simulate saving the user. Instead of interacting with the
                // actual database,
                // this will return the same user object that was passed to save().
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

        @Test
        public void userBookingService_signUpUser_exceptionWhenSaving() {
                // Arrange

                when(userRepositoryV2.findByUserName(userName.toLowerCase())).thenReturn(Optional.empty());
                when(userServiceUtil.hashPassword(password)).thenReturn(hashedPassword);
                // simulate exception
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

        @Test
        public void userBookingService_loginUser_incorrectPassword() {
                // Arrange

                when(userRepositoryV2.findByUserName(userName.toLowerCase()))
                                .thenReturn(Optional.of(mockUser));
                when(userServiceUtil.checkPassword(password, hashedPassword)).thenReturn(false);

                // Act
                ResponseDataDTO response = userBookingService.loginUser(userName, password);

                // Assert
                Assertions.assertThat(response.isStatus()).isFalse();
                Assertions.assertThat(response.getResponseStatus()).isEqualTo(ResponseStatus.PASSWORD_INCORRECT);

                verify(userRepositoryV2, times(1)).findByUserName(userName.toLowerCase());
                verify(userServiceUtil, times(1)).checkPassword(password, hashedPassword);
        }

        @Test
        public void userBookingService_loginUser_userNotFound() {
                // Arrange

                when(userRepositoryV2.findByUserName(userName.toLowerCase()))
                                .thenReturn(Optional.empty());

                // Act
                ResponseDataDTO response = userBookingService.loginUser(userName, password);

                // Assert
                Assertions.assertThat(response.isStatus()).isFalse();
                Assertions.assertThat(response.getResponseStatus()).isEqualTo(ResponseStatus.USER_NOT_FOUND);

                verify(userRepositoryV2, times(1)).findByUserName(userName.toLowerCase());
                verify(userServiceUtil, never()).checkPassword(password, hashedPassword);
        }

        @Test
        public void userBookingService_fetchAllTickets_success() {
                // Arrange
                List<Ticket> mockTickets = List.of(
                                Ticket.builder()
                                                .ticketId("ticket1")
                                                .build(),
                                Ticket.builder()
                                                .ticketId("ticket2")
                                                .build());
                mockUser.setTicketsBooked(mockTickets);

                // Set logged in User
                ReflectionTestUtils.setField(userBookingService, "loggedInUser", mockUser);

                // Act
                ResponseDataDTO response = userBookingService.fetchAllTickets();

                // Assertions
                Assertions.assertThat(response.isStatus()).isTrue();
                Assertions.assertThat(response.getData()).isNotNull();
                Assertions.assertThat(response.getData()).isInstanceOf(List.class);
        }

        @Test
        public void userBookingService_fetchAllTickets_userNotLoggedIn() {
                // Arrange
                User mockUser = null;

                // Set logged in User
                ReflectionTestUtils.setField(userBookingService, "loggedInUser", mockUser);

                // Act
                ResponseDataDTO response = userBookingService.fetchAllTickets();

                // Assertions
                Assertions.assertThat(response.isStatus()).isFalse();
                Assertions.assertThat(response.getResponseStatus()).isEqualTo(ResponseStatus.USER_NOT_FOUND);
                Assertions.assertThat(response.getData()).isNull();
        }

        @Test
        public void userBookingService_fetchTicketById_success() {
                // Arrange

                // Set logged in User
                ReflectionTestUtils.setField(userBookingService, "loggedInUser", mockUser);

                when(ticketService.findTicketById(any(String.class))).thenReturn(Optional.of(new Ticket()));

                // Act
                ResponseDataDTO response = userBookingService.fetchTicketById("ticket1");

                // Assert
                Assertions.assertThat(response.isStatus()).isTrue();
                Assertions.assertThat(response.getData()).isInstanceOf(Ticket.class);

                verify(ticketService, times(1)).findTicketById("ticket1");
        }

        @Test
        public void userBookingService_fetchTicketById_ticketNotFound() {
                // Arrange

                when(ticketService.findTicketById(any(String.class))).thenReturn(Optional.empty());

                // Set logged in User
                ReflectionTestUtils.setField(userBookingService, "loggedInUser", mockUser);

                // Act
                ResponseDataDTO response = userBookingService.fetchTicketById("ticket1");

                // Assertions
                Assertions.assertThat(response.isStatus()).isFalse();
                Assertions.assertThat(response.getResponseStatus()).isEqualTo(ResponseStatus.TICKET_NOT_FOUND);
                Assertions.assertThat(response.getData()).isNull();
        }

        @Test
        public void userBookingService_fetchTicketById_userNotLoggedIn() {
                // Arrange
                User mockUser = null;

                // Set logged in User
                ReflectionTestUtils.setField(userBookingService, "loggedInUser", mockUser);

                // Act
                ResponseDataDTO response = userBookingService.fetchTicketById("ticket1");

                // Assertions
                Assertions.assertThat(response.isStatus()).isFalse();
                Assertions.assertThat(response.getResponseStatus()).isEqualTo(ResponseStatus.USER_NOT_FOUND);
                Assertions.assertThat(response.getData()).isNull();
        }

        @Test
        public void userBookingService_rescheduleTicket_success() {
                // Arrange

                // set logged in user
                ReflectionTestUtils.setField(userBookingService, "loggedInUser", mockUser);

                LocalDate newTravelDate = LocalDate.now().plusDays(5); // Future date

                when(ticketService.findTicketById(ticketId)).thenReturn(Optional.of(mockTicket));
                when(trainService.canBeBooked(any(), any(), any(), any()))
                                .thenReturn(new ResponseDataDTO(true, "Can be booked"));

                // Act
                ResponseDataDTO response = userBookingService.rescheduleTicket(ticketId, newTravelDate);

                // Assertions
                Assertions.assertThat(response.isStatus()).isTrue();
                verify(ticketService, times(1)).saveTicket(mockTicket);

                // re fetch
                Ticket reFetchedMockTicket = (Ticket) userBookingService.fetchTicketById(ticketId).getData();
                Assertions.assertThat(reFetchedMockTicket.getDateOfTravel()).isEqualTo(newTravelDate);
        }

        @Test
        public void userBookingService_rescheduleTicket_cannotBeBooked() {
                // Arrange

                // set logged in user
                ReflectionTestUtils.setField(userBookingService, "loggedInUser", mockUser);

                
                LocalDate newTravelDate = LocalDate.now().plusDays(5); // Future date

                when(ticketService.findTicketById(ticketId)).thenReturn(Optional.of(mockTicket));
                when(trainService.canBeBooked(any(), any(), any(), any()))
                                .thenReturn(new ResponseDataDTO(false, "Can not be booked"));

                // Act
                ResponseDataDTO response = userBookingService.rescheduleTicket(ticketId, newTravelDate);

                // Assertions
                Assertions.assertThat(response.isStatus()).isFalse();
                verify(ticketService, never()).saveTicket(mockTicket);

        }

        @Test
        public void userBookingService_rescheduleTicket_ticketNotFound() {

                // set logged in user
                ReflectionTestUtils.setField(userBookingService, "loggedInUser", mockUser);

                LocalDate newTravelDate = LocalDate.now().plusDays(5); // Future date

                when(ticketService.findTicketById(ticketId)).thenReturn(Optional.empty());

                // Act
                ResponseDataDTO response = userBookingService.rescheduleTicket(ticketId, newTravelDate);

                // Assertions
                Assertions.assertThat(response.isStatus()).isFalse();
                Assertions.assertThat(response.getResponseStatus()).isEqualTo(ResponseStatus.TICKET_NOT_FOUND);

                verify(ticketService, never()).saveTicket(any(Ticket.class));
        }

        @Test
        public void userBookingService_rescheduleTicket_dateIsInThePast() {
                // Arrange

                // set logged in user
                ReflectionTestUtils.setField(userBookingService, "loggedInUser", mockUser);

                LocalDate newTravelDate = LocalDate.now().minusDays(5); // Future date

                // Act
                ResponseDataDTO response = userBookingService.rescheduleTicket(ticketId, newTravelDate);

                // Assertions
                Assertions.assertThat(response.isStatus()).isFalse();
                Assertions.assertThat(response.getResponseStatus()).isEqualTo(ResponseStatus.INVALID_DATA);

                verify(ticketService, never()).saveTicket(any(Ticket.class));
        }

        @Test
        public void userBookingService_rescheduleTicket_userNotLoggedIn() {
                User mockUser = null;
                
                LocalDate newTravelDate = LocalDate.now().plusDays(3);
                // set logged in user
                ReflectionTestUtils.setField(userBookingService, "loggedInUser", mockUser);

                // Act
                ResponseDataDTO response = userBookingService.rescheduleTicket(ticketId, newTravelDate);

                // Assertions
                Assertions.assertThat(response.isStatus()).isFalse();
                Assertions.assertThat(response.getResponseStatus()).isEqualTo(ResponseStatus.USER_NOT_FOUND);

                verify(ticketService, never()).saveTicket(any(Ticket.class));
        }

        @Test
        public void userBookingService_bookTicket_success() {
                // Arrange
                int seatsToBook = 2;
                List<List<Integer>> seatLayout = List.of(
                                new ArrayList<>(List.of(0, 0, 0)), // Row 1
                                new ArrayList<>(List.of(0, 1, 0)) // Row 2
                );

                ReflectionTestUtils.setField(userBookingService, "loggedInUser", mockUser);

                // Mocking responses
                when(trainService.canBeBooked(trainPrn, source, destination, travelDate))
                                .thenReturn(new ResponseDataDTO(true, "Train can be booked", mockTrain));

                when(trainService.areSeatsAvailable(mockTrain, seatsToBook, travelDate))
                                .thenReturn(new ResponseDataDTO(true, "Seats available", seatLayout));

                when(ticketService.createNewTicket(
                                any(), any(), any(), any(), any(), any(), any(), any()))
                                .thenReturn(mockTicket);

                // Act
                ResponseDataDTO response = userBookingService.bookTicket(trainPrn, source, destination, travelDate,
                                seatsToBook);

                // Assert
                Assertions.assertThat(response.isStatus()).isTrue();
                Assertions.assertThat(response.getData()).isInstanceOf(Ticket.class);
                Assertions.assertThat(mockUser.getTicketsBooked()).contains(mockTicket);
                verify(userRepositoryV2, times(1)).save(mockUser);
                verify(trainService, times(1)).updateTrain(mockTrain);
        }

        @Test
        public void userBookingService_bookTicket_userNotLoggedIn() {
                // Arrange
                ReflectionTestUtils.setField(userBookingService, "loggedInUser", null);

                // Act
                ResponseDataDTO response = userBookingService.bookTicket(trainPrn, source, destination,
                                travelDate, 2);

                // Assert
                Assertions.assertThat(response.isStatus()).isFalse();
                Assertions.assertThat(response.getResponseStatus()).isEqualTo(ResponseStatus.USER_NOT_FOUND);

        }

        @Test
        public void userBookingService_bookTicket_dateInPast() {
                // Arrange
                LocalDate pastDate = LocalDate.now().minusDays(1);
                ReflectionTestUtils.setField(userBookingService, "loggedInUser",mockUser);

                // Act
                ResponseDataDTO response = userBookingService.bookTicket(trainPrn, source, destination, pastDate, 2);

                // Assert
                Assertions.assertThat(response.isStatus()).isFalse();
                Assertions.assertThat(response.getResponseStatus()).isEqualTo(ResponseStatus.INVALID_DATA);

        }

        @Test
        public void userBookingService_bookTicket_trainCannotBeBooked() {
                // Arrange
                ReflectionTestUtils.setField(userBookingService, "loggedInUser", mockUser);

                ResponseDataDTO failedResponse = new ResponseDataDTO(false, "Train is not available");
                when(trainService.canBeBooked(trainPrn, source, destination, travelDate)).thenReturn(failedResponse);

                // Act
                ResponseDataDTO response = userBookingService.bookTicket(trainPrn, source, destination, travelDate, 2);

                // Assert
                Assertions.assertThat(response.isStatus()).isFalse();

        }

        @Test
        public void userBookingService_bookTicket_noSeatsAvailable() {
                // Arrange
                ReflectionTestUtils.setField(userBookingService, "loggedInUser", mockUser);

                ResponseDataDTO trainResponse = new ResponseDataDTO(true, "Train available", mockTrain);
                ResponseDataDTO noSeatsResponse = new ResponseDataDTO(false, "No seats available");

                when(trainService.canBeBooked(trainPrn, source, destination, travelDate)).thenReturn(trainResponse);
                when(trainService.areSeatsAvailable(mockTrain, 2, travelDate)).thenReturn(noSeatsResponse);

                // Act
                ResponseDataDTO response = userBookingService.bookTicket(trainPrn, source, destination, travelDate, 2);

                // Assert
                Assertions.assertThat(response.isStatus()).isFalse();

        }

        @Test
        public void userBookingService_bookTicket_exceptionDuringBooking() {
                // Arrange
                ReflectionTestUtils.setField(userBookingService, "loggedInUser", mockUser);

                List<List<Integer>> seatLayout = List.of(List.of(0, 0, 0));

                when(trainService.canBeBooked(trainPrn, source, destination, travelDate))
                                .thenReturn(new ResponseDataDTO(true, "Train available", mockTrain));

                when(trainService.areSeatsAvailable(mockTrain, 2, travelDate))
                                .thenReturn(new ResponseDataDTO(true, "Seats available", seatLayout));

                when(ticketService.createNewTicket(
                                any(), any(), any(), any(), any(), any(), any(), any()))
                                .thenThrow(new RuntimeException("Database error"));

                // Act
                ResponseDataDTO response = userBookingService.bookTicket(trainPrn, source, destination, travelDate, 2);

                // Assert
                Assertions.assertThat(response.isStatus()).isFalse();
                Assertions.assertThat(response.getResponseStatus()).isEqualTo(ResponseStatus.TICKET_NOT_BOOKED);
        }

        @Test
        public void userBookingService_cancelTicket_success() {
                // Arrange (book seats)
                mockTicket.setBookedSeatsIndex(List.of(
                                new ArrayList<>(List.of(0, 1)), 
                                new ArrayList<>(List.of(0, 0)) 
                ));
                
                ReflectionTestUtils.setField(userBookingService, "loggedInUser", mockUser);
                mockUser.setTicketsBooked(new ArrayList<>(List.of(mockTicket)));
                

                // Mocking responses
                when(trainService.findTrainByPrn(trainPrn)).thenReturn(Optional.of(mockTrain));
                doNothing().when(trainService).freeTheBookedSeats(any(), any(), any());

                // Act
                ResponseDataDTO response = userBookingService.cancelTicket(ticketId);

                // Assert
                Assertions.assertThat(response.isStatus()).isTrue();
                Assertions.assertThat(mockUser.getTicketsBooked()).isEmpty();
                verify(userRepositoryV2, times(1)).save(mockUser);
                verify(trainService, times(1)).updateTrain(mockTrain);
                verify(ticketService, times(1)).deleteTicketById(ticketId);
        }

}
