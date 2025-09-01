package org.fxtravel.services.train.service.impl;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.transaction.Transactional;
import org.fxtravel.services.payment.common.E_PaymentStatus;
import org.fxtravel.services.payment.common.E_PaymentType;
import org.fxtravel.services.payment.entitiy.payment;
import org.fxtravel.services.train.mapper.TrainMealOrderMapper;
import org.fxtravel.services.train.dto.TrainMealOrderDTO;
import org.fxtravel.services.train.entitiy.TrainMeal;
import org.fxtravel.services.train.entitiy.TrainMealOrder;
import org.fxtravel.services.train.service.inter.TrainMealOrderService;
import org.fxtravel.services.train.service.inter.TrainMealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@DependsOn("paymentServiceImpl")
public class TrainMealOrderServiceImpl implements TrainMealOrderService {

    @Autowired
    private TrainMealOrderMapper trainMealOrderMapper;

    @Autowired
    private TrainMealService trainMealService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private EventCenter eventCenter;

    @PostConstruct
    public void init() {
        // 注册回调，确保在服务启动时就注册
        eventCenter.subscribe(EventType.TM_STATUS_CHANGED, this::handlePaymentStatusChange);
    }

    @PreDestroy
    public void destroy() {
        // 服务关闭时注销回调
        eventCenter.unsubscribe(EventType.TM_STATUS_CHANGED, this::handlePaymentStatusChange);
    }

    // 处理支付状态变更的回调方法
    private void handlePaymentStatusChange(PaymentInfo info) {
        trainMealOrderMapper.updateStatus(info.getOrderId(), info.getNewStatus());
    }

    @Override
    public TrainMealOrder getOrderById(Integer id) {
        return trainMealOrderMapper.selectById(id);
    }

    @Override
    public List<TrainMealOrder> getOrdersByUser(Integer userId) {
        return trainMealOrderMapper.selectByUser(userId);
    }

    @Override
    public List<TrainMealOrder> getOrdersBySeatOrder(Integer seatOrderId) {
        return trainMealOrderMapper.selectBySeatOrder(seatOrderId);
    }

    @Override
    public TrainMealOrder createOrder(TrainMealOrderDTO orderDTO) {
        // 1. 验证餐食信息
        TrainMeal meal = trainMealService.getMealById(orderDTO.getTrainMealId());
        if (meal == null || !meal.getEnabled()) {
            throw new RuntimeException("餐食不存在或已下架");
        }

        // 2. 计算总金额
        Double totalAmount = meal.getPrice() * orderDTO.getQuantity();

        // 3. 创建订单实体
        TrainMealOrder order = new TrainMealOrder();
        order.setUserId(orderDTO.getUserId());
        order.setSeatOrderId(orderDTO.getTicketReservationId());
        order.setTrainMealId(orderDTO.getTrainMealId());
        order.setQuantity(orderDTO.getQuantity());
        order.setTotalAmount(totalAmount);
        order.setStatus(E_PaymentStatus.IDLE);
        order.setCreateTime(LocalDateTime.now());
        trainMealOrderMapper.insert(order);

        // 4. 保存订单
        // 5. 创建支付记录
        payment payment = paymentService.createPayment(
                order.getUserId(),
                E_PaymentType.TRAIN_MEAL,
                order.getTotalAmount(),
                order.getId(),
                order.getQuantity(),
                order.getTrainMealId()
        );
        order.setRelatedPaymentId(payment.getId());
        order.setOrderNumber(payment.getOrderNumber());
        trainMealOrderMapper.updateById(order);

        // 6. 启动异步支付流程
        paymentService.simulatePaymentProcess(payment.getOrderNumber(), 30,
                () -> trainMealService.checkAndGet(meal.getId(), order.getQuantity(), null),
                () -> null);

        return order;
    }

    @Override
    public boolean existsBySeatOrder(Integer seatOrderId) {
        return trainMealOrderMapper.existsBySeatOrderId(seatOrderId);
    }
}
