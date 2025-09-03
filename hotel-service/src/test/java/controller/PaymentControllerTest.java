package controller;

import org.fxtravel.services.hotel.controller.PaymentController;
import org.fxtravel.services.hotel.dto.PaymentRequest;
import org.fxtravel.services.hotel.service.inter.PaymentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @InjectMocks
    private PaymentController paymentController;

    @Mock
    private PaymentService paymentService;

    @Test
    void completePayment_success() {
        PaymentRequest request = new PaymentRequest();
        request.setOrderNumber("123");
        request.setData("mockData");

        Mockito.when(paymentService.completePayment("123", "mockData")).thenReturn(true);

        ResponseEntity<Boolean> response = paymentController.completePayment("user1", request);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody());
    }

    @Test
    void cancelPayment_fail() {
        PaymentRequest request = new PaymentRequest();
        request.setOrderNumber("456");
        request.setData("errorData");

        Mockito.when(paymentService.failPayment("456", "errorData")).thenReturn(false);

        ResponseEntity<Boolean> response = paymentController.cancelPayment("user2", request);

        assertEquals(200, response.getStatusCodeValue());
        assertFalse(response.getBody());
    }

    @Test
    void finishPayment_success() {
        PaymentRequest request = new PaymentRequest();
        request.setOrderNumber("789");
        request.setData("finishData");

        Mockito.when(paymentService.finishPayment("789", "finishData")).thenReturn(true);

        ResponseEntity<Boolean> response = paymentController.finishPayment("user3", request);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody());
    }
}
