package org.fxtravel.services.train.dto;
import lombok.Data;

@Data
public class PaymentRequest {
    private String orderNumber;
    private Object data;
}
