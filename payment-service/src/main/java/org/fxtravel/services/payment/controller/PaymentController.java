package org.fxtravel.services.payment.controller;


import org.fxtravel.services.payment.common.E_PaymentType;
import org.fxtravel.services.payment.dto.PaymentRequest;
import org.fxtravel.services.payment.dto.SimulatePaymentRequest;
import org.fxtravel.services.payment.entitiy.PaymentResultDTO;
import org.fxtravel.services.payment.entitiy.payment;
import org.fxtravel.services.payment.service.inter.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.function.Supplier;

@RestController
@RequestMapping("/api/payment")
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

    @PostMapping("/createPayment")
    public payment createPayment(@RequestParam Integer userId,
                                 @RequestParam E_PaymentType type,
                                 @RequestParam Double amount,
                                 @RequestParam Integer relatedId,
                                 @RequestParam Integer quantity,
                                 @RequestParam Integer goodId) {
        return paymentService.createPayment(userId, type, amount, relatedId, quantity, goodId);
    }

    @PostMapping("/refund")
    public boolean refundPayment(@RequestParam String orderNumber,@RequestBody Object data){
        return paymentService.refundPayment(orderNumber, data);
    }

    @PostMapping("/simulate")
    public PaymentResultDTO simulatePaymentProcess(@RequestParam String orderNumber,
                                                   @RequestParam long timeout,
                                                   @RequestParam boolean inventoryResult,
                                                   @RequestParam Object extraData) {
        return paymentService.simulatePaymentProcess(orderNumber ,timeout, ()-> inventoryResult, ()-> extraData);
    }

    @PostMapping("/status")
    PaymentResultDTO checkPaymentStatus(@RequestParam Integer paymentId) {
        return paymentService.checkPaymentStatus(paymentId);
    }

}