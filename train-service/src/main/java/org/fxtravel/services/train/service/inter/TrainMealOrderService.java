package org.fxtravel.services.train.service.inter;

import org.fxtravel.services.train.dto.TrainMealOrderDTO;
import org.fxtravel.services.train.entitiy.TrainMealOrder;

import java.util.List;

public interface TrainMealOrderService {
    TrainMealOrder getOrderById(Integer id);

    List<TrainMealOrder> getOrdersByUser(Integer userId);

    List<TrainMealOrder> getOrdersBySeatOrder(Integer seatOrderId);

    TrainMealOrder createOrder(TrainMealOrderDTO orderDTO);

    boolean existsBySeatOrder(Integer seatOrderId);
}
