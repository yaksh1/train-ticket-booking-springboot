package com.yaksh.train_ticket.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Ticket {
    private String ticketId;
    private String userId;
    private Train train;
    private String dateOfTravel;
    private String source;
    private String destination;

    public String getTicketInfo(){
        return String.format("Ticket ID: %s belongs to User with ID: %s from %s to %s on %s",ticketId,userId,source,destination,dateOfTravel);
    }

}
