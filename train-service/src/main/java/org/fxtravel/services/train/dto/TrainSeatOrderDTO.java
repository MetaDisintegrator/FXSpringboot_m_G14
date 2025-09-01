package org.fxtravel.services.train.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fxtravel.services.payment.common.E_PaymentStatus;
import org.fxtravel.services.train.entitiy.Train;
import org.fxtravel.services.train.entitiy.TrainSeat;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainSeatOrderDTO {
    private Integer id;
    private String orderNumber;
    private Integer userId;
    private Train train;
    private TrainSeat trainSeat;
    private String seatNumber;
    private Integer relatedPaymentId;
    private Double totalAmount;
    private E_PaymentStatus status;
    private LocalDateTime createTime;

}
