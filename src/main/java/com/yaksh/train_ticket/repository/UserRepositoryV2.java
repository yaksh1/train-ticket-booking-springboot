package com.yaksh.train_ticket.repository;

import com.yaksh.train_ticket.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepositoryV2 extends MongoRepository<User,String> {
    Optional<User> findByUserName(String username);
}
