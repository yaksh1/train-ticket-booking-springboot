package com.yaksh.train_ticket.repository;

import com.yaksh.train_ticket.model.Train;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.*;

@DataMongoTest
public class TrainRepositoryTest {
    @Autowired
    private TrainRepositoryV2 trainRepositoryV2;

    @BeforeEach
    void setup(){
        trainRepositoryV2.deleteAll();
    }

    @Test
    public void trainRepository_save_success(){
        // Arrange
        Train train = Train.builder()
                .prn("123456")
                .trainName("Shatabdi")
                .build();

        // Act
        Train savedTrain = trainRepositoryV2.save(train);

        // Assert
        Assertions.assertThat(savedTrain).isNotNull();
        Assertions.assertThat(savedTrain.getPrn()).isEqualTo(train.getPrn());
    }

    @Test
    public void trainRepository_saveAll_success(){
        // Arrange
        Train train1 = Train.builder()
                .prn("123456")
                .trainName("Shatabdi")
                .build();
        Train train2 = Train.builder()
                .prn("789012")
                .trainName("Mumbai Express")
                .build();

        List<Train> trains = Arrays.asList(train1,train2);

        // Act
        List<Train> savedTrains = trainRepositoryV2.saveAll(trains);

        // Assert
        Assertions.assertThat(savedTrains).isNotNull();
        Assertions.assertThat(savedTrains).hasSize(2);
    }

    @Test
    public void trainRepository_findById_success(){
        // Arrange
        Train train1 = Train.builder()
                .prn("123456")
                .trainName("Shatabdi")
                .build();
        trainRepositoryV2.save(train1);

        // Act
        Optional<Train> savedTrain = trainRepositoryV2.findById(train1.getPrn());

        // Assert
        Assertions.assertThat(savedTrain).isPresent();
    }

    @Test
    public void trainRepository_findAll_success(){
        // Arrange
        Train train1 = Train.builder()
                .prn("123456")
                .trainName("Shatabdi")
                .build();
        Train train2 = Train.builder()
                .prn("789012")
                .trainName("Mumbai Express")
                .build();

        List<Train> trains = Arrays.asList(train1,train2);
        trainRepositoryV2.saveAll(trains);


        // Act
        List<Train> savedTrains = trainRepositoryV2.findAll();

        // Assert
        Assertions.assertThat(savedTrains).isNotNull();
        Assertions.assertThat(savedTrains).hasSize(2);
    }

    @Test
    public void trainRepository_updateTrain_success(){
        // Arrange
        Train train1 = Train.builder()
                .prn("123456")
                .trainName("Shatabdi")
                .build();
        Map<String, List<List<Integer>>> seats = new HashMap<>();
        seats.put("2025-03-16", Arrays.asList(
                Arrays.asList(0, 0, 0, 0), // Row 1: (1 = booked, 0 = available)
                Arrays.asList(0, 0, 0, 0), // Row 2
                Arrays.asList(0, 0, 0, 0)  // Row 3
        ));
        train1.setSeats(seats);

       trainRepositoryV2.save(train1);

        // Act
        Optional<Train> savedTrainOptional = trainRepositoryV2.findById("123456");
        Assertions.assertThat(savedTrainOptional).isPresent();
        Train savedTrain = savedTrainOptional.get();

        // book 2 tickets
        savedTrain.getSeats().get("2025-03-16").get(0).set(0,1);
        savedTrain.getSeats().get("2025-03-16").get(0).set(1,1);

        Train updatedTrain = trainRepositoryV2.save(savedTrain);

        // Assert
        Assertions.assertThat(updatedTrain).isNotNull();
        // row 0 col 0
        Assertions.assertThat(updatedTrain.getSeats().get("2025-03-16").get(0).get(0)).isEqualTo(1);
        // row 0 col 1
        Assertions.assertThat(updatedTrain.getSeats().get("2025-03-16").get(0).get(1)).isEqualTo(1);
    }
}
