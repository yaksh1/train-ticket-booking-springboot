package com.yaksh.train_ticket.service;

import com.yaksh.train_ticket.DTO.ResponseDataDTO;
import com.yaksh.train_ticket.model.Train;

import java.io.IOException;
import java.util.List;

public interface TrainService {
    ResponseDataDTO searchTrains(String source, String destination);
    ResponseDataDTO areSeatsAvailable(Train train,int numberOfSeatsToBeBooked);
    ResponseDataDTO canBeBooked(String trainPrn);

    boolean saveTrainToFile() throws IOException;

    ResponseDataDTO addTrain(Train newTrain);
    ResponseDataDTO addMultipleTrains(List<Train> newTrains);

    ResponseDataDTO updateTrain(Train updatedTrain);


}
