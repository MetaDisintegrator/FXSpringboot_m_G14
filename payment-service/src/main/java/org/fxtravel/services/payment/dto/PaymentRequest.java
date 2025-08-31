package org.fxtravel.services.payment.dto;

import lombok.Data;

@Data
public class PaymentRequest {
    private String orderNumber;
    private Object data;
}
