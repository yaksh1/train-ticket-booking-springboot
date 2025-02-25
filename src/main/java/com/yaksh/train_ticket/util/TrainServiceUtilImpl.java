package com.yaksh.train_ticket.util;


import com.yaksh.train_ticket.model.Train;
import com.yaksh.train_ticket.repository.TrainRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Override
    public boolean seatsAvailable(List<Integer> seatsOfRowChosen, List<Integer> seatsIndex) {
        // pointer in list of seats to be booked
        int pointerInSeatsIndexList=0;

        // seeing if all seats can be booked by checking if the pointer can reach the end of seatsIndex list
        while( pointerInSeatsIndexList < seatsIndex.size() &&
                seatsOfRowChosen.get(seatsIndex.get(pointerInSeatsIndexList)-1)!=1){
            pointerInSeatsIndexList++;
        }
        return pointerInSeatsIndexList == seatsIndex.size();
    }
}
