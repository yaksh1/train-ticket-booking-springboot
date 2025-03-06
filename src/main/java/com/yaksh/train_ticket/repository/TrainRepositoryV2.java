package com.yaksh.train_ticket.repository;

import com.yaksh.train_ticket.model.Train;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TrainRepositoryV2 extends MongoRepository<Train,String> {
}
