package com.adinga.trigger_engine_service.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "trigger_rules")
public class TriggerRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=100)
    private String name;

    /** n초마다 실행 */
    @Column(name="interval_seconds", nullable=false)
    private int intervalSeconds;

    @Column(nullable=false)
    private boolean enabled = true;

    private LocalDateTime lastRunAt;

    // getters/setters
    public Long getId() { return id; }
    public String getName() { return name; }
    public int getIntervalSeconds() { return intervalSeconds; }
    public boolean isEnabled() { return enabled; }
    public LocalDateTime getLastRunAt() { return lastRunAt; }

    public void setName(String name) { this.name = name; }
    public void setIntervalSeconds(int intervalSeconds) { this.intervalSeconds = intervalSeconds; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public void setLastRunAt(LocalDateTime lastRunAt) { this.lastRunAt = lastRunAt; }
}