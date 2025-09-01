package org.fxtravel.services.train.dto;

import lombok.Data;
import org.fxtravel.services.train.entitiy.Train;
import org.fxtravel.services.train.entitiy.TrainSeat;

import java.util.List;

@Data
public class TrainSearchResult {
    private Train train;                    // 列车信息
    private List<TrainSeat> trainseats;     // 该列车的所有座位信息
}