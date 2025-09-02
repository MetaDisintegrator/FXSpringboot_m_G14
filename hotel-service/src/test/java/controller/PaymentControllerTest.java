package controller;

import org.fxtravel.services.hotel.controller.PaymentController;
import org.fxtravel.services.hotel.dto.PaymentRequest;
import org.fxtravel.services.hotel.service.inter.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public class PaymentControllerTest {

    private PaymentController controller;
    private PaymentService paymentService;

    private void injectPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
    @BeforeEach
    public void setUp() throws Exception {
        paymentService = Mockito.mock(PaymentService.class);
        controller = new PaymentController();
        injectPrivateField(controller, "paymentService", paymentService);
    }

    // completePayment 正向
    @Test
    public void testCompletePayment_success() {
        PaymentRequest req = new PaymentRequest();
        when(paymentService.completePayment(anyString(), any())).thenReturn(true);

        ResponseEntity<Boolean> response = controller.completePayment("1", req);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody());
    }

    // completePayment 反向
    @Test
    public void testCompletePayment_fail() {
        PaymentRequest req = new PaymentRequest();
        when(paymentService.completePayment(anyString(), any())).thenReturn(false);

        ResponseEntity<Boolean> response = controller.completePayment("1", req);
        assertEquals(200, response.getStatusCodeValue());
        assertFalse(response.getBody());
    }

    // cancelPayment 正向
    @Test
    public void testCancelPayment_success() {
        PaymentRequest req = new PaymentRequest();
        when(paymentService.failPayment(anyString(), any())).thenReturn(true);

        ResponseEntity<Boolean> response = controller.cancelPayment("1", req);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody());
    }

    // cancelPayment 反向
    @Test
    public void testCancelPayment_fail() {
        PaymentRequest req = new PaymentRequest();
        when(paymentService.failPayment(anyString(), any())).thenReturn(false);

        ResponseEntity<Boolean> response = controller.cancelPayment("1", req);
        assertEquals(200, response.getStatusCodeValue());
        assertFalse(response.getBody());
    }

    // finishPayment 正向
    @Test
    public void testFinishPayment_success() {
        PaymentRequest req = new PaymentRequest();
        when(paymentService.finishPayment(anyString(), any())).thenReturn(true);

        ResponseEntity<Boolean> response = controller.finishPayment("1", req);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody());
    }

    // finishPayment 反向
    @Test
    public void testFinishPayment_fail() {
        PaymentRequest req = new PaymentRequest();
        when(paymentService.finishPayment(anyString(), any())).thenReturn(false);

        ResponseEntity<Boolean> response = controller.finishPayment("1", req);
        assertEquals(200, response.getStatusCodeValue());
        assertFalse(response.getBody());
    }
}