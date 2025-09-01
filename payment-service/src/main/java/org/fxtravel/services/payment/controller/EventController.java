package org.fxtravel.services.payment.controller;

import org.fxtravel.services.payment.dto.EventPublishRequest;
import org.fxtravel.services.payment.dto.EventSubscriptionRequest;
import org.fxtravel.services.payment.event.EventCenter;
import org.fxtravel.services.payment.event.EventListener;
import org.fxtravel.services.payment.event.EventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventCenter eventCenter;

    @PostMapping("/subscribe")
    public void subscribe(@RequestBody EventSubscriptionRequest request) {
        eventCenter.subscribe(request.getEventType(), request.getCallbackUrl());
    }

    @PostMapping("/unsubscribe")
    public void unsubscribe(@RequestBody EventSubscriptionRequest request) {
        eventCenter.unsubscribe(request.getEventType(), request.getCallbackUrl());
    }

    @PostMapping("/publish")
    public <T> void publish(@RequestBody EventPublishRequest<T> request) {
        eventCenter.publish(request.getEventType(), request.getData());
    }

    @PostMapping("/publishAsync")
    public <T> void publishAsync(@RequestBody EventPublishRequest<T> request) {
        eventCenter.publishAsync(request.getEventType(), request.getData());
    }
}