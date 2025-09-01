package org.fxtravel.services.train.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GetTicketRequest {
    private Integer userId;
    private Integer seatId;
}