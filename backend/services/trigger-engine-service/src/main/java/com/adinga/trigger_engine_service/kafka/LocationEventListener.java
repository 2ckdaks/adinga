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

        // (데모) ruleId/Name 이 오면 그대로, 없으면 기본값으로 통과시킴
        int ruleId = ev.getRuleId() != null ? ev.getRuleId() : 1;
        String ruleName = ev.getRuleName() != null ? ev.getRuleName() : "demo-log-rule";

        NotificationEvent ne = new NotificationEvent(ruleId, ruleName, Instant.now());
        notificationProducer.send(ne); // 기존 Producer 사용
    }
}
