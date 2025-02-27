package com.yaksh.train_ticket.service;

import com.yaksh.train_ticket.DTO.CommonResponsesDTOs;
import com.yaksh.train_ticket.DTO.ResponseDataDTO;
import com.yaksh.train_ticket.enums.ResponseStatus;
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
import java.util.*;
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
    @Override
    public boolean saveTrainToFile() throws IOException {
        return trainRepository.saveTrainToFile();
    }

    @Override
    public ResponseDataDTO addTrain(Train newTrain) {
        try{
            return trainRepository.addTrain(newTrain);
        }catch(Exception e){
            return CommonResponsesDTOs.trainNotAddedToFIleDTO(e.getMessage());
        }
    }

    @Override
    public ResponseDataDTO addMultipleTrains(List<Train> newTrains) {
        try{
            return trainRepository.addMultipleTrains(newTrains);
        }catch(Exception e){
            return CommonResponsesDTOs.trainNotAddedToFIleDTO(e.getMessage());
        }
    }

    @Override
    public ResponseDataDTO updateTrain(Train updatedTrain) {
        try{
            return trainRepository.updateTrain(updatedTrain);
        }catch(Exception e){
            return new ResponseDataDTO(false,ResponseStatus.TRAIN_UPDATING_FAILED,"Error while updating train: "+e.getMessage());
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
    public ResponseDataDTO canBeBooked(String trainPrn){
        // get the train with the prn
        Train train = trainRepository.findTrainByPRN(trainPrn);
        // train not found
        if(train==null){
            return CommonResponsesDTOs.trainDoesNotExistDTO(trainPrn);
        }
        return new ResponseDataDTO(true,"Can be Booked",train);
    }



    @Override
    public ResponseDataDTO areSeatsAvailable(Train train,int numberOfSeatsToBeBooked){
        List<List<Integer>> allSeats = train.getSeats();
        List<int[]> availableSeats = new ArrayList<>();

        int totalSeats = allSeats.size() * allSeats.get(0).size(); // Total number of seats
        int foundSeats = 0;
            if(numberOfSeatsToBeBooked>totalSeats){
            return CommonResponsesDTOs.notEnoughSeatsDTO();
        }
        for (int index = 0; index < totalSeats; index++) {
            int row = index / allSeats.get(0).size(); // Row number
            int col = index % allSeats.get(0).size(); // Column number

            if (allSeats.get(row).get(col) == 0) { // If seat is available
                availableSeats.add(new int[]{row, col});
                foundSeats++;

                if (foundSeats == numberOfSeatsToBeBooked) {
                    return new ResponseDataDTO(true, "Seats found", availableSeats);
                }
            }
        }
        return CommonResponsesDTOs.notEnoughSeatsDTO();
    }
}
