package com.adinga.trigger_engine_service.service;

import com.adinga.trigger_engine_service.dto.GeoRuleDtos;
import com.adinga.trigger_engine_service.domain.GeoRule;
import com.adinga.trigger_engine_service.repository.GeoRuleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class GeoRuleService {

    private final GeoRuleRepository repo;

    public GeoRuleService(GeoRuleRepository repo) {
        this.repo = repo;
    }

    public GeoRule create(GeoRuleDtos.CreateReq req) {
        GeoRule r = new GeoRule();
        r.setTodoId(req.getTodoId());
        r.setDeviceId(req.getDeviceId());
        r.setLat(req.getLat());
        r.setLng(req.getLng());
        r.setRadiusM(req.getRadiusM());
        r.setWhen(req.getWhen());
        r.setEnabled(req.getEnabled() != null ? req.getEnabled() : Boolean.TRUE);
        return repo.save(r);
    }

    public GeoRule update(Long id, GeoRuleDtos.UpdateReq req) {
        GeoRule r = repo.findById(id).orElseThrow();
        if (req.getLat() != null) r.setLat(req.getLat());
        if (req.getLng() != null) r.setLng(req.getLng());
        if (req.getRadiusM() != null) r.setRadiusM(req.getRadiusM());
        if (req.getWhen() != null) r.setWhen(req.getWhen());
        if (req.getEnabled() != null) r.setEnabled(req.getEnabled());
        r.setUpdatedAt(LocalDateTime.now());
        return repo.save(r);
    }

    public GeoRule find(Long id) {
        return repo.findById(id).orElseThrow();
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
