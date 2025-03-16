package com.yaksh.train_ticket.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collection = "tickets")
public class Ticket {
    @Id
    private String ticketId;
    @NotNull(message = "User ID cannot be null")
    private String userId;
    @NotNull(message = "Train ID cannot be null")
    private String trainId;
    @NotNull(message = "Date of travel cannot be null")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateOfTravel;
    @NotNull(message = "Source cannot be null")
    private String source;

    private LocalDateTime arrivalTimeAtSource;
    @NotNull(message = "Destination cannot be null")
    private String destination;
    private LocalDateTime reachingTimeAtDestination;

    private List<List<Integer>> bookedSeatsIndex;

    public String getTicketInfo(){
        return String.format("Ticket ID: %s belongs to User with ID: %s from %s to %s on %s",ticketId,userId,source,destination,dateOfTravel);
    }

}
