package com.adinga.notification_service.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "device_push_token", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"deviceId"})
})
public class DevicePushToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String deviceId;

    @Column(nullable = false, length = 200)
    private String expoToken;

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    // --- getters / setters ---
    public Long getId() { return id; }
    public String getDeviceId() { return deviceId; }
    public String getExpoToken() { return expoToken; }
    public Instant getUpdatedAt() { return updatedAt; }

    public void setId(Long id) { this.id = id; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public void setExpoToken(String expoToken) { this.expoToken = expoToken; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
