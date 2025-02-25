package com.yaksh.train_ticket.controller;

import com.yaksh.train_ticket.DTO.ResponseDataDTO;
import com.yaksh.train_ticket.service.TrainService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/train")
@RequiredArgsConstructor
public class TrainController {
    private final TrainService trainService;

    @GetMapping("/searchTrains")
    public ResponseDataDTO searchTrains(@RequestParam String source,@RequestParam String destination){
        return trainService.searchTrains(source,destination);
    }



}
