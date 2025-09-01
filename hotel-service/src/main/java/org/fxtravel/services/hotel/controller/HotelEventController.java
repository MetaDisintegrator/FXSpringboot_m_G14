package org.fxtravel.services.hotel.controller;

import org.fxtravel.services.hotel.service.impl.HotelServiceImpl;
import org.fxtravel.services.payment.event.data.PaymentInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/events")
public class HotelEventController {

    @Autowired
    private HotelServiceImpl hotelService;

    @PostMapping("/payment-status-change")
    public void handlePaymentStatusChange(@RequestBody PaymentInfo info) {
        hotelService.handlePaymentStatusChange(info);
    }
}