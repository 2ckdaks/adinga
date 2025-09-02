package com.adinga.notification_service.kafka;

import com.adinga.notification_service.model.NotificationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationEventListener {

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

        // 타입(레코드/POJO) 상관없이 데모 실패 트리거
        String asText = String.valueOf(event);
        if (asText.contains("fail")) {
            throw new IllegalArgumentException("forced failure for demo");
        }
    }
}
