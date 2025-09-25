package com.adinga.location_event_service.kafka;

import com.adinga.location_event_service.dto.LocationEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

@Service
public class LocationEventPublisher {

    private final KafkaTemplate<String, LocationEvent> kafkaTemplate;
    private final String topic;

    public LocationEventPublisher(
            KafkaTemplate<String, LocationEvent> kafkaTemplate,
            @Value("${app.kafka.location-topic:location-events}") String topic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public CompletableFuture<SendResult<String, LocationEvent>> publish(LocationEvent e) {
        if (e.getTs() == null) e.setTs(Instant.now());
        String key = (e.getDeviceId() == null || e.getDeviceId().isBlank()) ? "unknown" : e.getDeviceId();

        return kafkaTemplate.send(topic, key, e).whenComplete((r, ex) -> {
            if (ex == null) {
                var md = r.getRecordMetadata();
                org.slf4j.LoggerFactory.getLogger(getClass())
                        .info("[LOC] Kafka 전송 성공 topic={}, partition={}, offset={}", md.topic(), md.partition(), md.offset());
            } else {
                org.slf4j.LoggerFactory.getLogger(getClass())
                        .error("[LOC] Kafka 전송 실패 topic={}, err={}", topic, ex.toString());
            }
        });
    }
}
