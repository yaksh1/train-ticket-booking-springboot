package com.yaksh.train_ticket.util;


import com.yaksh.train_ticket.model.StationSchedule;
import com.yaksh.train_ticket.model.Train;
import com.yaksh.train_ticket.repository.TrainRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class TrainServiceUtilImpl implements TrainServiceUtil {
    @Override
    public boolean validTrain(String source, String destination, LocalDate travelDate, Train train) {
        List<StationSchedule> schedules = train.getSchedules().get(HelperFunctions.localDateToString(travelDate));
        if(schedules==null){
            return false;
        }
        log.info(schedules.toString());
        int sourceIndx = schedules.stream()
                .filter(station -> station.getName().equalsIgnoreCase(source))
                .findFirst()
                .map(schedules::indexOf)
                .orElse(-1);
        int destinationIndx = schedules.stream()
                .filter(station -> station.getName().equalsIgnoreCase(destination))
                .findFirst()
                .map(schedules::indexOf)
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
