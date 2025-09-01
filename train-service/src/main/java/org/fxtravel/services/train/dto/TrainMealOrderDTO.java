package org.fxtravel.services.train.dto;

import jakarta.annotation.Nullable;
import lombok.Data;

// 列车餐订单DTO
@Data
public class TrainMealOrderDTO {
    @Nullable
    private Integer id;
    private Integer userId;
    private Integer ticketReservationId;
    private Integer trainMealId;
    private Integer quantity;
}
