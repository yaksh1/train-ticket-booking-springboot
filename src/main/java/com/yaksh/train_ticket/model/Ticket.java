package com.yaksh.train_ticket.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collection = "tickets")
public class Ticket {
    @Id
    private String ticketId;
    private String userId;

    private String trainId;
    private LocalDate dateOfTravel;
    private String source;
    private LocalDateTime arrivalTimeAtSource;
    private String destination;
    private LocalDateTime reachingTimeAtDestination;

    private List<List<Integer>> bookedSeatsIndex;

    public String getTicketInfo(){
        return String.format("Ticket ID: %s belongs to User with ID: %s from %s to %s on %s",ticketId,userId,source,destination,dateOfTravel);
    }

}
