package org.fxtravel.services.train.controller;

import org.fxtravel.services.train.dto.PaymentRequest;
import org.fxtravel.services.train.service.inter.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment/train")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/complete")
    public ResponseEntity<Boolean> completePayment(@RequestBody PaymentRequest request) {
        boolean result = paymentService.completePayment(request.getOrderNumber(), request.getData());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/fail")
    public ResponseEntity<Boolean> cancelPayment(@RequestBody PaymentRequest request) {
        boolean result = paymentService.failPayment(request.getOrderNumber(), request.getData());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/finish")
    public ResponseEntity<Boolean> finishPayment(@RequestBody PaymentRequest request) {
        boolean result = paymentService.finishPayment(request.getOrderNumber(), request.getData());
        return ResponseEntity.ok(result);
    }
}