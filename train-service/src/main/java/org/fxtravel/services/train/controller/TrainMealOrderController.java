package org.fxtravel.services.train.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.fxtravel.services.train.common.E_PaymentStatus;
import org.fxtravel.services.train.dto.PaymentRequest;
import org.fxtravel.services.train.entitiy.PaymentResultDTO;
import org.fxtravel.services.train.mapper.TrainMealMapper;
import org.fxtravel.services.train.mapper.TrainSeatOrderMapper;
import org.fxtravel.services.train.dto.TrainMealOrderDTO;
import org.fxtravel.services.train.dto.TrainMealOrderResponse;
import org.fxtravel.services.train.entitiy.TrainSeatOrder;
import org.fxtravel.services.train.entitiy.TrainMeal;
import org.fxtravel.services.train.entitiy.TrainMealOrder;
import org.fxtravel.services.train.service.inter.PaymentService;
import org.fxtravel.services.train.service.inter.TrainMealOrderService;
import org.fxtravel.services.train.service.inter.TrainMealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/train/meal")
public class TrainMealOrderController {

    @Autowired
    private TrainMealService trainMealService;

    @Autowired
    private TrainMealOrderService trainMealOrderService;

    @Autowired
    private TrainSeatOrderMapper trainSeatOrderMapper;

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private TrainMealMapper trainMealMapper;

    @GetMapping("/orders/{userId}")
    public ResponseEntity<?> getOrdersByUser(@RequestHeader("X-User-Id") String userID,@PathVariable Integer userId) {


        List<TrainMealOrder> orders = trainMealOrderService.getOrdersByUser(userId);
        List<TrainMealOrderResponse> orderResponses = new ArrayList<>();
        for (TrainMealOrder order : orders) {
            TrainMealOrderResponse orderResponse = new TrainMealOrderResponse(
                    order.getId(),
                    order.getOrderNumber(),
                    order.getQuantity(),
                    order.getTotalAmount(),
                    trainMealMapper.selectById(order.getTrainMealId()).getName(),
                    trainSeatOrderMapper.selectById(order.getSeatOrderId()).getOrderNumber(),
                    order.getStatus(),
                    order.getCreateTime()
            );
            orderResponses.add(orderResponse);
        }
        return ResponseEntity.ok(Map.of(
                "message", "查询成功",
                "data", orderResponses
        ));
    }

    @GetMapping("/orders/by-ticket/{seatOrderId}")
    public ResponseEntity<?> getOrdersBySeatOrder(@RequestHeader("X-User-Id") String userId, @PathVariable Integer seatOrderId) {

        List<TrainMealOrder> orders = trainMealOrderService.getOrdersBySeatOrder(seatOrderId);
        return ResponseEntity.ok(Map.of(
                "message", "查询成功",
                "data", orders
        ));
    }

    @PostMapping("/get")
    public ResponseEntity<?> createOrder(@RequestHeader("X-User-Id") String userId,@Valid @RequestBody TrainMealOrderDTO orderDTO
            , BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors()
                    .stream()
                    .map(e -> e.getField() + ": " + e.getDefaultMessage())
                    .toList();
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }

        TrainSeatOrder seat = trainSeatOrderMapper.selectById(orderDTO.getTicketReservationId());
        if (seat == null || seat.getStatus() != E_PaymentStatus.COMPLETED) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "error", "未在对应列车上购票"));
        }
        Integer intUserId = Integer.valueOf(userId);
        // 必须有购票
        TrainMeal meal = trainMealService.getMealById(orderDTO.getTrainMealId());
        if (!trainSeatOrderMapper.existsByTrainAndUser(meal.getTrainId(), intUserId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "error", "未在对应列车上购票"));
        }

        TrainMealOrder order = trainMealOrderService.createOrder(orderDTO);
        return ResponseEntity.ok(Map.of(
                "message", "列车餐预订成功",
                "id", order.getId(),
                "number", order.getOrderNumber(),
                "meal", order.getTrainMealId(),
                "seatOrder", order.getSeatOrderId()
        ));
    }

    @GetMapping("/status/{orderId}")
    public ResponseEntity<?> getOrderPaymentStatus(@PathVariable Integer orderId) {
        TrainMealOrder order = trainMealOrderService.getOrderById(orderId);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }

        PaymentResultDTO result = order.getRelatedPaymentId() != null ?
                paymentService.checkPaymentStatus(order.getRelatedPaymentId()) : null;
        return ResponseEntity.ok(result);
    }

    @PostMapping("/refund")
    public ResponseEntity<?> refund(@RequestHeader("X-User-Id") String userId,@Valid @RequestBody PaymentRequest request,
                                    BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors()
                    .stream()
                    .map(e -> e.getField() + ": " + e.getDefaultMessage())
                    .toList();
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }
        return ResponseEntity.ok(paymentService.refundPayment(request.getOrderNumber(), request.getData()));
    }
}
