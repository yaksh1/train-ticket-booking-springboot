package com.yaksh.train_ticket.util;

import com.yaksh.train_ticket.model.Train;

import java.util.List;

public interface TrainServiceUtil {
    boolean validTrain(String source, String destination, Train train);
    boolean seatsAvailable(List<Integer> seatsOfRowChosen,List<Integer> seatsIndex);
}
