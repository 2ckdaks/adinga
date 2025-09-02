package com.adinga.trigger_engine_service.service;

import com.adinga.trigger_engine_service.model.NotificationEvent;
import com.adinga.trigger_engine_service.repository.TriggerRuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class RuleScheduler {

    private final TriggerRuleRepository ruleRepository;
    private final NotificationProducer producer;

    @Scheduled(fixedDelayString = "PT15S")
    @Transactional(readOnly = true)
    public void tick() {
        var rules = ruleRepository.findByEnabledTrue();
        for (var rule : rules) {
            log.info("[RULE] fired: id={}, name={}, interval={}s, lastRunAt={}",
                    rule.getId(), rule.getName(), rule.getIntervalSeconds(), rule.getLastRunAt());

            producer.send(new NotificationEvent(
                    rule.getId(),
                    rule.getName(),
                    java.time.Instant.now()
            ));
        }
    }
}
