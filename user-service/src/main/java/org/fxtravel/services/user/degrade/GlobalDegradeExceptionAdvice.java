package org.fxtravel.services.user.degrade;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalDegradeExceptionAdvice {

    @ExceptionHandler(ReadonlyGuardAspect.ReadonlyException.class)
    public ResponseEntity<Map<String,Object>> handleReadonly(ReadonlyGuardAspect.ReadonlyException ex){
        Map<String,Object> body = new HashMap<>();
        body.put("code", 423);
        body.put("msg", "Read-only mode");
        body.put("reason", ex.getMessage());
        return ResponseEntity.status(423).body(body); // 423 LOCKED
    }

    @ExceptionHandler(ReadonlyGuardAspect.ServiceDisabledException.class)
    public ResponseEntity<Map<String,Object>> handleDisabled(ReadonlyGuardAspect.ServiceDisabledException ex){
        Map<String,Object> body = new HashMap<>();
        body.put("code", 503);
        body.put("msg", "Service temporarily unavailable");
        body.put("reason", ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body);
    }
}
