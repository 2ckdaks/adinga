package com.adinga.trigger_engine_service.service;

import com.adinga.trigger_engine_service.domain.TriggerRule;
import com.adinga.trigger_engine_service.repository.TriggerRuleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RuleScheduler {

    private static final Logger log = LoggerFactory.getLogger(RuleScheduler.class);

    private final TriggerRuleRepository repo;

    public RuleScheduler(TriggerRuleRepository repo) {
        this.repo = repo;
    }

    @Value("${rules.poll-interval-ms:5000}")
    private long pollIntervalMs;

    /** 고정 지연으로 폴링: 활성화된 룰 읽고 due 여부 계산해 실행 */
    @Transactional
    @Scheduled(fixedDelayString = "${rules.poll-interval-ms:5000}")
    public void tick() {
        LocalDateTime now = LocalDateTime.now();
        List<TriggerRule> rules = repo.findByEnabledTrue();

        for (TriggerRule rule : rules) {
            LocalDateTime last = rule.getLastRunAt() == null
                    ? now.minusYears(10)
                    : rule.getLastRunAt();

            if (Duration.between(last, now).getSeconds() >= rule.getIntervalSeconds()) {
                // === 여기서 실제 액션을 수행하면 됨 (현재는 로그만) ===
                log.info("[RULE] fired: id={}, name={}, interval={}s, lastRunAt={}",
                        rule.getId(), rule.getName(), rule.getIntervalSeconds(), rule.getLastRunAt());

                // 실행 후 lastRunAt 갱신
                rule.setLastRunAt(now);
            }
        }
    }
}
