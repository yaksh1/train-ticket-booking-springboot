package com.yaksh.train_ticket.service;

import com.yaksh.train_ticket.DTO.ResponseDataDTO;
import com.yaksh.train_ticket.model.Train;

import java.util.List;

public interface TrainService {
    ResponseDataDTO searchTrains(String source, String destination);
}
