package org.fxtravel.services.payment.dto;

import jakarta.annotation.Nullable;
import lombok.Data;
import org.fxtravel.services.payment.common.E_PaymentStatus;
import org.fxtravel.services.payment.common.E_PaymentType;


import java.time.LocalDateTime;

@Data
public class PaymentDTO {
    @Nullable
    private Integer id;
    private String orderNumber;
    private Integer userId;
    private E_PaymentType type;
    private E_PaymentStatus status;
    private Double amount;
    @Nullable
    private LocalDateTime paymentTime;
    @Nullable
    private Integer relatedId;
}
