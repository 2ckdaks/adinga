package com.adinga.notification_service.service;

import com.adinga.notification_service.model.NotificationEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

@Component
public class RecentEventStore {
    private static final int CAPACITY = 100;
    private final Deque<NotificationEvent> deque = new ConcurrentLinkedDeque<>();

    public void add(NotificationEvent e) {
        deque.addFirst(e);
        while (deque.size() > CAPACITY) {
            deque.pollLast();
        }
    }

    public List<NotificationEvent> list() {
        return new ArrayList<>(deque);
    }

    public void clear() {
        deque.clear();
    }
}
