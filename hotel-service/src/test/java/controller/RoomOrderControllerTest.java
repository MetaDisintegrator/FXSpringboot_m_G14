package controller;

import org.fxtravel.services.hotel.common.E_PaymentStatus;
import org.fxtravel.services.hotel.controller.RoomOrderController;
import org.fxtravel.services.hotel.dto.BookHotelRequest;
import org.fxtravel.services.hotel.dto.PaymentRequest;
import org.fxtravel.services.hotel.dto.RoomOrderResponse;
import org.fxtravel.services.hotel.entitiy.RoomOrder;
import org.fxtravel.services.hotel.entitiy.PaymentResultDTO;
import org.fxtravel.services.hotel.mapper.HotelMapper;
import org.fxtravel.services.hotel.mapper.RoomMapper;
import org.fxtravel.services.hotel.service.inter.PaymentService;
import org.fxtravel.services.hotel.service.inter.RoomOrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public class RoomOrderControllerTest {

    private RoomOrderController controller;
    private RoomOrderService roomOrderService;
    private PaymentService paymentService;
    private HotelMapper hotelMapper;
    private RoomMapper roomMapper;
    private BindingResult bindingResult;

    private void injectPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @BeforeEach
    public void setUp() throws Exception {
        roomOrderService = Mockito.mock(RoomOrderService.class);
        paymentService = Mockito.mock(PaymentService.class);
        hotelMapper = Mockito.mock(HotelMapper.class);
        roomMapper = Mockito.mock(RoomMapper.class);
        controller = new RoomOrderController();
        injectPrivateField(controller, "roomOrderService", roomOrderService);
        injectPrivateField(controller, "paymentService", paymentService);
        injectPrivateField(controller, "hotelMapper", hotelMapper);
        injectPrivateField(controller, "roomMapper", roomMapper);
        bindingResult = Mockito.mock(BindingResult.class);
    }
    // getRoom 正向
    @Test
    public void testGetRoom_success() {
        BookHotelRequest req = new BookHotelRequest();
        when(bindingResult.hasErrors()).thenReturn(false);
        RoomOrder order = new RoomOrder();
        order.setId(1);
        order.setOrderNumber("A123");
        order.setRoomId(2);
        order.setTotalAmount(100.0);
        when(roomOrderService.createOrder(any())).thenReturn(order);

        ResponseEntity<?> response = controller.getRoom("1", req, bindingResult);
        assertEquals(200, response.getStatusCodeValue());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals("预订酒店成功", body.get("message"));
    }

    // getRoom 反向：参数校验失败
    @Test
    public void testGetRoom_invalidParams() {
        BookHotelRequest req = new BookHotelRequest();
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(Mockito.mock(FieldError.class)));

        ResponseEntity<?> response = controller.getRoom("1", req, bindingResult);
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(((Map<?, ?>) response.getBody()).containsKey("errors"));
    }

    // getRoom 反向：服务异常
    @Test
    public void testGetRoom_serviceException() {
        BookHotelRequest req = new BookHotelRequest();
        when(bindingResult.hasErrors()).thenReturn(false);
        when(roomOrderService.createOrder(any())).thenThrow(new RuntimeException("异常"));

        ResponseEntity<?> response = controller.getRoom("1", req, bindingResult);
        assertEquals(500, response.getStatusCodeValue());
        assertTrue(((Map<?, ?>) response.getBody()).containsKey("error"));
    }

    // getOrderPaymentStatus 正向
    @Test
    public void testGetOrderPaymentStatus_success() {
        RoomOrder order = new RoomOrder();
        order.setRelatedPaymentId(10);
        when(roomOrderService.getOrderById(anyInt())).thenReturn(order);
        PaymentResultDTO result = new PaymentResultDTO();
        when(paymentService.checkPaymentStatus(anyInt())).thenReturn(result);

        ResponseEntity<?> response = controller.getOrderPaymentStatus("1", 1);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(result, response.getBody());
    }

    // getOrderPaymentStatus 反向：订单不存在
    @Test
    public void testGetOrderPaymentStatus_notFound() {
        when(roomOrderService.getOrderById(anyInt())).thenReturn(null);

        ResponseEntity<?> response = controller.getOrderPaymentStatus("1", 1);
        assertEquals(404, response.getStatusCodeValue());
    }

    // getOrderByUserId 正向
    @Test
    public void testGetOrderByUserId_success() {
        RoomOrder order = new RoomOrder();
        order.setId(1);
        order.setOrderNumber("A123");
        order.setUserId(1);
        order.setHotelId(2);
        order.setRoomId(3);
        order.setStatus(E_PaymentStatus.COMPLETED);
        order.setTotalAmount(100.0);
        order.setCheckInDate(null);
        order.setCheckOutDate(null);
        order.setCreateTime(null);

        when(roomOrderService.getOrdersByUserId(anyInt())).thenReturn(List.of(order));
        when(hotelMapper.selectById(anyInt())).thenReturn(Mockito.mock(org.fxtravel.services.hotel.entitiy.Hotel.class));
        when(roomMapper.selectById(anyInt())).thenReturn(Mockito.mock(org.fxtravel.services.hotel.entitiy.Room.class));

        ResponseEntity<List<RoomOrderResponse>> response = controller.getOrderByUserId("1", 1);
        assertEquals(200, response.getStatusCodeValue());
        assertFalse(response.getBody().isEmpty());
    }

    // getOrderByUserId 反向：无订单
    @Test
    public void testGetOrderByUserId_empty() {
        when(roomOrderService.getOrdersByUserId(anyInt())).thenReturn(Collections.emptyList());

        ResponseEntity<List<RoomOrderResponse>> response = controller.getOrderByUserId("1", 1);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isEmpty());
    }

    // refund 正向
    @Test
    public void testRefund_success() {
        PaymentRequest req = new PaymentRequest();
        when(paymentService.refundPayment(anyString(), any())).thenReturn(true);

        ResponseEntity<?> response = controller.refund("1", req);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(true, response.getBody());
    }

    // refund 反向
    @Test
    public void testRefund_fail() {
        PaymentRequest req = new PaymentRequest();
        when(paymentService.refundPayment(anyString(), any())).thenReturn(false);

        ResponseEntity<?> response = controller.refund("1", req);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(false, response.getBody());
    }
}