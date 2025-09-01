package org.fxtravel.services.hotel.entitiy;

import lombok.Data;
import org.fxtravel.services.hotel.common.E_PaymentStatus;


@Data
public class PaymentResultDTO {
    private String orderNumber;
    private E_PaymentStatus currentStatus;
    private String message;
    private Long remainingTimeSeconds;
}
