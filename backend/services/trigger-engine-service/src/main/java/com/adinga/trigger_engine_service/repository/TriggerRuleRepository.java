package com.adinga.trigger_engine_service.repository;

import com.adinga.trigger_engine_service.domain.TriggerRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TriggerRuleRepository extends JpaRepository<TriggerRule, Long> {
    List<TriggerRule> findByEnabledTrue();
}
