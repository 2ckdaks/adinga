package com.adinga.trigger_engine_service.service;

import com.adinga.trigger_engine_service.model.NotificationEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationProducer {
    private static final Logger log = LoggerFactory.getLogger(NotificationProducer.class);

    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    @Value("${app.topics.notifications}")
    private String topic;

    public void send(NotificationEvent event) {
        kafkaTemplate.send(topic, String.valueOf(event.ruleId()), event)
                .whenComplete((r, ex) -> {
                    if (ex != null) {
                        log.error("Kafka 전송 실패 topic={}, key={}, err={}", topic, event.ruleId(), ex.toString(), ex);
                    } else {
                        log.info("Kafka 전송 성공 topic={}, partition={}, offset={}",
                                r.getRecordMetadata().topic(),
                                r.getRecordMetadata().partition(),
                                r.getRecordMetadata().offset());
                    }
                });
    }
}
