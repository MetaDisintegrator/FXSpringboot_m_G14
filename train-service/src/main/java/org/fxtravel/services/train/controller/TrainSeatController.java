package org.fxtravel.services.train.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.fxtravel.services.train.client.UserClient;
import org.fxtravel.services.train.dto.SearchTrainRequest;
import org.fxtravel.services.train.dto.TrainSearchResult;
import org.fxtravel.services.train.entitiy.Train;
import org.fxtravel.services.train.service.inter.TrainSeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/train")
public class TrainSeatController {
    @Autowired
    TrainSeatService trainSeatService;

    UserClient userClient;

    @GetMapping("/by-id/{id}")
    public Train getTrain(@RequestHeader("X-User-Id") String userId, @PathVariable Integer id) {
        return trainSeatService.getTrainById(id);
    }

    // 按出发时间排序查询车次接口
    @PostMapping("/seat/by-departure-time")
    public ResponseEntity<?> searchTrainByDepartureTime(@RequestHeader("X-User-Id") String userId,@Valid @RequestBody SearchTrainRequest request,
                                                        BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors()
                    .stream()
                    .map(e -> e.getField() + ": " + e.getDefaultMessage())
                    .toList();
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }

        try {
            List<TrainSearchResult> results = trainSeatService.findByRouteAndTimeOrderByTime(
                    request.getDepartureStation(),
                    request.getArrivalStation(),
                    request.getDepartureDate()
            );

            return ResponseEntity.ok(Map.of(
                    "message", "查询成功",
                    "data", results,
                    "sortBy", "departureTime"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "查询失败: " + e.getMessage()));
        }
    }

    @PostMapping("/seat/by-duration-time")
    public ResponseEntity<?> searchTrainByDuration(@RequestHeader("X-User-Id") String userId,@Valid @RequestBody SearchTrainRequest request,
                                                        BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors()
                    .stream()
                    .map(e -> e.getField() + ": " + e.getDefaultMessage())
                    .toList();
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }

        try {
            List<TrainSearchResult> results = trainSeatService.findByRouteAndTimeOrderByDuration(
                    request.getDepartureStation(),
                    request.getArrivalStation(),
                    request.getDepartureDate()
            );

            return ResponseEntity.ok(Map.of(
                    "message", "查询成功",
                    "data", results,
                    "sortBy", "departureTime"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "查询失败: " + e.getMessage()));
        }
    }
}
