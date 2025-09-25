package com.adinga.trigger_engine_service.dto;

import lombok.Data;
import java.time.Instant;

@Data
public class LocationEvent {
    private String deviceId;
    private double lat;
    private double lng;
    // location-event-service에서 epoch-nano로 오므로 Instant로 받음
    private Instant ts;

    private Integer ruleId;
    private String ruleName;
}
