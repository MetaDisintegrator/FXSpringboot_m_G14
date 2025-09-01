package org.fxtravel.services.train.controller;



import jakarta.servlet.http.HttpSession;
import org.fxtravel.services.train.entitiy.TrainMeal;
import org.fxtravel.services.train.service.inter.TrainMealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/train/meal")
public class TrainMealController {
    @Autowired
    private TrainMealService trainMealService;

    @GetMapping("/{trainId}")
    public ResponseEntity<?> getUserMeals(@PathVariable Integer trainId,
                                          HttpSession session) {

        List<TrainMeal> results = trainMealService.getMealsByTrain4User(trainId);
        return ResponseEntity.ok(Map.of(
                "message", "查询成功",
                "data", results,
                "sortBy", "price"
        ));
    }
}
