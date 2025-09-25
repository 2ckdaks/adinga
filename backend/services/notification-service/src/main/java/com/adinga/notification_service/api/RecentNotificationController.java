package com.adinga.notification_service.api;

import com.adinga.notification_service.service.RecentNotificationStore;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class RecentNotificationController {

    private final RecentNotificationStore store;

    @GetMapping("/recent")
    public List<Map<String, Object>> listRecent() {
        return store.list().stream().map(it -> Map.<String, Object>of(
                "ruleId", it.getRuleId(),
                "ruleName", it.getRuleName() != null ? it.getRuleName() : "demo-log-rule",
                "occurredAt", (it.getOccurredAt() != null ? it.getOccurredAt() : Instant.now()).toString()
        )).toList();
    }

    @DeleteMapping("/recent")
    public ResponseEntity<Void> clearRecent() {
        store.clear();
        return ResponseEntity.ok().build();
    }
}
