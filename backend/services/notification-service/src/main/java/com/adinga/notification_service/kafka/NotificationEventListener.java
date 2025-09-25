package com.adinga.notification_service.kafka;

import com.adinga.notification_service.repository.DevicePushTokenRepository;
import com.adinga.notification_service.service.GeoRuleClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.header.Headers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import com.adinga.notification_service.service.RecentNotificationStore;
import java.time.Instant;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final DevicePushTokenRepository tokenRepo;
    private final GeoRuleClient geoRuleClient;
    private final RecentNotificationStore recent;

    private final WebClient expo = WebClient.builder()
            .baseUrl("https://exp.host/--/api/v2/push")
            .build();

    @Value("${app.push.enabled:true}")
    private boolean pushEnabled;

    public record NotificationEvent(Long ruleId, String ruleName, java.time.Instant occurredAt) {}

    @KafkaListener(topics = "${app.topics.notifications:notifications}", groupId = "notification-service")
    public void onMessage(
            @Payload NotificationEvent ev,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.RECEIVED_PARTITION) Integer partition,
            @Header(KafkaHeaders.OFFSET) Long offset,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_TIMESTAMP) Long ts,
            @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String msgKey,
            @Header(KafkaHeaders.RECEIVED_HEADERS) Headers headers
    ) {
        log.info("Notification received topic={}, partition={}, offset={}, key={}, event={}",
                topic, partition, offset, key, ev);

        Instant occurred = ev.occurredAt() != null ? ev.occurredAt() : Instant.now();
        recent.add(ev.ruleId(), ev.ruleName(), occurred);

        if (!pushEnabled) return;

        try {
            // 1) ruleId → deviceId 조회
            String deviceId = null;
            if (ev.ruleId() != null) {
                var rule = geoRuleClient.getRule(ev.ruleId()).block(Duration.ofSeconds(3));
                deviceId = (rule != null) ? rule.getDeviceId() : null;
            }
            if (deviceId == null) {
                log.info("[push] skip (no deviceId for ruleId={})", ev.ruleId());
                return;
            }

            // 2) deviceId → Expo 토큰들
            var tokens = tokenRepo.findAllByDeviceIdIn(List.of(deviceId))
                    .stream().map(t -> t.getExpoToken()).toList();

            if (tokens.isEmpty()) {
                log.info("[push] skip (no tokens for deviceId={})", deviceId);
                return;
            }

            // 3) Expo Push (토큰별 전송)
            for (var token : tokens) {
                try {
                    var body = Map.of(
                            "to", token,
                            "title", ev.ruleName() != null ? ev.ruleName() : "알림",
                            "body", "위치 트리거 발생",
                            "data", Map.of(
                                    "ruleId", ev.ruleId(),
                                    "occurredAt", ev.occurredAt() != null ? ev.occurredAt().toString() : null
                            )
                    );
                    var rsp = expo.post().uri("/send")
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(body)
                            .retrieve()
                            .bodyToMono(String.class)
                            .block(Duration.ofSeconds(5));
                    log.info("[push] sent -> {}", rsp);
                } catch (Exception e) {
                    log.warn("[push] send failed token={}", token, e);
                }
            }
        } catch (Exception e) {
            log.warn("[push] unexpected failure", e);
        }
    }
}
