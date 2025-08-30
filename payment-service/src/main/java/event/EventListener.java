package java.event;

@FunctionalInterface
public interface EventListener<T> {
    void onEvent(T eventData);
}
