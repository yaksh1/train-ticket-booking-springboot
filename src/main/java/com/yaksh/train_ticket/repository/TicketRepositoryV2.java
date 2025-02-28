package com.yaksh.train_ticket.repository;

import com.yaksh.train_ticket.model.Ticket;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TicketRepositoryV2 extends MongoRepository<Ticket,String> {
}
