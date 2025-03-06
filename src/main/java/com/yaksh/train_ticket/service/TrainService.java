package com.yaksh.train_ticket.service;

import com.yaksh.train_ticket.DTO.ResponseDataDTO;
import com.yaksh.train_ticket.model.Train;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface TrainService {
    ResponseDataDTO searchTrains(String source, String destination);
    ResponseDataDTO areSeatsAvailable(Train train,int numberOfSeatsToBeBooked);
    ResponseDataDTO canBeBooked(String trainPrn);
    Optional<Train> findTrainByPrn(String prn);

    ResponseDataDTO addTrain(Train newTrain);
    ResponseDataDTO addMultipleTrains(List<Train> newTrains);

    ResponseDataDTO updateTrain(Train updatedTrain);



}
