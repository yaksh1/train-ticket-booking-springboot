package com.yaksh.train_ticket.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yaksh.train_ticket.model.Train;
import com.yaksh.train_ticket.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Slf4j
@Repository
public class TrainRepositoryImpl implements TrainRepository{
    private static final String TRAINS_PATH = "C:\\Users\\91635\\Desktop\\Projects\\train-ticket\\src\\main\\java\\com\\yaksh\\train_ticket\\repository\\trains.json";
    // to serialize/deserialize the data (userName -> user_name)
    private ObjectMapper objectMapper = new ObjectMapper();
    private List<Train> trainList;


    @Override
    public List<Train> getTrainsList() {
        return trainList;
    }

    @Override
    public void loadTrains() throws IOException {
        File trainsData = new File(TRAINS_PATH);
        if(trainsData.exists()){
            trainList = objectMapper.readValue(trainsData, new TypeReference<List<Train>>() {});
            log.info("Trains loaded successfully.");
        } else {
            log.warn("Trains file not found, initializing empty train list.");
        }
    }

    @Override
    public boolean saveTrainToFile() throws IOException {
        File trainsFile = new File(TRAINS_PATH);
        long initialSize = trainsFile.exists() ? trainsFile.length():0;
        try{
            objectMapper.writeValue(trainsFile,trainList);
            long newSize = trainsFile.length();
            return newSize>initialSize;
        }catch (Exception e){
            log.error("Error writing to file: {}", e.getMessage());
            return false; // Return false if an exception occurs
        }
    }


}
