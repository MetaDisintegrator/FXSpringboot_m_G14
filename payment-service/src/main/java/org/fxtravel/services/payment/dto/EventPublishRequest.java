package org.fxtravel.services.payment.dto;

import org.fxtravel.services.payment.event.EventType;

public class EventPublishRequest<T> {
    private EventType eventType;
    private T data;

    // Getters and setters
    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}