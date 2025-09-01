package org.fxtravel.services.payment.event;

public interface EventCenter {
    void subscribe(EventType eventType, String callbackUrl);
    void unsubscribe(EventType eventType, String callbackUrl);
    void publish(EventType eventType, Object data);
    void publishAsync(EventType eventType, Object data);
}