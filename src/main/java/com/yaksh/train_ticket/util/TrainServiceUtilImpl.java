package com.yaksh.train_ticket.util;


import com.yaksh.train_ticket.model.Train;
import com.yaksh.train_ticket.repository.TrainRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TrainServiceUtilImpl implements TrainServiceUtil {
    private final TrainRepository trainRepository;

    @Override
    public boolean validTrain(String source, String destination, Train train) {
        int sourceIndx = train.getStations().indexOf(source.toLowerCase());
        int destinationIndx = train.getStations().indexOf(destination.toLowerCase());

        return sourceIndx!= -1 && destinationIndx!=-1 && sourceIndx < destinationIndx;
    }
}
