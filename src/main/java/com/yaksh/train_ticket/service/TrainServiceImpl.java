package com.yaksh.train_ticket.service;

import com.yaksh.train_ticket.DTO.CommonResponsesDTOs;
import com.yaksh.train_ticket.DTO.ResponseDataDTO;
import com.yaksh.train_ticket.enums.ResponseStatus;
import com.yaksh.train_ticket.model.StationSchedule;
import com.yaksh.train_ticket.model.Train;
import com.yaksh.train_ticket.repository.TrainRepositoryV2;
import com.yaksh.train_ticket.util.HelperFunctions;
import com.yaksh.train_ticket.util.TrainServiceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
            // if train with same prn exists then return false
            if(trainServiceUtil.doesTrainExist(newTrain.getPrn(),trainRepositoryV2)){
                return new ResponseDataDTO(false,ResponseStatus.TRAIN_ALREADY_EXISTS,"Train added in the collection");
            }

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
            List<String> existingTrainPrns = newTrains.stream().filter(
                    train -> trainServiceUtil.doesTrainExist(train.getPrn(),trainRepositoryV2)
            ).map(train -> train.getPrn()).collect(Collectors.toList());

            List<Train> newTrainsToAdd = newTrains.stream()
                    .filter(train -> !trainServiceUtil.doesTrainExist(train.getPrn(), trainRepositoryV2)) // Only new trains
                    .collect(Collectors.toList());
            trainRepositoryV2.saveAll(newTrainsToAdd);
            log.info("Successfully added {} trains", newTrains.size());
            log.info("Successfully skipped trains with prn {}", existingTrainPrns);
            return new ResponseDataDTO(true,"Trains added in the collection except trains with prn: "+ existingTrainPrns,newTrainsToAdd);
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

    @Override
    public boolean bookSeats(List<List<Integer>> seatsToBook,List<List<Integer>> allSeats) {
        seatsToBook.forEach(seat -> allSeats.get(seat.get(0)).set(seat.get(1), 1));
        return true;
    }

    @Override
    public void freeTheBookedSeats(List<List<Integer>> bookedSeats, Train train,LocalDate travelDate) {
        ResponseDataDTO seatsLayout = getSeatsAtParticularDate(train.getPrn(),travelDate);
        log.info("seats layout before freeing {}",seatsLayout.getData());
        if(seatsLayout.isStatus()){
            List<List<Integer>> seatsList = (List<List<Integer>>) seatsLayout.getData();
            bookedSeats.forEach(seat -> seatsList.get(seat.get(0)).set(seat.get(1), 0));
            train.getSeats().put(travelDate.toString(),seatsList);
            log.info("seats layout after freeing {}",train.getSeats().get(travelDate.toString()));
        }
    }

    @Override
    public LocalDateTime getArrivalAtSourceTime(Train train, String source,LocalDate travelDate) {
        ResponseDataDTO isScheduleAvailable = getTrainSchedule(train.getPrn(),travelDate);
        if(isScheduleAvailable.isStatus()){
            List<StationSchedule> schedules = (List<StationSchedule>) isScheduleAvailable.getData();
            return schedules.
                    stream().
                    filter(schedule->schedule.getName().equalsIgnoreCase(source))
                    .map(StationSchedule::getArrivalTime)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    @Override
    public ResponseDataDTO getTrainSchedule(String trainPrn, LocalDate travelDate) {
        Train train = trainRepositoryV2.findById(trainPrn).orElse(null);
        if(train==null){
            return CommonResponsesDTOs.trainDoesNotExistDTO(trainPrn);
        }
        return new ResponseDataDTO(true,String.format("Schedule of train %s fetched successfully",trainPrn),train.getSchedules().get(HelperFunctions.localDateToString(travelDate)));
    }

    @Override
    public ResponseDataDTO getSeatsAtParticularDate(String trainPrn, LocalDate travelDate) {
        Train train = trainRepositoryV2.findById(trainPrn).orElse(null);
        if(train==null){
            return CommonResponsesDTOs.trainDoesNotExistDTO(trainPrn);
        }
        return new ResponseDataDTO(true,String.format("Seats of train %s fetched successfully",trainPrn),train.getSeats().get(HelperFunctions.localDateToString(travelDate)));
    }

    @Override
    public ResponseDataDTO searchTrains(String source, String destination,LocalDate travelDate) {
        log.info("Searching trains from {} to {}", source, destination);
        List<Train> trains = trainRepositoryV2.findAll()
                .stream()
                .filter(train -> trainServiceUtil.validTrain(source, destination,travelDate, train))
                .collect(Collectors.toList());

        Map<String, Object> result = Map.of(
                "totalTrains", trains.size(),
                "trainsData", trains
        );

        log.info("Found {} trains from {} to {}", trains.size(), source, destination);
        return new ResponseDataDTO(true, "Trains fetched", result);
    }

    @Override
    public ResponseDataDTO canBeBooked(String trainPrn,String source,String destination,LocalDate travelDate) {
        log.info("Checking if train can be booked: {}", trainPrn);
        // get the train with the prn
        Train train = trainRepositoryV2.findById(trainPrn).orElse(null);

        // train not found
        if (train == null) {
            log.warn("Train not found: {}", trainPrn);
            return CommonResponsesDTOs.trainDoesNotExistDTO(trainPrn);
        }
        // if source and destination align with train data
        boolean validTrain = trainServiceUtil.validTrain(source,destination,travelDate,train);
        if(!validTrain){
            return new ResponseDataDTO(false, "Can not be Booked: Source and destination do not align with train data");
        }

        log.info("Train {} can be booked", trainPrn);
        return new ResponseDataDTO(true, "Can be Booked", train);
    }

    @Override
    public Optional<Train> findTrainByPrn(String prn) {
        return trainRepositoryV2.findById(prn);
    }

    @Override
    public ResponseDataDTO areSeatsAvailable(Train train, int numberOfSeatsToBeBooked,LocalDate travelDate) {
        log.info("Checking seat availability for train {}: {} seats requested", train.getPrn(), numberOfSeatsToBeBooked);
        List<List<Integer>> allSeats = train.getSeats().get(HelperFunctions.localDateToString(travelDate));
        List<List<Integer>> availableSeats = new ArrayList<>();

        int totalSeats = allSeats.size() * allSeats.get(0).size(); // Total number of seats
        int foundSeats = 0;

        if (numberOfSeatsToBeBooked > totalSeats) {
            log.warn("Not enough seats available in train {}: requested {} seats, total seats {}", train.getPrn(), numberOfSeatsToBeBooked, totalSeats);
            return CommonResponsesDTOs.notEnoughSeatsDTO();
        }
        int foundContinuousSeats= 0;
        // first try to find continuous seats if available
        for(int index=0;index<totalSeats;index++){
            int row = index / allSeats.get(0).size(); // Row number
            int col = index % allSeats.get(0).size(); // Column number

            // if a filled seat found then initialize counter and seats list to zero
            if(allSeats.get(row).get(col)==1){
                foundContinuousSeats=0;
                availableSeats = new ArrayList<>();
            }else{
                foundContinuousSeats++;
                availableSeats.add(Arrays.asList(row,col));
            }

            if(foundContinuousSeats==numberOfSeatsToBeBooked){
                log.info("Found {} available continuous seats in train {}", numberOfSeatsToBeBooked, train.getPrn());
                return new ResponseDataDTO(true, "Seats found", availableSeats);
            }
        }

        // continuous seats not found
        log.info("Continuous seats not found", numberOfSeatsToBeBooked, train.getPrn());
        availableSeats = new ArrayList<>();

        // try to find seats separately
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
