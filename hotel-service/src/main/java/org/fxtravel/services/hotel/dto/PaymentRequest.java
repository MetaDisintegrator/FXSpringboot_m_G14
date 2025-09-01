package org.fxtravel.services.hotel.dto;

import lombok.Data;

@Data
public class PaymentRequest {
    private String orderNumber;
    private Object data;
}
