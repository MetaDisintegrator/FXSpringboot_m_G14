package org.fxtravel.services.hotel.degrade;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class HotelGlobalFallback {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAny(Exception ex) {
        return ResponseEntity.ok(Map.of(
                "degraded", true,
                "hotels", List.of(),
                "message", "hotel-service 内部异常，已返回降级数据"
        ));
    }
}
