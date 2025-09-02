package com.adinga.notification_service.web;

import com.adinga.notification_service.model.NotificationEvent;
import com.adinga.notification_service.service.RecentEventStore;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final RecentEventStore store;

    @GetMapping("/recent")
    public List<NotificationEvent> recent() {
        return store.list();
    }

    @DeleteMapping("/recent")
    public void clear() {
        store.clear();
    }
}
