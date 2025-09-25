package com.adinga.notification_service.api;

import com.adinga.notification_service.dto.PushTokenReq;
import com.adinga.notification_service.model.DevicePushToken;
import com.adinga.notification_service.repository.DevicePushTokenRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class PushTokenController {

    private final DevicePushTokenRepository repo;

    @PostMapping("/tokens")
    public ResponseEntity<Void> register(@Valid @RequestBody PushTokenReq req) {
        repo.findByDeviceId(req.deviceId())
                .map(e -> { e.setExpoToken(req.expoToken()); e.setUpdatedAt(Instant.now()); return repo.save(e); })
                .orElseGet(() -> {
                    var e = new DevicePushToken();
                    e.setDeviceId(req.deviceId());
                    e.setExpoToken(req.expoToken());
                    e.setUpdatedAt(Instant.now());
                    return repo.save(e);
                });
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/tokens/{deviceId}")
    public ResponseEntity<Void> unregister(@PathVariable String deviceId) {
        repo.findByDeviceId(deviceId).ifPresent(repo::delete);
        return ResponseEntity.noContent().build();
    }
}
