package org.fxtravel.services.hotel.service.impl;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import org.fxtravel.services.hotel.client.EventClient;
import org.fxtravel.services.payment.dto.EventSubscriptionRequest;
import org.fxtravel.services.payment.event.EventType;
import org.fxtravel.services.payment.event.data.PaymentInfo;
import org.fxtravel.services.hotel.dto.HotelSearchResult;
import org.fxtravel.services.hotel.entitiy.Hotel;
import org.fxtravel.services.hotel.entitiy.Room;
import org.fxtravel.services.hotel.mapper.HotelMapper;
import org.fxtravel.services.hotel.mapper.RoomMapper;
import org.fxtravel.services.hotel.service.inter.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;



@Service
public class HotelServiceImpl implements HotelService {
    @Autowired
    private HotelMapper hotelMapper;
    @Autowired
    private RoomMapper roomMapper;
    @Autowired
    private EventClient eventClient;
    // 不需要 @Value 注入，直接使用服务名称
    private static final String HOTEL_SERVICE_NAME = "hotel-service";

    // 在订阅事件时使用服务名称构建回调路径
    @PostConstruct
    public void init() {
        // 使用服务名称而不是完整URL
        String callbackPath = "/api/events/payment-status-change";
        EventSubscriptionRequest request = new EventSubscriptionRequest();
        request.setEventType(EventType.HT_STATUS_CHANGED);
        request.setCallbackUrl("http://" + HOTEL_SERVICE_NAME + callbackPath);
        eventClient.subscribe(request);
    }

    @PreDestroy
    public void destroy() {
        // 注销回调URL
        String callbackPath = "/api/events/payment-status-change";
        EventSubscriptionRequest request = new EventSubscriptionRequest();
        request.setEventType(EventType.HT_STATUS_CHANGED);
        request.setCallbackUrl("http://" + HOTEL_SERVICE_NAME + callbackPath);
        eventClient.unsubscribe(request);
    }

    // 处理支付状态变更的回调方法
    public void handlePaymentStatusChange(PaymentInfo info) {
        switch (info.getNewStatus()){
            case FAILED:
                // 支付成功，无需
            case REFUNDED:
                putBack(info.getGoodId(), info.getQuantity(), null);
                break;
        }
    }

    @Override
    public Hotel getHotelById(Integer id) {
        return hotelMapper.selectById(id);
    }

    @Override
    public Room getRoomById(Integer id) {
        return roomMapper.selectById(id);
    }

    @Override
    public List<HotelSearchResult> searchHotels(String destination, String pattern) {
        if (pattern == null || pattern.isEmpty()) {
            return getResults(hotelMapper.findByDest(destination));
        }

        return getResults(hotelMapper.findByDestAndName(destination, pattern));
    }

    private List<HotelSearchResult> getResults(List<Hotel> hotels) {
        if (hotels.isEmpty()) {
            return null;
        }

        // 转换为搜索结果
        List<HotelSearchResult> results = new ArrayList<>();

        for (Hotel hotel : hotels) {
            List<Room> roomList = roomMapper.findByHotel(hotel.getId());
            if (roomList.isEmpty()) {
                continue;
            }
            // 创建搜索结果对象
            HotelSearchResult result = new HotelSearchResult();
            result.setHotel(hotel);
            result.setRooms(roomList);
            results.add(result);
        }

        return results;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean checkAndGet(int id, int count, Object data) {
        // 1. 检查库存是否充足
        Room obj = roomMapper.selectById(id);
        if (obj == null || obj.getRemain() < count) {
            return false;
        }

        // 2. 扣减库存
        int updated = roomMapper.deduct(id, count);

        return updated > 0;
    }

    @Override
    public void putBack(int id, int count, Object data) {
        roomMapper.add(id, count);
    }
}