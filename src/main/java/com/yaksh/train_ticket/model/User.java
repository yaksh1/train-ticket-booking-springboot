package com.yaksh.train_ticket.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.List;

/**
 * Represents a User entity in the system.
 * This class is annotated to map to a MongoDB document in the "users" collection.
 * It uses Lombok annotations to reduce boilerplate code for constructors, getters, setters, etc.
 * The class also supports JSON serialization/deserialization with specific naming strategies.
 */
@AllArgsConstructor // Generates a constructor with all fields as arguments.
@NoArgsConstructor  // Generates a no-argument constructor.
@Data               // Generates getters, setters, equals, hashCode, and toString methods.
@Builder            // Enables the builder pattern for object creation.
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class) // Maps JSON fields to snake_case naming convention.
@JsonIgnoreProperties(ignoreUnknown = true) // Ignores unknown properties during JSON deserialization.
@Document(collection = "users") // Maps this class to the "users" collection in MongoDB.
public class User {
    @Id // Marks this field as the primary identifier for the MongoDB document.
    private String userId; // Unique identifier for the user.

    private String userEmail; // Email address of the user.

    private String hashedPassword; // Hashed password for secure authentication.

    @DocumentReference // Establishes a reference to other documents (tickets) in MongoDB.
    private List<Ticket> ticketsBooked; // List of tickets booked by the user.
}