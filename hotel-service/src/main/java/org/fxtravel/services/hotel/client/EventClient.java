package org.fxtravel.services.hotel.client;

import org.fxtravel.services.payment.dto.EventPublishRequest;
import org.fxtravel.services.payment.dto.EventSubscriptionRequest;
import org.fxtravel.services.payment.event.EventListener;
import org.fxtravel.services.payment.event.EventType;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service", contextId = "eventCenterClient")
public interface EventClient {

    @PostMapping("/api/events/subscribe")
    void subscribe(@RequestBody EventSubscriptionRequest request);

    @PostMapping("/api/events/unsubscribe")
    void unsubscribe(@RequestBody EventSubscriptionRequest request);

    @PostMapping("/api/events/publish")
    <T> void publish(@RequestBody EventPublishRequest<T> request);

    @PostMapping("/api/events/publishAsync")
    <T> void publishAsync(@RequestBody EventPublishRequest<T> request);
}
