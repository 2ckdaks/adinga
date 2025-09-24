package com.adinga.location_event_service.api;

import com.adinga.location_event_service.dto.LocationEvent;
import com.adinga.location_event_service.kafka.LocationEventPublisher;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/locations")
public class LocationEventController {

    private final LocationEventPublisher publisher;

    public LocationEventController(LocationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @PostMapping("/events/locations")
    public ResponseEntity<Void> publish(@Valid @RequestBody LocationEvent ev) {
        publisher.publish(ev);
        // 비동기 전송이므로 202가 의미적으로 더 맞음
        return ResponseEntity.accepted().build();
    }

    // 개발 편의를 위한 단순 CORS 허용(게이트웨이에서 처리하면 이 부분은 제거 가능)
    @CrossOrigin
    @GetMapping("/healthz")
    public String healthz() { return "ok"; }
}
