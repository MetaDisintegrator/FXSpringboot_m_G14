package org.fxtravel.services.payment.controller;

import org.fxtravel.services.payment.event.EventCenter;
import org.fxtravel.services.payment.event.EventListener;
import org.fxtravel.services.payment.event.EventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventCenter eventCenter;

    @PostMapping("/subscribe")
    public <T> void subscribe(EventType eventType, EventListener<T> listener){
        eventCenter.subscribe(eventType, listener);
    };

    @PostMapping("/unsubscribe")
    public <T> void unsubscribe(EventType eventType, EventListener<T> listener) {
        eventCenter.unsubscribe(eventType, listener);
    }

    @PostMapping("/publish")
    public <T> void publish(EventType eventType, T data) {
        eventCenter.publish(eventType, data);
    }

    @PostMapping("/publishAsync")
    public <T> void publishAsync(EventType eventType, T data){
        eventCenter.publishAsync(eventType, data);
    }
}
