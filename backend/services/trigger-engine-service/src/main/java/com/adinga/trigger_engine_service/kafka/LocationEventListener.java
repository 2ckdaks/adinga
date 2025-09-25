package com.adinga.trigger_engine_service.kafka;

import com.adinga.trigger_engine_service.dto.LocationEvent;
import com.adinga.trigger_engine_service.model.NotificationEvent;
import com.adinga.trigger_engine_service.service.NotificationProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class LocationEventListener {

    private final NotificationProducer notificationProducer;

    // trigger-input 소비 → 간단 룰 매칭 후 notifications로 전송
    @KafkaListener(
            topics = "${app.topics.location:trigger-input}",
            groupId = "${spring.kafka.consumer.group-id:trigger-engine}"
    )
    public void onMessage(@Payload LocationEvent ev) {
        log.info("[TRG] location consumed deviceId={}, lat={}, lng={}, ts={}",
                ev.getDeviceId(), ev.getLat(), ev.getLng(), ev.getTs());

        Long ruleId = ev.getRuleId() == null ? null : ev.getRuleId().longValue();

        String ruleName = ev.getRuleName() != null ? ev.getRuleName() : "demo-log-rule";

        Instant occurredAt = ev.getTs() != null ? ev.getTs() : Instant.now();

        NotificationEvent ne = new NotificationEvent(ruleId, ruleName, occurredAt);
        notificationProducer.send(ne);
    }
}
