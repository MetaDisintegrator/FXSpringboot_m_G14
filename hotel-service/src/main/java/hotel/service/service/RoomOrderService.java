package hotel.service.service;

import org.fxtravel.fxspringboot.pojo.dto.hotel.BookHotelRequest;
import org.fxtravel.fxspringboot.pojo.entities.RoomOrder;

import java.util.List;

public interface RoomOrderService {
    List<RoomOrder> getOrdersByUserId(Integer userId);
    RoomOrder getOrderById(Integer orderId);
    RoomOrder createOrder(BookHotelRequest request);
}
