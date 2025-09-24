package com.adinga.trigger_engine_service.api;

import com.adinga.trigger_engine_service.api.dto.GeoRuleDtos;
import com.adinga.trigger_engine_service.domain.GeoRule;
import com.adinga.trigger_engine_service.service.GeoRuleService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/triggers")
public class GeoRuleController {

    private final GeoRuleService service;

    public GeoRuleController(GeoRuleService service) {
        this.service = service;
    }

    @PostMapping("/rules")
    public ResponseEntity<GeoRuleDtos.Res> create(@Valid @RequestBody GeoRuleDtos.CreateReq req) {
        GeoRule created = service.create(req);
        return ResponseEntity
                .created(URI.create("/triggers/rules/" + created.getId()))
                .body(toRes(created));
    }

    @GetMapping("/rules/{id}")
    public GeoRuleDtos.Res get(@PathVariable Long id) {
        return toRes(service.find(id));
    }

    @PatchMapping("/rules/{id}")
    public GeoRuleDtos.Res patch(@PathVariable Long id, @Valid @RequestBody GeoRuleDtos.UpdateReq req) {
        return toRes(service.update(id, req));
    }

    @DeleteMapping("/rules/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    private static GeoRuleDtos.Res toRes(GeoRule r) {
        GeoRuleDtos.Res res = new GeoRuleDtos.Res();
        res.setId(r.getId());
        res.setTodoId(r.getTodoId());
        res.setDeviceId(r.getDeviceId());
        res.setLat(r.getLat());
        res.setLng(r.getLng());
        res.setRadiusM(r.getRadiusM());
        res.setWhen(r.getWhen());
        res.setEnabled(r.getEnabled());
        return res;
    }
}
