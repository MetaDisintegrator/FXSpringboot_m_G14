package org.fxtravel.services.hotel.service.inter;


import org.fxtravel.services.hotel.entitiy.Hotel;
import org.fxtravel.services.hotel.entitiy.Room;
import org.fxtravel.services.hotel.dto.HotelSearchResult;
import org.fxtravel.services.payment.service.inter.GoodService;

import java.util.List;

public interface HotelService extends GoodService {
    Hotel getHotelById(Integer id);
    Room getRoomById(Integer id);
    List<HotelSearchResult> searchHotels(String destination, String pattern);
}