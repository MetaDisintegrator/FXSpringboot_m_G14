package org.fxtravel.services.hotel.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.fxtravel.services.hotel.client.PaymentClient;
import org.fxtravel.services.hotel.client.UserClient;
import org.fxtravel.services.hotel.dto.BookHotelRequest;
import org.fxtravel.services.hotel.dto.RoomOrderResponse;
import org.fxtravel.services.hotel.entitiy.RoomOrder;
import org.fxtravel.services.hotel.mapper.HotelMapper;
import org.fxtravel.services.hotel.mapper.RoomMapper;
import org.fxtravel.services.hotel.service.inter.RoomOrderService;
import org.fxtravel.services.payment.dto.PaymentRequest;
import org.fxtravel.services.payment.entitiy.PaymentResultDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hotel")
public class RoomOrderController {
    @Autowired
    private RoomOrderService roomOrderService;
    @Autowired
    private PaymentClient paymentClient;
    @Autowired
    private HotelMapper hotelMapper;
    @Autowired
    private RoomMapper roomMapper;
    @Autowired
    private UserClient userClient;

    @PostMapping("/room/get")
    public ResponseEntity<?> getRoom(@Valid @RequestBody BookHotelRequest request,
                                       BindingResult bindingResult,
                                       HttpSession session) {


        ResponseEntity<? extends Map<String, ?>> errors = userClient.check(bindingResult, session);
        if (errors != null) return errors;

        try {
            RoomOrder order = roomOrderService.createOrder(request);

            return ResponseEntity.ok(Map.of(
                    "message", "预订酒店成功",
                    "id", order.getId(),
                    "number", order.getOrderNumber(),
                    "roomId", order.getRoomId(),
                    "totalAmount", order.getTotalAmount()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "预订酒店失败: " + e.getMessage()));
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderPaymentStatus(@PathVariable Integer orderId) {
        RoomOrder order = roomOrderService.getOrderById(orderId);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }

        PaymentResultDTO result = order.getRelatedPaymentId() != null ?
                paymentClient.checkPaymentStatus(order.getRelatedPaymentId()) : null;

        return ResponseEntity.ok(result);
    }

    @GetMapping("/orders/{userId}")
    public ResponseEntity<List<RoomOrderResponse>> getOrderByUserId(@PathVariable Integer userId) {
        List<RoomOrder> orders = roomOrderService.getOrdersByUserId(userId);
        List<RoomOrderResponse> roomOrderResponses = new ArrayList<>();
        for (RoomOrder order : orders) {
            RoomOrderResponse roomOrderResponse = new RoomOrderResponse(
                    order.getId(),
                    order.getOrderNumber(),
                    order.getUserId(),
                    hotelMapper.selectById(order.getHotelId()).getName(),
                    roomMapper.selectById(order.getRoomId()).getName(),
                    order.getCheckInDate(),
                    order.getCheckOutDate(),
                    order.getStatus(),
                    order.getTotalAmount(),
                    order.getCreateTime()
            );
            roomOrderResponses.add(roomOrderResponse);
        }
        return ResponseEntity.ok(roomOrderResponses);
    }

    @PostMapping("/refund")
    public ResponseEntity<?> refund(@Valid @RequestBody PaymentRequest request,
                                    BindingResult bindingResult, HttpSession session) {

        ResponseEntity<? extends Map<String, ?>> errors = userClient.check(bindingResult, session);
        if (errors != null) return errors;

        return ResponseEntity.ok(paymentClient.refundPayment(request.getOrderNumber(), request.getData()));
    }
}
