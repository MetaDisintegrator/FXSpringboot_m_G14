package org.fxtravel.services.payment.event;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Service
public class EventCenterImpl implements EventCenter {
    private final Map<EventType, Set<String>> callbackUrls = new ConcurrentHashMap<>();
    private final Executor asyncExecutor = Executors.newFixedThreadPool(4);
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public void subscribe(EventType eventType, String callbackUrl) {
        callbackUrls.computeIfAbsent(eventType, k -> ConcurrentHashMap.newKeySet())
                .add(callbackUrl);
    }

    @Override
    public void unsubscribe(EventType eventType, String callbackUrl) {
        Set<String> urls = callbackUrls.get(eventType);
        if (urls != null) {
            urls.remove(callbackUrl);
        }
    }

    @Override
    public void publish(EventType eventType, Object data) {
        Set<String> urls = callbackUrls.get(eventType);
        if (urls != null) {
            for (String url : urls) {
                try {
                    restTemplate.postForObject(url, data, Void.class);
                } catch (Exception e) {
                    System.err.println("Error calling callback URL: " + url + ", error: " + e.getMessage());
                }
            }
        }
    }

    @Override
    public void publishAsync(EventType eventType, Object data) {
        asyncExecutor.execute(() -> publish(eventType, data));
    }
}