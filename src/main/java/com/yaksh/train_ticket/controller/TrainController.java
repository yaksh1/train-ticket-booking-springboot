package com.yaksh.train_ticket.controller;

import com.yaksh.train_ticket.DTO.ResponseDataDTO;
import com.yaksh.train_ticket.model.Train;
import com.yaksh.train_ticket.service.TrainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * TrainController is a REST controller that provides endpoints for managing and searching trains.
 * It handles HTTP requests related to train operations such as searching, adding, and updating train details.
 */
@RestController
@RequestMapping("/v1/train")
@RequiredArgsConstructor
@Slf4j
public class TrainController {

    // Service layer dependency for handling train-related business logic
    private final TrainService trainService;

    /**
     * Searches for trains based on source, destination, and travel date.
     *
     * @param source      The source station of the train.
     * @param destination The destination station of the train.
     * @param travelDate  The date of travel.
     * @return A ResponseEntity containing a ResponseDataDTO with the search results.
     */
    @GetMapping("/searchTrains")
    public ResponseEntity<ResponseDataDTO> searchTrains(@RequestParam String source, @RequestParam String destination, @RequestParam LocalDate travelDate) {
        // Delegates the search logic to the trainService and returns the response
        return ResponseEntity.ok(trainService.searchTrains(source, destination, travelDate));
    }

    /**
     * Adds a new train to the system.
     *
     * @param newTrain The Train object containing details of the new train.
     * @return A ResponseEntity containing a ResponseDataDTO with the result of the operation.
     */
    @PostMapping("/addTrain")
    public ResponseEntity<ResponseDataDTO> addTrain(@RequestBody Train newTrain) {
        // Delegates the add train logic to the trainService and returns the response
        log.info(newTrain.toString());
        return ResponseEntity.ok(trainService.addTrain(newTrain));
    }

    /**
     * Adds multiple trains to the system in a single operation.
     *
     * @param newTrains A list of Train objects containing details of the new trains.
     * @return A ResponseEntity containing a ResponseDataDTO with the result of the operation.
     */
    @PostMapping("/addMultipleTrains")
    public ResponseEntity<ResponseDataDTO> addMultipleTrains(@RequestBody List<Train> newTrains) {
        // Delegates the add multiple trains logic to the trainService and returns the response
        return ResponseEntity.ok(trainService.addMultipleTrains(newTrains));
    }

    /**
     * Updates the details of an existing train.
     *
     * @param updatedTrain The Train object containing updated details of the train.
     * @return A ResponseEntity containing a ResponseDataDTO with the result of the operation.
     */
    @PostMapping("/updateTrain")
    public ResponseEntity<ResponseDataDTO> updateTrain(@RequestBody Train updatedTrain) {
        // Delegates the update train logic to the trainService and returns the response
        return ResponseEntity.ok(trainService.updateTrain(updatedTrain));
    }

}