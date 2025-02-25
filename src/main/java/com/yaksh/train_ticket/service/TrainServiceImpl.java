package com.yaksh.train_ticket.service;

import com.yaksh.train_ticket.DTO.ResponseDataDTO;
import com.yaksh.train_ticket.model.Train;
import com.yaksh.train_ticket.repository.TrainRepository;
import com.yaksh.train_ticket.util.TrainServiceUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
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



}
