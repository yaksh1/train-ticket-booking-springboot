package com.yaksh.train_ticket.service;

import com.yaksh.train_ticket.DTO.ResponseDataDTO;
import com.yaksh.train_ticket.model.Ticket;
import com.yaksh.train_ticket.model.Train;
import com.yaksh.train_ticket.model.User;
import com.yaksh.train_ticket.repository.TrainRepository;
import com.yaksh.train_ticket.util.TrainServiceUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TrainServiceImpl implements TrainService {
    private final TrainRepository trainRepository;
    private final TrainServiceUtil trainServiceUtil;

    @PostConstruct
    public void init(){
        try{
            trainRepository.loadTrains();
        }catch(Exception e){
            log.error("Error loading trains: {}", e.getMessage());
        }
    }


    // Project Assumption: Every train is available every day at same time
    @Override
    public ResponseDataDTO searchTrains(String source, String destination) {
        List<Train> trains = trainRepository.getTrainsList()
                .stream()
                .filter(train -> trainServiceUtil.validTrain(source,destination,train))
                .collect(Collectors.toList());
        Map<String,Object> result = Map.of(
                "totalTrains",trains.size(),
                "trainsData",trains
        );
        return new ResponseDataDTO(true,"Trains fetched",result);
    }

    @Override
    public ResponseDataDTO canBeBooked(String trainPrn,int row){
        // get the train with the prn
        Train train = trainRepository.findTrainByPRN(trainPrn);
        // train not found
        if(train==null){
            return new ResponseDataDTO(false,"Train does not exist with prn: "+trainPrn,null);
        }
        if(row<1 || row > train.getSeats().size()){
            return new ResponseDataDTO(false,"Invalid row number",null);
        }
        return new ResponseDataDTO(true,"Can be Booked",train);
    }

    @Override
    public boolean saveTrainToFile() throws IOException {
        return trainRepository.saveTrainToFile();
    }

    @Override
    public ResponseDataDTO areSeatsAvailable(List<Integer> seatsOfRowChosen,List<Integer> seatsIndex){
        // pointer in list of seats to be booked
        int pointerInSeatsIndexList=0;

        // seeing if all seats can be booked by checking if the pointer can reach the end of seatsIndex list
        while( pointerInSeatsIndexList < seatsIndex.size() &&
                seatsOfRowChosen.get(seatsIndex.get(pointerInSeatsIndexList)-1)!=1){
            pointerInSeatsIndexList++;
        }
        return new ResponseDataDTO(pointerInSeatsIndexList==seatsIndex.size(),"Invalid row number",null);
    }
}
