package org.fxtravel.services.hotel.client;

import org.fxtravel.services.payment.common.E_PaymentType;
import org.fxtravel.services.payment.entitiy.PaymentResultDTO;
import org.fxtravel.services.payment.entitiy.payment;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.function.Supplier;

@FeignClient(name = "payment-service")
public interface PaymentClient {

    @PostMapping("/api/payment/createPayment")
    public payment createPayment(Integer userId, E_PaymentType type, Double amount,
                                 Integer relatedId, Integer quantity, Integer goodId);

    @PostMapping("/api/payment/refund")
    public boolean refundPayment(String orderNumber, Object data);

    @PostMapping("/api/payment/simulate")
    public PaymentResultDTO simulatePaymentProcess(String orderNumber, long timeout,
                                                   boolean inventoryResult, Object extraData);

    @PostMapping("/api/payment/status")
    public PaymentResultDTO checkPaymentStatus(Integer paymentId);
}
