package org.fxtravel.services.hotel.service.inter;

import org.fxtravel.services.hotel.dto.BookHotelRequest;
import org.fxtravel.services.hotel.entitiy.RoomOrder;

import java.util.List;

public interface RoomOrderService {
    List<RoomOrder> getOrdersByUserId(Integer userId);
    RoomOrder getOrderById(Integer orderId);
    RoomOrder createOrder(BookHotelRequest request);
}
