package com.adinga.trigger_engine_service.model;

import java.time.Instant;

public record NotificationEvent(
        Long ruleId,
        String ruleName,
        Instant occurredAt
) {}
