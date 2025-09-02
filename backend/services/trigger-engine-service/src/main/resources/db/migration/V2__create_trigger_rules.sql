CREATE TABLE IF NOT EXISTS trigger_rules
(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    interval_seconds INT NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    last_run_at DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 데모 룰: 15초마다 한 번 실행
INSERT INTO trigger_rules(name, interval_seconds, enabled)
VALUES ('demo-log-rule', 15, TRUE);
