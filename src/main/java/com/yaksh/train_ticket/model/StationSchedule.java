package com.yaksh.train_ticket.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Represents the schedule of a station, including its name and arrival time.
 */
@Data // Lombok annotation to generate getters, setters, equals, hashCode, and toString methods.
@AllArgsConstructor // Lombok annotation to generate a constructor with all fields as parameters.
@NoArgsConstructor // Lombok annotation to generate a no-argument constructor.
public class StationSchedule {
    private String name; // Name of the station.
    private LocalDateTime arrivalTime; // Arrival time at the station.
}