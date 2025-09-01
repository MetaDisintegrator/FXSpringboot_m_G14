package org.fxtravel.services.train.service.inter;


import org.fxtravel.services.train.entitiy.TrainMeal;

import java.util.List;

public interface TrainMealService extends GoodService {
    List<TrainMeal> getMealsByTrain4User(Integer trainId);
    TrainMeal getMealById(Integer id);
}