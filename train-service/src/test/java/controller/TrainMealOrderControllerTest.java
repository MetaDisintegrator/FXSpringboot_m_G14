package controller;

import org.fxtravel.services.train.common.E_PaymentStatus;
import org.fxtravel.services.train.controller.TrainMealOrderController;
import org.fxtravel.services.train.dto.PaymentRequest;
import org.fxtravel.services.train.dto.TrainMealOrderDTO;
import org.fxtravel.services.train.entitiy.PaymentResultDTO;
import org.fxtravel.services.train.entitiy.TrainMeal;
import org.fxtravel.services.train.entitiy.TrainMealOrder;
import org.fxtravel.services.train.entitiy.TrainSeatOrder;
import org.fxtravel.services.train.mapper.TrainMealMapper;
import org.fxtravel.services.train.mapper.TrainSeatOrderMapper;
import org.fxtravel.services.train.service.inter.PaymentService;
import org.fxtravel.services.train.service.inter.TrainMealOrderService;
import org.fxtravel.services.train.service.inter.TrainMealService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainMealOrderControllerTest {

    @Mock
    private TrainMealService trainMealService;

    @Mock
    private TrainMealOrderService trainMealOrderService;

    @Mock
    private TrainSeatOrderMapper trainSeatOrderMapper;

    @Mock
    private PaymentService paymentService;

    @Mock
    private TrainMealMapper trainMealMapper;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private TrainMealOrderController trainMealOrderController;

    private TrainMealOrderDTO orderDTO;
    private TrainMealOrder mockOrder;
    private TrainSeatOrder mockSeatOrder;
    private TrainMeal mockMeal;

    @BeforeEach
    void setUp() {
        orderDTO = new TrainMealOrderDTO();
        orderDTO.setTrainMealId(1);
        orderDTO.setTicketReservationId(100);
        orderDTO.setQuantity(2);

        mockOrder = new TrainMealOrder();
        mockOrder.setId(1);
        mockOrder.setOrderNumber("MEALORDER001");
        mockOrder.setQuantity(2);
        mockOrder.setTotalAmount(50.0);
        mockOrder.setTrainMealId(1);
        mockOrder.setSeatOrderId(100);
        mockOrder.setStatus(E_PaymentStatus.PENDING);

        mockSeatOrder = new TrainSeatOrder();
        mockSeatOrder.setId(100);
        mockSeatOrder.setStatus(E_PaymentStatus.COMPLETED);

        mockMeal = new TrainMeal();
        mockMeal.setId(1);
        mockMeal.setTrainId(500);
        mockMeal.setName("Test Meal");
    }

    @Test
    void getOrdersByUser_Success() {
        // Arrange
        when(trainMealOrderService.getOrdersByUser(anyInt())).thenReturn(Arrays.asList(mockOrder));
        when(trainMealMapper.selectById(anyInt())).thenReturn(mockMeal);
        when(trainSeatOrderMapper.selectById(anyInt())).thenReturn(mockSeatOrder);

        // Act
        ResponseEntity<?> response = trainMealOrderController.getOrdersByUser("user123", 1);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());

        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals("查询成功", responseBody.get("message"));
        assertNotNull(responseBody.get("data"));

        verify(trainMealOrderService, times(1)).getOrdersByUser(1);
    }

    @Test
    void getOrdersByUser_EmptyResult() {
        // Arrange
        when(trainMealOrderService.getOrdersByUser(anyInt())).thenReturn(List.of());

        // Act
        ResponseEntity<?> response = trainMealOrderController.getOrdersByUser("user123", 1);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());

        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals("查询成功", responseBody.get("message"));
        assertTrue(((List<?>) responseBody.get("data")).isEmpty());

        verify(trainMealOrderService, times(1)).getOrdersByUser(1);
    }

    @Test
    void getOrdersBySeatOrder_Success() {
        // Arrange
        when(trainMealOrderService.getOrdersBySeatOrder(anyInt())).thenReturn(Arrays.asList(mockOrder));

        // Act
        ResponseEntity<?> response = trainMealOrderController.getOrdersBySeatOrder("user123", 100);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());

        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals("查询成功", responseBody.get("message"));
        assertNotNull(responseBody.get("data"));

        verify(trainMealOrderService, times(1)).getOrdersBySeatOrder(100);
    }

    @Test
    void createOrder_ValidationErrors() {
        // Arrange
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList(
                new FieldError("TrainMealOrderDTO", "trainMealId", "不能为空")
        ));

        // Act
        ResponseEntity<?> response = trainMealOrderController.createOrder("user123", orderDTO, bindingResult);

        // Assert
        assertEquals(400, response.getStatusCodeValue());
        assertNotNull(response.getBody());

        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody.get("errors"));

        verify(trainMealOrderService, never()).createOrder(any());
    }

    @Test
    void createOrder_NoSeatOrder() {
        // Arrange
        when(bindingResult.hasErrors()).thenReturn(false);
        when(trainSeatOrderMapper.selectById(anyInt())).thenReturn(null);

        // Act
        ResponseEntity<?> response = trainMealOrderController.createOrder("user123", orderDTO, bindingResult);

        // Assert
        assertEquals(400, response.getStatusCodeValue());
        assertNotNull(response.getBody());

        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals("未在对应列车上购票", responseBody.get("error"));

        verify(trainMealOrderService, never()).createOrder(any());
    }

    @Test
    void createOrder_SeatOrderNotCompleted() {
        // Arrange
        when(bindingResult.hasErrors()).thenReturn(false);
        mockSeatOrder.setStatus(E_PaymentStatus.PENDING);
        when(trainSeatOrderMapper.selectById(anyInt())).thenReturn(mockSeatOrder);

        // Act
        ResponseEntity<?> response = trainMealOrderController.createOrder("user123", orderDTO, bindingResult);

        // Assert
        assertEquals(400, response.getStatusCodeValue());
        assertNotNull(response.getBody());

        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals("未在对应列车上购票", responseBody.get("error"));

        verify(trainMealOrderService, never()).createOrder(any());
    }


    @Test
    void createOrder_Success() {
        // Arrange
        when(bindingResult.hasErrors()).thenReturn(false);
        when(trainSeatOrderMapper.selectById(anyInt())).thenReturn(mockSeatOrder);
        when(trainMealService.getMealById(anyInt())).thenReturn(mockMeal);
        when(trainSeatOrderMapper.existsByTrainAndUser(anyInt(), anyInt())).thenReturn(true);
        when(trainMealOrderService.createOrder(any())).thenReturn(mockOrder);

        // Act
        ResponseEntity<?> response = trainMealOrderController.createOrder("123", orderDTO, bindingResult);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());

        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals("列车餐预订成功", responseBody.get("message"));
        assertEquals(1, responseBody.get("id"));
        assertEquals("MEALORDER001", responseBody.get("number"));

        verify(trainMealOrderService, times(1)).createOrder(orderDTO);
    }

    @Test
    void getOrderPaymentStatus_OrderNotFound() {
        // Arrange
        when(trainMealOrderService.getOrderById(anyInt())).thenReturn(null);

        // Act
        ResponseEntity<?> response = trainMealOrderController.getOrderPaymentStatus("user123", 1);

        // Assert
        assertEquals(404, response.getStatusCodeValue());
        verify(trainMealOrderService, times(1)).getOrderById(1);
    }

    @Test
    void getOrderPaymentStatus_NoPayment() {
        // Arrange
        mockOrder.setRelatedPaymentId(null);
        when(trainMealOrderService.getOrderById(anyInt())).thenReturn(mockOrder);

        // Act
        ResponseEntity<?> response = trainMealOrderController.getOrderPaymentStatus("user123", 1);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNull(response.getBody());
        verify(trainMealOrderService, times(1)).getOrderById(1);
    }

    @Test
    void refund_ValidationErrors() {
        // Arrange
        PaymentRequest request = new PaymentRequest();
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList(
                new FieldError("PaymentRequest", "orderNumber", "不能为空")
        ));

        // Act
        ResponseEntity<?> response = trainMealOrderController.refund("user123", request, bindingResult);

        // Assert
        assertEquals(400, response.getStatusCodeValue());
        assertNotNull(response.getBody());

        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody.get("errors"));

        verify(paymentService, never()).refundPayment(anyString(), anyString());
    }

    @Test
    void refund_Success() {
        // Arrange
        PaymentRequest request = new PaymentRequest();
        request.setOrderNumber("ORDER123");
        request.setData("refund-data");
        when(bindingResult.hasErrors()).thenReturn(false);
        when(paymentService.refundPayment(anyString(), anyString())).thenReturn(true);

        // Act
        ResponseEntity<?> response = trainMealOrderController.refund("user123", request, bindingResult);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertTrue((Boolean) response.getBody());
        verify(paymentService, times(1)).refundPayment("ORDER123", "refund-data");
    }
}