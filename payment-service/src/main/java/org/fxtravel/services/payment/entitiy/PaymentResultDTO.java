package org.fxtravel.services.payment.entitiy;

import lombok.Data;
import org.fxtravel.services.payment.common.E_PaymentStatus;

@Data
public class PaymentResultDTO {
    private String orderNumber;
    private E_PaymentStatus currentStatus;
    private String message;
    private Long remainingTimeSeconds;
}
