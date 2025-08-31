package org.fxtravel.services.payment.event;

@FunctionalInterface
public interface EventListener<T> {
    void onEvent(T eventData);
}
