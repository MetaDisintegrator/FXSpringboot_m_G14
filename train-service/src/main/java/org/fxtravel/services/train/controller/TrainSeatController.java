package org.fxtravel.services.train.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.fxtravel.services.train.dto.SearchTrainRequest;
import org.fxtravel.services.train.dto.TrainSearchResult;
import org.fxtravel.services.train.entitiy.Train;
import org.fxtravel.services.train.service.inter.TrainSeatService;
import org.fxtravel.services.train.degrade.DegradeSwitch;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/train")
@RequiredArgsConstructor
public class TrainSeatController {

    private final TrainSeatService trainSeatService;
    private final DegradeSwitch degrade;

    @GetMapping("/by-id/{id}")
    public Train getTrain(@RequestHeader("X-User-Id") String userId, @PathVariable Integer id) {
        // 简单查询，出异常会被全局兜底处理
        return trainSeatService.getTrainById(id);
    }

    @PostMapping("/seat/by-departure-time")
    public ResponseEntity<?> searchTrainByDepartureTime(@RequestHeader("X-User-Id") String userId,
                                                        @Valid @RequestBody SearchTrainRequest request,
                                                        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors()
                    .stream().map(e -> e.getField() + ": " + e.getDefaultMessage()).toList();
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }

        // 全量降级开关
        if (degrade.isOn()) {
            return ResponseEntity.ok(Map.of(
                    "degraded", true,
                    "trains", List.of(),
                    "message", "train-service DEGRADE_ALL"
            ));
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
            // 异常时降级：返回 200 + 简化数据
            return ResponseEntity.ok(Map.of(
                    "degraded", true,
                    "trains", List.of(),
                    "message", "train-service error:return downgrade"
            ));
        }
    }

    @PostMapping("/seat/by-duration-time")
    public ResponseEntity<?> searchTrainByDuration(@RequestHeader("X-User-Id") String userId,
                                                   @Valid @RequestBody SearchTrainRequest request,
                                                   BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors()
                    .stream().map(e -> e.getField() + ": " + e.getDefaultMessage()).toList();
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }

        if (degrade.isOn()) {
            return ResponseEntity.ok(Map.of(
                    "degraded", true,
                    "trains", List.of(),
                    "message", "train-service DEGRADE_ALL"
            ));
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
            return ResponseEntity.ok(Map.of(
                    "degraded", true,
                    "trains", List.of(),
                    "message", "train-service error:return downgrade"
            ));
        }
    }
}
