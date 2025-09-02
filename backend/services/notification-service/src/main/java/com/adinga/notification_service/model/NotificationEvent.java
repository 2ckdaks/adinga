package com.adinga.notification_service.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationEvent {
    @JsonAlias({"id", "ruleId"})
    private Long ruleId;

    @JsonAlias({"name", "ruleName"})
    private String ruleName;

    @JsonAlias({"timestamp", "occurredAt", "ts"})
    private Instant occurredAt;

    public NotificationEvent() {}

    public NotificationEvent(Long ruleId, String ruleName, Instant occurredAt) {
        this.ruleId = ruleId;
        this.ruleName = ruleName;
        this.occurredAt = occurredAt;
    }

    public Long getRuleId() { return ruleId; }
    public String getRuleName() { return ruleName; }
    public Instant getOccurredAt() { return occurredAt; }

    public void setRuleId(Long ruleId) { this.ruleId = ruleId; }
    public void setRuleName(String ruleName) { this.ruleName = ruleName; }
    public void setOccurredAt(Instant occurredAt) { this.occurredAt = occurredAt; }

    @Override
    public String toString() {
        return "NotificationEvent{ruleId=%d, ruleName='%s', occurredAt=%s}"
                .formatted(ruleId, ruleName, occurredAt);
    }
}
