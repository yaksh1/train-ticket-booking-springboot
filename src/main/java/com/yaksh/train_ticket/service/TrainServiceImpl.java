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
    public void init() {
        try {
            log.info("Initializing train repository data");
            trainRepository.loadTrains();
        } catch (Exception e) {
            log.error("Error loading trains: {}", e.getMessage(), e);
        }
    }

    @Override
    public boolean saveTrainToFile() throws IOException {
        log.info("Attempting to save trains to file");
        boolean result = trainRepository.saveTrainToFile();
        if (result) {
            log.info("Trains successfully saved to file");
        } else {
            log.error("Failed to save trains to file");
        }
        return result;
    }

    @Override
    public ResponseDataDTO addTrain(Train newTrain) {
        log.info("Attempting to add new train: {}", newTrain.getPrn());
        try {
            ResponseDataDTO response = trainRepository.addTrain(newTrain);
            log.info("Train added successfully: {}", newTrain.getPrn());
            return response;
        } catch (Exception e) {
            log.error("Error adding train {}: {}", newTrain.getPrn(), e.getMessage(), e);
            return CommonResponsesDTOs.trainNotAddedToFIleDTO(e.getMessage());
        }
    }

    @Override
    public ResponseDataDTO addMultipleTrains(List<Train> newTrains) {
        log.info("Attempting to add {} trains", newTrains.size());
        try {
            ResponseDataDTO response = trainRepository.addMultipleTrains(newTrains);
            log.info("Successfully added {} trains", newTrains.size());
            return response;
        } catch (Exception e) {
            log.error("Error adding multiple trains: {}", e.getMessage(), e);
            return CommonResponsesDTOs.trainNotAddedToFIleDTO(e.getMessage());
        }
    }

    @Override
    public ResponseDataDTO updateTrain(Train updatedTrain) {
        log.info("Attempting to update train: {}", updatedTrain.getPrn());
        try {
            ResponseDataDTO response = trainRepository.updateTrain(updatedTrain);
            log.info("Train updated successfully: {}", updatedTrain.getPrn());
            return response;
        } catch (Exception e) {
            log.error("Error updating train {}: {}", updatedTrain.getPrn(), e.getMessage(), e);
            return new ResponseDataDTO(false, ResponseStatus.TRAIN_UPDATING_FAILED, "Error while updating train: " + e.getMessage());
        }
    }

    // Project Assumption: Every train is available every day at same time
    @Override
    public ResponseDataDTO searchTrains(String source, String destination) {
        log.info("Searching trains from {} to {}", source, destination);
        List<Train> trains = trainRepository.getTrainsList()
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
    public ResponseDataDTO canBeBooked(String trainPrn) {
        log.info("Checking if train can be booked: {}", trainPrn);
        // get the train with the prn
        Train train = trainRepository.findTrainByPRN(trainPrn);

        // train not found
        if (train == null) {
            log.warn("Train not found: {}", trainPrn);
            return CommonResponsesDTOs.trainDoesNotExistDTO(trainPrn);
        }

        log.info("Train {} can be booked", trainPrn);
        return new ResponseDataDTO(true, "Can be Booked", train);
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
