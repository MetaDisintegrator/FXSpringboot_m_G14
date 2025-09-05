package org.fxtravel.services.train.degrade;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class TrainGlobalFallback {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAny(Exception ex) {
        return ResponseEntity.ok(Map.of(
                "degraded", true,
                "trains", List.of(),
                "message", "train-service 内部异常，已返回降级数据"
        ));
    }
}
