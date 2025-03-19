package com.yaksh.train_ticket.repository;

import com.yaksh.train_ticket.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for performing CRUD operations on User entities in the MongoDB database.
 * This interface extends the MongoRepository interface provided by Spring Data MongoDB.
 * It provides built-in methods for interacting with the database and custom query methods as needed.
 */
@Repository
public interface UserRepositoryV2 extends MongoRepository<User, String> {

    /**
     * Finds a User entity based on the provided email address.
     * This method uses a derived query mechanism provided by Spring Data MongoDB
     * to automatically generate the query based on the method name.
     *
     * @param userEmail the email address of the user to be retrieved.
     * @return an Optional containing the User entity if found, or an empty Optional if not found.
     */
    Optional<User> findByUserEmail(String userEmail);
}