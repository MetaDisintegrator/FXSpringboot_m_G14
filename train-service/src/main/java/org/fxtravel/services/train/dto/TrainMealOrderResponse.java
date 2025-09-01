package org.fxtravel.services.train.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fxtravel.services.train.common.E_PaymentStatus;


import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainMealOrderResponse {
    private int id;
    private String OrderNumber;
    private int quantity;
    private double totalAmount;
    private String trainMealName;
    private String reservationSeatOrderNumber;
    private E_PaymentStatus status;
    private LocalDateTime createTime;
}
