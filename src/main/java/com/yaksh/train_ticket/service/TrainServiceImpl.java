package com.yaksh.train_ticket.service;

import com.yaksh.train_ticket.DTO.CommonResponsesDTOs;
import com.yaksh.train_ticket.DTO.ResponseDataDTO;
import com.yaksh.train_ticket.enums.ResponseStatus;
import com.yaksh.train_ticket.model.Train;
import com.yaksh.train_ticket.repository.TrainRepositoryV2;
import com.yaksh.train_ticket.util.TrainServiceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TrainServiceImpl implements TrainService {
    private final TrainRepositoryV2 trainRepositoryV2;
    private final TrainServiceUtil trainServiceUtil;


    @Override
    public ResponseDataDTO addTrain(Train newTrain) {
        log.info("Attempting to add new train: {}", newTrain.getPrn());
        try {
            trainRepositoryV2.save(newTrain);
            log.info("Train added successfully: {}", newTrain.getPrn());
            return new ResponseDataDTO(true,"Train added in the collection",newTrain);
        } catch (Exception e) {
            log.error("Error adding train {}: {}", newTrain.getPrn(), e.getMessage(), e);
            return CommonResponsesDTOs.trainNotAddedToCollectionDTO(e.getMessage());
        }
    }

    @Override
    public ResponseDataDTO addMultipleTrains(List<Train> newTrains) {
        log.info("Attempting to add {} trains", newTrains.size());
        try {

            trainRepositoryV2.saveAll(newTrains);
            log.info("Successfully added {} trains", newTrains.size());
            return new ResponseDataDTO(true,"Trains added in the collection",newTrains);
        } catch (Exception e) {
            log.error("Error adding multiple trains: {}", e.getMessage(), e);
            return CommonResponsesDTOs.trainNotAddedToCollectionDTO(e.getMessage());
        }
    }

    @Override
    public ResponseDataDTO updateTrain(Train updatedTrain) {
        log.info("Attempting to update train: {}", updatedTrain.getPrn());
        try {
            trainRepositoryV2.save(updatedTrain);

            log.info("Train updated successfully: {}", updatedTrain.getPrn());
            return new ResponseDataDTO(true,"Train updated in the collection",updatedTrain);
        } catch (Exception e) {
            log.error("Error updating train {}: {}", updatedTrain.getPrn(), e.getMessage(), e);
            return new ResponseDataDTO(false, ResponseStatus.TRAIN_UPDATING_FAILED, "Error while updating train: " + e.getMessage());
        }
    }

    // Project Assumption: Every train is available every day at same time
    @Override
    public ResponseDataDTO searchTrains(String source, String destination) {
        log.info("Searching trains from {} to {}", source, destination);
        List<Train> trains = trainRepositoryV2.findAll()
                .stream()
                .filter(train -> trainServiceUtil.validTrain(source, destination, train))
                .collect(Collectors.toList());

        Map<String, Object> result = Map.of(
                "totalTrains", trains.size(),
                "trainsData", trains
        );

        log.info("Found {} trains from {} to {}", trains.size(), source, destination);
        return new ResponseDataDTO(true, "Trains fetched", result);
    }

    @Override
    public ResponseDataDTO canBeBooked(String trainPrn,String source,String destination) {
        log.info("Checking if train can be booked: {}", trainPrn);
        // get the train with the prn
        Train train = trainRepositoryV2.findById(trainPrn).orElse(null);

        // train not found
        if (train == null) {
            log.warn("Train not found: {}", trainPrn);
            return CommonResponsesDTOs.trainDoesNotExistDTO(trainPrn);
        }
        boolean validTrain = trainServiceUtil.validTrain(source,destination,train);
        if(!validTrain){
            return new ResponseDataDTO(false, "Can not be Booked: Source and destination do not align with train data", train);
        }

        log.info("Train {} can be booked", trainPrn);
        return new ResponseDataDTO(true, "Can be Booked", train);
    }

    @Override
    public Optional<Train> findTrainByPrn(String prn) {
        return trainRepositoryV2.findById(prn);
    }

    @Override
    public ResponseDataDTO areSeatsAvailable(Train train, int numberOfSeatsToBeBooked) {
        log.info("Checking seat availability for train {}: {} seats requested", train.getPrn(), numberOfSeatsToBeBooked);
        List<List<Integer>> allSeats = train.getSeats();
        List<List<Integer>> availableSeats = new ArrayList<>();

        int totalSeats = allSeats.size() * allSeats.get(0).size(); // Total number of seats
        int foundSeats = 0;

        if (numberOfSeatsToBeBooked > totalSeats) {
            log.warn("Not enough seats available in train {}: requested {} seats, total seats {}", train.getPrn(), numberOfSeatsToBeBooked, totalSeats);
            return CommonResponsesDTOs.notEnoughSeatsDTO();
        }

        for (int index = 0; index < totalSeats; index++) {
            int row = index / allSeats.get(0).size(); // Row number
            int col = index % allSeats.get(0).size(); // Column number

            if (allSeats.get(row).get(col) == 0) { // If seat is available
                availableSeats.add(Arrays.asList(row,col));
                foundSeats++;

                if (foundSeats == numberOfSeatsToBeBooked) {
                    log.info("Found {} available seats in train {}", numberOfSeatsToBeBooked, train.getPrn());
                    return new ResponseDataDTO(true, "Seats found", availableSeats);
                }
            }
        }

        log.warn("Not enough seats available in train {}: requested {} seats, found {} seats", train.getPrn(), numberOfSeatsToBeBooked, foundSeats);
        return CommonResponsesDTOs.notEnoughSeatsDTO();
    }
}
