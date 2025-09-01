package org.fxtravel.services.hotel.client;

import org.fxtravel.services.payment.event.EventListener;
import org.fxtravel.services.payment.event.EventType;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "payment-service", contextId = "eventCenterClient")
public interface EventClient {

    @PostMapping("/api/events/subscribe")
    public <T> void subscribe(EventType eventType, EventListener<T> listener);

    @PostMapping("/api/events/unsubscribe")
    public <T> void unsubscribe(EventType eventType, EventListener<T> listener);

    @PostMapping("/api/events/publish")
    public <T> void publish(EventType eventType, T data);

    @PostMapping("/api/events/publishAsync")
    public <T> void publishAsync(EventType eventType, T data);

}
