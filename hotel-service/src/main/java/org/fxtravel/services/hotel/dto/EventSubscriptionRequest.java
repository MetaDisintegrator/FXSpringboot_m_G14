package org.fxtravel.services.hotel.dto;

import org.fxtravel.services.hotel.event.EventType;

public class EventSubscriptionRequest {
    private EventType eventType;
    private String callbackUrl;

    // Getters and setters
    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }
}