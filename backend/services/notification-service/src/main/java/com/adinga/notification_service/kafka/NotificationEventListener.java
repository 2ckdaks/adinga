package com.adinga.notification_service.kafka;

import com.adinga.notification_service.model.NotificationEvent;
import com.adinga.notification_service.service.RecentEventStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final RecentEventStore store;

    @KafkaListener(
            topics = "${app.topics.notifications:notifications}",
            containerFactory = "notificationKafkaListenerContainerFactory",
            groupId = "${spring.kafka.consumer.group-id:notification-service}"
    )
    public void onMessage(
            @Payload NotificationEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            @Header(name = KafkaHeaders.RECEIVED_KEY, required = false) String key
    ) {
        log.info("Notification received topic={}, partition={}, offset={}, key={}, event={}",
                topic, partition, offset, key, event);

        // 최근 이벤트 저장( /api/notifications/recent 에서 보이게 )
        store.add(event);

        // 데모 실패 트리거는 유지
        String asText = String.valueOf(event);
        if (asText.contains("fail")) {
            throw new IllegalArgumentException("forced failure for demo");
        }
    }
}
