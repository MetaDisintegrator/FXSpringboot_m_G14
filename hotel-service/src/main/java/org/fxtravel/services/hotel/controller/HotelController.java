package org.fxtravel.services.hotel.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.fxtravel.services.hotel.dto.HotelSearchResult;
import org.fxtravel.services.hotel.dto.SearchHotelRequest;
import org.fxtravel.services.hotel.service.inter.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hotel")
@RequiredArgsConstructor
public class HotelController {
    @Autowired
    private final HotelService hotelService;

    @PostMapping("/room/by-dest")
    public ResponseEntity<?> searchHotel(@Valid @RequestBody SearchHotelRequest request,
                                                        BindingResult bindingResult,
                                                        HttpSession session) {
        User user = (User) session.getAttribute("user");

        ResponseEntity<? extends Map<String, ?>> errors = AuthUtil.check(bindingResult, user);
        if (errors != null) return errors;

        try {
            List<HotelSearchResult> results = hotelService.searchHotels(
                    request.getDestination(), request.getNamePattern());

            return ResponseEntity.ok(Map.of(
                    "message", "查询成功",
                    "data", results,
                    "sortBy", "rating"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "查询失败: " + e.getMessage()));
        }
    }
}
