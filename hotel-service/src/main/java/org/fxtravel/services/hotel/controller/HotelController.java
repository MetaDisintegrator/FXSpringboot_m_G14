package org.fxtravel.services.hotel.controller;

import jakarta.validation.Valid;

import org.fxtravel.services.hotel.dto.HotelSearchResult;
import org.fxtravel.services.hotel.dto.SearchHotelRequest;
import org.fxtravel.services.hotel.service.inter.HotelService;
import org.fxtravel.services.hotel.degrade.DegradeSwitch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hotel")
public class HotelController {

    private final HotelService hotelService;
    private final DegradeSwitch degrade;

    // —— Spring 运行时使用：标注 @Autowired 的双参构造器 ——
    @Autowired
    public HotelController(HotelService hotelService, DegradeSwitch degrade) {
        this.hotelService = hotelService;
        this.degrade = degrade;
    }

    // —— 兼容旧单测：只有 HotelService 的便捷构造器（不影响运行时） ——
    public HotelController(HotelService hotelService) {
        this(hotelService, new DegradeSwitch(false));
    }

    @PostMapping("/room/by-dest")
    public ResponseEntity<?> searchHotel(@RequestHeader("X-User-Id") String userId,
                                         @Valid @RequestBody SearchHotelRequest request,
                                         BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }

        // === 全量降级开关（逻辑不变） ===
        if (degrade.isOn()) {
            return ResponseEntity.ok(Map.of(
                    "degraded", true,
                    "hotels", List.of(),
                    "message", "hotel-service DEGRADE_ALL"
            ));
        }

        try {
            List<HotelSearchResult> results =
                    hotelService.searchHotels(request.getDestination(), request.getNamePattern());

            return ResponseEntity.ok(Map.of(
                    "message", "查询成功",
                    "data", results,
                    "sortBy", "rating"
            ));
        } catch (Exception e) {
            // 保持原有：异常时返回 500
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "查询失败: " + e.getMessage()));
        }
    }
}
