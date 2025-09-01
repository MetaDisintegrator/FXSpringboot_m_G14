package org.fxtravel.services.hotel.client;

import jakarta.servlet.http.HttpSession;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;


import java.util.Map;

//@FeignClient(name = "user-service")
//public interface UserClient {
//
//    @GetMapping("/api/auth/check")
//    public ResponseEntity<? extends Map<String, ?>> check(BindingResult bindingResult, HttpSession session);
//}