package com.yaksh.train_ticket.service;

import com.yaksh.train_ticket.DTO.CommonResponsesDTOs;
import com.yaksh.train_ticket.DTO.ResponseDataDTO;
import com.yaksh.train_ticket.enums.ResponseStatus;
import com.yaksh.train_ticket.exceptions.CustomException;
import com.yaksh.train_ticket.model.StationSchedule;
import com.yaksh.train_ticket.model.Train;
import com.yaksh.train_ticket.repository.TrainRepositoryV2;
import com.yaksh.train_ticket.util.TrainServiceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service implementation for managing train-related operations.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TrainServiceImpl implements TrainService {
    private final TrainRepositoryV2 trainRepositoryV2;
    private final TrainServiceUtil trainServiceUtil;

    /**
     * Adds a new train to the repository.
     *
     * @param newTrain The train to be added.
     * @return ResponseDataDTO containing the result of the operation.
     */
    @Override
    public ResponseDataDTO addTrain(Train newTrain) {
        log.info("Attempting to add new train: {}", newTrain.getPrn());
        try {
            // Check if a train with the same PRN already exists
            if (trainServiceUtil.doesTrainExist(newTrain.getPrn(), trainRepositoryV2)) {
                throw new CustomException( ResponseStatus.TRAIN_ALREADY_EXISTS);
            }

            // Save the new train to the repository
            trainRepositoryV2.save(newTrain);
            log.info("Train added successfully: {}", newTrain.getPrn());
            return new ResponseDataDTO(true, "Train added in the collection", newTrain);
        } catch (Exception e) {
            log.error("Error adding train {}: {}", newTrain.getPrn(), e.getMessage(), e);
            throw new CustomException("Error while saving the train: " + e.getMessage(),
                    ResponseStatus.TRAIN_NOT_SAVED_IN_COLLECTION);
        }
    }

    /**
     * Adds multiple trains to the repository.
     *
     * @param newTrains List of trains to be added.
     * @return ResponseDataDTO containing the result of the operation.
     */
    @Override
    public ResponseDataDTO addMultipleTrains(List<Train> newTrains) {
        log.info("Attempting to add {} trains", newTrains.size());
        try {
            // Find existing trains by PRN
            List<String> existingTrainPrns = newTrains.stream().filter(
                    train -> trainServiceUtil.doesTrainExist(train.getPrn(), trainRepositoryV2)
            ).map(train -> train.getPrn()).collect(Collectors.toList());

            // Filter out new trains that do not already exist
            List<Train> newTrainsToAdd = newTrains.stream()
                    .filter(train -> !trainServiceUtil.doesTrainExist(train.getPrn(), trainRepositoryV2))
                    .collect(Collectors.toList());

            // Save the new trains to the repository
            trainRepositoryV2.saveAll(newTrainsToAdd);
            log.info("Successfully added {} trains", newTrains.size());
            log.info("Successfully skipped trains with PRN {}", existingTrainPrns);
            return new ResponseDataDTO(true, "Trains added in the collection except trains with PRN: " + existingTrainPrns, newTrainsToAdd);
        } catch (Exception e) {
            log.error("Error adding multiple trains: {}", e.getMessage(), e);
            throw new CustomException("Error while saving the train: " + e.getMessage(),
                    ResponseStatus.TRAIN_NOT_SAVED_IN_COLLECTION);
        }
    }

    /**
     * Updates an existing train in the repository.
     *
     * @param updatedTrain The train with updated details.
     * @return ResponseDataDTO containing the result of the operation.
     */
    @Override
    public ResponseDataDTO updateTrain(Train updatedTrain) {
        log.info("Attempting to update train: {}", updatedTrain.getPrn());
        try {
            // Save the updated train to the repository
            trainRepositoryV2.save(updatedTrain);
            log.info("Train updated successfully: {}", updatedTrain.getPrn());
            return new ResponseDataDTO(true, "Train updated in the collection", updatedTrain);
        } catch (Exception e) {
            log.error("Error updating train {}: {}", updatedTrain.getPrn(), e.getMessage(), e);
            throw new CustomException("Error while updating train: " + e.getMessage(),ResponseStatus.TRAIN_UPDATING_FAILED);
        }
    }

    /**
     * Books seats for a train by marking them as occupied.
     *
     * @param seatsToBook List of seat positions to be booked.
     * @param allSeats    Current seat layout of the train.
     * @return true if booking is successful.
     */
    @Override
    public boolean bookSeats(List<List<Integer>> seatsToBook, List<List<Integer>> allSeats) {
        // Mark each specified seat as booked (1)
        seatsToBook.forEach(seat -> allSeats.get(seat.get(0)).set(seat.get(1), 1));
        return true;
    }

    /**
     * Frees previously booked seats for a train on a specific travel date.
     *
     * @param bookedSeats List of seat positions to be freed.
     * @param train       The train object.
     * @param travelDate  The travel date for which seats are being freed.
     */
    @Override
    public void freeTheBookedSeats(List<List<Integer>> bookedSeats, Train train, LocalDate travelDate) {
        ResponseDataDTO seatsLayout = getSeatsAtParticularDate(train.getPrn(), travelDate);
        log.info("Seats layout before freeing {}", seatsLayout.getData());
        if (seatsLayout.isStatus()) {
            // Retrieve the current seat layout and mark the specified seats as free (0)
            List<List<Integer>> seatsList = (List<List<Integer>>) seatsLayout.getData();
            bookedSeats.forEach(seat -> seatsList.get(seat.get(0)).set(seat.get(1), 0));
            train.getSeats().put(travelDate.toString(), seatsList);
            log.info("Seats layout after freeing {}", train.getSeats().get(travelDate.toString()));
        }
    }

    /**
     * Gets the arrival time of a train at the source station on a specific travel date.
     *
     * @param train       The train object.
     * @param source      The source station name.
     * @param travelDate  The travel date.
     * @return The arrival time at the source station, or null if not found.
     */
    @Override
    public LocalDateTime getArrivalAtSourceTime(Train train, String source, LocalDate travelDate) {
        ResponseDataDTO isScheduleAvailable = getTrainSchedule(train.getPrn(), travelDate);
        if (isScheduleAvailable.isStatus()) {
            // Find the arrival time at the source station from the schedule
            List<StationSchedule> schedules = (List<StationSchedule>) isScheduleAvailable.getData();
            return schedules.stream()
                    .filter(schedule -> schedule.getName().equalsIgnoreCase(source))
                    .map(StationSchedule::getArrivalTime)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    /**
     * Retrieves the schedule of a train for a given travel date.
     *
     * @param trainPrn    The PRN of the train.
     * @param travelDate  The travel date.
     * @return ResponseDataDTO containing the train schedule.
     */
    @Override
    public ResponseDataDTO getTrainSchedule(String trainPrn, LocalDate travelDate) {
        Train train = trainRepositoryV2.findById(trainPrn).orElse(null);
        if (train == null) {
            throw new CustomException("Train does not exist with PRN: " + trainPrn, ResponseStatus.TRAIN_NOT_FOUND);

        }
        return new ResponseDataDTO(true, String.format("Schedule of train %s fetched successfully", trainPrn), train.getSchedules().get(travelDate.toString()));
    }

    /**
     * Retrieves the seat layout of a train for a specific travel date.
     *
     * @param trainPrn    The PRN of the train.
     * @param travelDate  The travel date.
     * @return ResponseDataDTO containing the seat layout.
     */
    @Override
    public ResponseDataDTO getSeatsAtParticularDate(String trainPrn, LocalDate travelDate) {
        Train train = trainRepositoryV2.findById(trainPrn).orElse(null);
        if (train == null) {
            throw new CustomException("Train does not exist with PRN: " + trainPrn, ResponseStatus.TRAIN_NOT_FOUND);

        }
        return new ResponseDataDTO(true, String.format("Seats of train %s fetched successfully", trainPrn), train.getSeats().get(travelDate.toString()));
    }

    /**
     * Searches for trains between a source and destination on a specific travel date.
     *
     * @param source      The source station name.
     * @param destination The destination station name.
     * @param travelDate  The travel date.
     * @return ResponseDataDTO containing the search results.
     */
    @Override
    public ResponseDataDTO searchTrains(String source, String destination, LocalDate travelDate) {
        log.info("Searching trains from {} to {}", source, destination);
        // Filter trains that are valid for the given source, destination, and travel date
        List<Train> trains = trainRepositoryV2.findAll()
                .stream()
                .filter(train -> trainServiceUtil.validTrain(source, destination, travelDate, train))
                .collect(Collectors.toList());

        Map<String, Object> result = Map.of(
                "totalTrains", trains.size(),
                "trainsData", trains
        );

        log.info("Found {} trains from {} to {}", trains.size(), source, destination);
        return new ResponseDataDTO(true, "Trains fetched", result);
    }

    /**
     * Checks if a train can be booked for a given source, destination, and travel date.
     *
     * @param trainPrn    The PRN of the train.
     * @param source      The source station name.
     * @param destination The destination station name.
     * @param travelDate  The travel date.
     * @return ResponseDataDTO containing the result of the check.
     */
    @Override
    public ResponseDataDTO canBeBooked(String trainPrn, String source, String destination, LocalDate travelDate) {
        log.info("Checking if train can be booked: {}", trainPrn);
        // Retrieve the train by PRN
        Train train = trainRepositoryV2.findById(trainPrn).orElse(null);

        // Train not found
        if (train == null) {
            log.warn("Train not found: {}", trainPrn);
            throw new CustomException("Train does not exist with PRN: " + trainPrn, ResponseStatus.TRAIN_NOT_FOUND);
        }

        // Validate if the train aligns with the given source, destination, and travel date
        boolean validTrain = trainServiceUtil.validTrain(source, destination, travelDate, train);
        if (!validTrain) {
            throw new CustomException(
                    "Can not be Booked: Source and destination do not align with train data", ResponseStatus.INVALID_DATA);
        }

        log.info("Train {} can be booked", trainPrn);
        return new ResponseDataDTO(true, "Can be Booked", train);
    }

    /**
     * Finds a train by its PRN.
     *
     * @param prn The PRN of the train.
     * @return An Optional containing the train if found.
     */
    @Override
    public Optional<Train> findTrainByPrn(String prn) {
        return trainRepositoryV2.findById(prn);
    }

    /**
     * Checks if the requested number of seats are available for a train on a specific travel date.
     *
     * @param train                  The train object.
     * @param numberOfSeatsToBeBooked The number of seats requested.
     * @param travelDate             The travel date.
     * @return ResponseDataDTO containing the seat availability result.
     */
    @Override
    public ResponseDataDTO areSeatsAvailable(Train train, int numberOfSeatsToBeBooked, LocalDate travelDate) {
        log.info("Checking seat availability for train {}: {} seats requested", train.getPrn(), numberOfSeatsToBeBooked);
        List<List<Integer>> allSeats = train.getSeats().get(travelDate.toString());
        List<List<Integer>> availableSeats = new ArrayList<>();

        int totalSeats = allSeats.size() * allSeats.get(0).size(); // Total number of seats
        int foundSeats = 0;

        // If the requested number of seats exceeds the total number of seats
        if (numberOfSeatsToBeBooked > totalSeats) {
            log.warn("Not enough seats available in train {}: requested {} seats, total seats {}", train.getPrn(), numberOfSeatsToBeBooked, totalSeats);
                    throw new CustomException("Not enough seats available", ResponseStatus.NOT_ENOUGH_SEATS);

        }

        int foundContinuousSeats = 0;
        // Try to find continuous seats first
        for (int index = 0; index < totalSeats; index++) {
            int row = index / allSeats.get(0).size(); // Row number
            int col = index % allSeats.get(0).size(); // Column number

            // Reset counter and available seats if a booked seat is found
            if (allSeats.get(row).get(col) == 1) {
                foundContinuousSeats = 0;
                availableSeats = new ArrayList<>();
            } else {
                foundContinuousSeats++;
                availableSeats.add(Arrays.asList(row, col));
            }

            // If enough continuous seats are found
            if (foundContinuousSeats == numberOfSeatsToBeBooked) {
                log.info("Found {} available continuous seats in train {}", numberOfSeatsToBeBooked, train.getPrn());
                return new ResponseDataDTO(true, "Seats found", availableSeats);
            }
        }

        // Continuous seats not found; try to find separate seats
        log.info("Continuous seats not found", numberOfSeatsToBeBooked, train.getPrn());
        availableSeats = new ArrayList<>();

        for (int index = 0; index < totalSeats; index++) {
            int row = index / allSeats.get(0).size(); // Row number
            int col = index % allSeats.get(0).size(); // Column number

            if (allSeats.get(row).get(col) == 0) { // If seat is available
                availableSeats.add(Arrays.asList(row, col));
                foundSeats++;

                // If enough separate seats are found
                if (foundSeats == numberOfSeatsToBeBooked) {
                    log.info("Found {} available seats in train {}", numberOfSeatsToBeBooked, train.getPrn());
                    return new ResponseDataDTO(true, "Seats found", availableSeats);
                }
            }
        }

        // Not enough seats found
        log.warn("Not enough seats available in train {}: requested {} seats, found {} seats", train.getPrn(), numberOfSeatsToBeBooked, foundSeats);
        throw new CustomException("Not enough seats available", ResponseStatus.NOT_ENOUGH_SEATS);

    }
}