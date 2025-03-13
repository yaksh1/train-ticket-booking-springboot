package com.yaksh.train_ticket.util;

import com.yaksh.train_ticket.model.Train;
import com.yaksh.train_ticket.repository.TrainRepositoryV2;

import java.util.List;

public interface TrainServiceUtil {
    boolean validTrain(String source, String destination, Train train);
    boolean doesTrainExist(String prn, TrainRepositoryV2 trainRepositoryV2);
}
