package com.yaksh.train_ticket.controller;

import com.yaksh.train_ticket.DTO.ResponseDataDTO;
import com.yaksh.train_ticket.model.Train;
import com.yaksh.train_ticket.service.TrainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/train")
@RequiredArgsConstructor
@Slf4j
public class TrainController {
    private final TrainService trainService;

    @GetMapping("/searchTrains")
    public ResponseDataDTO searchTrains(@RequestParam String source,@RequestParam String destination){
        return trainService.searchTrains(source,destination);
    }

    @PostMapping("/addTrain")
    public ResponseDataDTO addTrain(@RequestBody Train newTrain){
        return trainService.addTrain(newTrain);
    }

    @PostMapping("/addMultipleTrains")
    public ResponseDataDTO addMultipleTrains(@RequestBody List<Train> newTrains){
        return trainService.addMultipleTrains(newTrains);
    }
    @PostMapping("/updateTrain")
    public ResponseDataDTO updateTrain(@RequestBody Train updatedTrain){
        return trainService.updateTrain(updatedTrain);
    }



}
