package com.yaksh.train_ticket.util;


import com.yaksh.train_ticket.model.Train;
import com.yaksh.train_ticket.repository.TrainRepositoryV2;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
public class TrainServiceUtilImpl implements TrainServiceUtil {
    @Override
    public boolean validTrain(String source, String destination, Train train) {
        int sourceIndx = train.getStations().stream()
                .filter(station -> station.equalsIgnoreCase(source))
                .findFirst()
                .map(train.getStations()::indexOf)
                .orElse(-1);
        int destinationIndx = train.getStations().stream()
                .filter(station -> station.equalsIgnoreCase(destination))
                .findFirst()
                .map(train.getStations()::indexOf)
                .orElse(-1);

        return sourceIndx!= -1 && destinationIndx!=-1 && sourceIndx < destinationIndx;
    }

    @Override
    public boolean doesTrainExist(String prn, TrainRepositoryV2 trainRepositoryV2) {
        Train trainFound = trainRepositoryV2.findById(prn).orElse(null);
        if(trainFound!=null){
            return true;
        }
        return false;
    }
}
