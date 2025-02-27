package com.yaksh.train_ticket.repository;

import com.yaksh.train_ticket.DTO.ResponseDataDTO;
import com.yaksh.train_ticket.model.Train;

import java.io.IOException;
import java.util.List;

public interface TrainRepository {
    List<Train> getTrainsList();
    void loadTrains() throws IOException;
    boolean saveTrainToFile() throws IOException;

    Train findTrainByPRN(String prn);

    ResponseDataDTO addTrain(Train newTrain) throws IOException;
    ResponseDataDTO addMultipleTrains(List<Train> newTrains) throws IOException;

    ResponseDataDTO updateTrain(Train newTrain) throws IOException;

}
