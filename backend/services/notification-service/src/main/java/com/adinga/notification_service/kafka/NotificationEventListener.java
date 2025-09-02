package com.adinga.notification_service.kafka;

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
            topics = KafkaTopics.NOTIFICATIONS,
            containerFactory = "kafkaListenerContainerFactory",
            groupId = "notification-service"
    )
    public void onMessage(
            @Payload String value,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            @Header(name = KafkaHeaders.RECEIVED_KEY, required = false) String key
    ) {
        log.info("Notification received topic={}, partition={}, offset={}, key={}, value={}",
                topic, partition, offset, key, value);

        // 데모용: 값에 "fail"이 들어있으면 실패로 간주 → DLT로 보내짐
        if (value != null && value.contains("fail")) {
            throw new IllegalArgumentException("forced failure for demo");
        }
    }
}
