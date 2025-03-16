package com.yaksh.train_ticket.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Represents the schedule of a station in the train ticketing system.
 * This class contains details about the station name and its arrival time.
 */
@Data
@AllArgsConstructor // Generates a constructor with all fields as parameters.
@NoArgsConstructor  // Generates a no-argument constructor.
@Builder            // Enables the builder pattern for creating instances of this class.
public class StationSchedule {

    /** The name of the station. */
    private String name;

    /** The arrival time at the station. */
    private LocalDateTime arrivalTime;
}