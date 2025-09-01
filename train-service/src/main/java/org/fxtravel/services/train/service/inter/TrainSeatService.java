package org.fxtravel.services.train.service.inter;

import org.fxtravel.services.payment.service.inter.GoodService;
import org.fxtravel.services.train.dto.TrainSearchResult;
import org.fxtravel.services.train.entitiy.Train;


import java.time.LocalDate;
import java.util.List;

public interface TrainSeatService extends GoodService {
    Train getTrainById(Integer trainId);
    List<TrainSearchResult> findByRouteAndTimeOrderByTime(String fromStation, String toStation, LocalDate departureDate);
    List<TrainSearchResult> findByRouteAndTimeOrderByDuration(String fromStation, String toStation, LocalDate departureDate);
}
