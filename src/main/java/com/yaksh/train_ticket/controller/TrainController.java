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

@RestController
@RequestMapping("/v1/train")
@RequiredArgsConstructor
@Slf4j
public class TrainController {
    private final TrainService trainService;

    @GetMapping("/searchTrains")
    public ResponseEntity<ResponseDataDTO> searchTrains(@RequestParam String source, @RequestParam String destination, @RequestParam LocalDate travelDate){
        return ResponseEntity.ok(trainService.searchTrains(source, destination, travelDate));
    }

    @PostMapping("/addTrain")
    public ResponseEntity<ResponseDataDTO> addTrain(@RequestBody Train newTrain){
        return ResponseEntity.ok(trainService.addTrain(newTrain));
    }

    @PostMapping("/addMultipleTrains")
    public ResponseEntity<ResponseDataDTO> addMultipleTrains(@RequestBody List<Train> newTrains){
        return ResponseEntity.ok(trainService.addMultipleTrains(newTrains));
    }
    @PostMapping("/updateTrain")
    public ResponseEntity<ResponseDataDTO> updateTrain(@RequestBody Train updatedTrain){
        return ResponseEntity.ok(trainService.updateTrain(updatedTrain));
    }



}
