package org.fxtravel.services.payment.dto;

import jakarta.annotation.Nullable;
import org.fxtravel.services.payment.common.*;

import java.time.LocalDateTime;

public class PaymentQueryDTO {
    private String orderNumber;
    private E_PaymentType type;
    @Nullable
    private Integer userId;
    @Nullable
    private E_PaymentStatus status;
    @Nullable
    private Double amountMin;
    @Nullable
    private Double amountMax;
    @Nullable
    private LocalDateTime timeMin;
    @Nullable
    private LocalDateTime timeMax;
}
