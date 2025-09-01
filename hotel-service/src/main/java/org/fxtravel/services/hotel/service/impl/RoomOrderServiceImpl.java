package org.fxtravel.services.hotel.service.impl;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import org.fxtravel.services.hotel.client.EventClient;
import org.fxtravel.services.hotel.client.PaymentClient;
import org.fxtravel.services.payment.common.E_PaymentStatus;
import org.fxtravel.services.payment.common.E_PaymentType;
import org.fxtravel.services.payment.entitiy.payment;
import org.fxtravel.services.payment.event.EventType;
import org.fxtravel.services.payment.event.data.PaymentInfo;
import org.fxtravel.services.hotel.entitiy.*;
import org.fxtravel.services.hotel.dto.*;
import org.fxtravel.services.hotel.mapper.RoomOrderMapper;
import org.fxtravel.services.hotel.service.inter.HotelService;
import org.fxtravel.services.hotel.service.inter.RoomOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class RoomOrderServiceImpl implements RoomOrderService {
    @Autowired
    RoomOrderMapper roomOrderMapper;

    @Autowired
    HotelService hotelService;
    @Autowired
    PaymentClient paymentClient;
    @Autowired
    EventClient eventClient;

    @PostConstruct
    public void init() {
        // 注册回调，确保在服务启动时就注册
        eventClient.subscribe(EventType.HT_STATUS_CHANGED, this::handlePaymentStatusChange);
    }

    @PreDestroy
    public void destroy() {
        // 服务关闭时注销回调
        eventClient.unsubscribe(EventType.HT_STATUS_CHANGED, this::handlePaymentStatusChange);
    }

    // 处理支付状态变更的回调方法
    private void handlePaymentStatusChange(PaymentInfo info) {
        roomOrderMapper.updateStatus(info.getOrderId(), info.getNewStatus());
    }

    @Override
    public List<RoomOrder> getOrdersByUserId(Integer userId) {
        return roomOrderMapper.findByUserID(userId);
    }

    @Override
    public RoomOrder getOrderById(Integer orderId) {
        return roomOrderMapper.selectById(orderId);
    }

    @Override
    public RoomOrder createOrder(BookHotelRequest request) {
        // 1. 基础验证
        Hotel hotel = hotelService.getHotelById(request.getHotelId());
        Room room = hotelService.getRoomById(request.getRoomId());
        if (hotel == null || room == null) {
            throw new IllegalArgumentException("酒店或房型不存在");
        }

        // 2. 计算入住天数和总价
        long nights = ChronoUnit.DAYS.between(
                request.getCheckInDate(),
                request.getCheckOutDate()
        );
        Double totalAmount = room.getPricePerNight() * nights;

        // 3. 创建订单
        RoomOrder order = new RoomOrder();
        order.setUserId(request.getUserId());
        order.setHotelId(request.getHotelId());
        order.setRoomId(request.getRoomId());
        order.setCheckInDate(request.getCheckInDate());
        order.setCheckOutDate(request.getCheckOutDate());
        order.setTotalAmount(totalAmount);  // 使用计算后的总价
        order.setCreateTime(LocalDateTime.now());
        order.setStatus(E_PaymentStatus.IDLE);
        roomOrderMapper.insert(order);

        // 4. 创建支付记录
        payment payment = paymentClient.createPayment(
                order.getUserId(),
                E_PaymentType.HOTEL,
                order.getTotalAmount(),  // 传递计算后的总价
                order.getId(),
                1,
                room.getId()
        );

        // 5. 更新订单支付信息
        order.setRelatedPaymentId(payment.getId());
        order.setOrderNumber(payment.getOrderNumber());
        roomOrderMapper.updateById(order);

        // 6. 模拟支付流程（保持原有逻辑）
        paymentClient.simulatePaymentProcess(
                payment.getOrderNumber(),
                30,
                hotelService.checkAndGet(room.getId(), 1, null),
                null
        );

        return order;
    }
}
