package org.fxtravel.services.train.controller;

import jakarta.validation.Valid;
import org.fxtravel.services.train.dto.PaymentRequest;
import org.fxtravel.services.train.entitiy.PaymentResultDTO;
import org.fxtravel.services.train.entitiy.Train;
import org.fxtravel.services.train.entitiy.TrainSeat;
import org.fxtravel.services.train.entitiy.TrainSeatOrder;
import org.fxtravel.services.train.mapper.TrainMealOrderMapper;
import org.fxtravel.services.train.mapper.TrainMapper;
import org.fxtravel.services.train.mapper.TrainSeatMapper;
import org.fxtravel.services.train.dto.GetTicketRequest;
import org.fxtravel.services.train.dto.TrainSeatOrderDTO;
import org.fxtravel.services.train.service.inter.PaymentService;
import org.fxtravel.services.train.service.inter.TrainSeatOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/train")
public class TrainSeatOrderController {
    @Autowired
    private TrainSeatOrderService trainSeatOrderService;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private TrainSeatMapper trainSeatMapper;
    @Autowired
    private TrainMapper trainMapper;
    @Autowired
    private TrainMealOrderMapper trainMealOrderMapper;

    // 根据座次生成车票接口
    @PostMapping("/ticket/get")
    public ResponseEntity<?> getTicket(@RequestHeader("X-User-Id") String userId,@Valid @RequestBody GetTicketRequest request,
                                       BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors()
                    .stream()
                    .map(e -> e.getField() + ": " + e.getDefaultMessage())
                    .toList();
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }

        try {
            TrainSeatOrder order = trainSeatOrderService.createOrder(request);

            return ResponseEntity.ok(Map.of(
                    "message", "车票生成成功",
                    "id", order.getId(),
                    "number", order.getOrderNumber(),
                    "seat", order.getSeatNumber()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "生成车票失败: " + e.getMessage()));
        }
    }

    @GetMapping("/ticket/{orderId}")
    public ResponseEntity<PaymentResultDTO> getOrderPaymentStatus(@PathVariable Integer orderId) {
        TrainSeatOrder order = trainSeatOrderService.getOrderById(orderId);
        if (order == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "id not found");
        }

        PaymentResultDTO result = order.getRelatedPaymentId() != null ?
                paymentService.checkPaymentStatus(order.getRelatedPaymentId()) : null;

        return ResponseEntity.ok(result);
    }

    @GetMapping("/order/get/{userId}")
    public ResponseEntity<List<TrainSeatOrderDTO>> getTicket(@PathVariable Integer userId) {
        // 1. 获取用户订单
        List<TrainSeatOrder> orders = trainSeatOrderService.getOrdersByUserId(userId);
        if (orders.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        // 3. 使用Stream转换DTO
        List<TrainSeatOrderDTO> dtos = orders.stream()
                .map(order -> {
                    Train train = trainMapper.selectById(order.getTrainId());
                    TrainSeat seat = trainSeatMapper.selectById(order.getTrainSeatId());

                    return new TrainSeatOrderDTO(
                            order.getId(),
                            order.getOrderNumber(),
                            order.getUserId(),
                            train,
                            seat,  // 使用预加载的座位数据
                            order.getSeatNumber(),
                            order.getRelatedPaymentId(),
                            order.getTotalAmount(),
                            order.getStatus(),
                            order.getCreateTime()
                    );
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
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

        TrainSeatOrder order = trainSeatOrderService.getOrderByNumber(request.getOrderNumber());
        if (order == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "error", "订单无效"));
        }
        if (trainMealOrderMapper.existsBySeatOrderId(order.getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "error", "有未取消的餐品"));
        }

        return ResponseEntity.ok(paymentService.refundPayment(request.getOrderNumber(), request.getData()));
    }
}
