package org.fxtravel.services.hotel.event;

@FunctionalInterface
public interface EventListener<T> {
    void onEvent(T eventData);
}
