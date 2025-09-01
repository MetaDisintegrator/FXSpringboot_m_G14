package org.fxtravel.services.train.service.inter;

import org.fxtravel.services.train.dto.GetTicketRequest;
import org.fxtravel.services.train.entitiy.TrainSeatOrder;

import java.util.List;

public interface TrainSeatOrderService {
    boolean existsByTrainAndUser(Integer trainId, Integer userId);
    List<TrainSeatOrder> getOrdersByUserId(Integer userId);
    TrainSeatOrder getOrderById(Integer orderId);
    TrainSeatOrder getOrderByNumber(String orderNumber);
    TrainSeatOrder createOrder(GetTicketRequest request);
}
