package com.yaksh.train_ticket.repository;

import com.yaksh.train_ticket.model.Train;

import java.io.IOException;
import java.util.List;

public interface TrainRepository {
    List<Train> getTrainsList();
    void loadTrains() throws IOException;
    boolean saveTrainToFile() throws IOException;

    Train findTrainByPRN(String prn);
}
