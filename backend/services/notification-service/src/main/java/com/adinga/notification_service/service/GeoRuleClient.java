package com.adinga.notification_service.service;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/** trigger-engine-service 의 /triggers/rules/{id} 조회용 */
@Component
public class GeoRuleClient {
    private final WebClient web;

    public GeoRuleClient(@Value("${app.triggerBaseUrl}") String base) {
        this.web = WebClient.builder().baseUrl(base).build();
    }

    public Mono<GeoRuleRes> getRule(Long id) {
        return web.get().uri("/triggers/rules/{id}", id)
                .retrieve()
                .bodyToMono(GeoRuleRes.class);
    }

    @Data
    public static class GeoRuleRes {
        private Long id;
        private Long todoId;
        private String deviceId;
        private Double lat;
        private Double lng;
        private Integer radiusM;
        private String when;
        private Boolean enabled;
    }
}
