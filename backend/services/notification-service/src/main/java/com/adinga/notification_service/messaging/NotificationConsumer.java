package com.adinga.notification_service.messaging;

import com.adinga.notification_service.model.NotificationEvent;
import com.adinga.notification_service.service.RecentEventStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final RecentEventStore store;

    @KafkaListener(
            topics = "${app.topics.notifications}",
            groupId = "notification-service",
            containerFactory = "notificationKafkaListenerContainerFactory"
    )
    public void onMessage(NotificationEvent event) {
        log.info("[NOTIFICATIONS] consumed: {}", event);
        store.add(event);
    }
}
