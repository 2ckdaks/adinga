package com.adinga.trigger_engine_service.repository;

import com.adinga.trigger_engine_service.domain.GeoRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GeoRuleRepository extends JpaRepository<GeoRule, Long> {
    Optional<GeoRule> findByTodoId(Long todoId);
}
