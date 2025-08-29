package com.adinga.location_event_service.api;

import com.adinga.location_event_service.dto.LocationEvent;
import com.adinga.location_event_service.kafka.LocationEventPublisher;
import jakarta.validation.Valid;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/events/locations")
public class LocationEventController {

    private final LocationEventPublisher publisher;

    public LocationEventController(LocationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @PostMapping
    public ResponseEntity<?> publish(@Valid @RequestBody LocationEvent req) {
        try {
            var result = publisher.publish(req).join();
            RecordMetadata m = result.getRecordMetadata();
            return ResponseEntity.accepted().body(Map.of(
                    "topic", m.topic(),
                    "partition", m.partition(),
                    "offset", m.offset(),
                    "timestamp", m.timestamp()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "kafka_publish_failed",
                    "message", e.getMessage()
            ));
        }
    }
}
