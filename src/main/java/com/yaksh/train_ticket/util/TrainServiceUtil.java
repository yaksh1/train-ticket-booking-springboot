package com.yaksh.train_ticket.util;

import com.yaksh.train_ticket.model.Train;
import com.yaksh.train_ticket.repository.TrainRepositoryV2;

import java.time.LocalDate;
import java.util.List;

public interface TrainServiceUtil {
    boolean validTrain(String source, String destination, LocalDate travelDate, Train train);
    boolean doesTrainExist(String prn, TrainRepositoryV2 trainRepositoryV2);
}
