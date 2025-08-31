package org.fxtravel.services.hotel.dto;

import lombok.Data;
import org.fxtravel.services.hotel.entitiy.Hotel;
import org.fxtravel.services.hotel.entitiy.Room;

import java.util.List;

@Data
public class HotelSearchResult {
    private Hotel hotel;
    private List<Room> rooms;
}