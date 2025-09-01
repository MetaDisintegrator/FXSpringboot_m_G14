package org.fxtravel.services.train.dto;

import lombok.Data;
import org.fxtravel.services.train.common.TrainType;

import java.time.LocalDateTime;

@Data
public class UpdateTrainRequest {
    private String trainNumber;
    private TrainType trainType;
    private String fromStation;
    private String toStation;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
}
