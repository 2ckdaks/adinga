INSERT INTO trigger_rules (id, name, enabled, interval_seconds, last_run_at)
VALUES
  (1, 'demo-log-rule', 1, 15, NULL),
  (2, 'fast-log-rule', 1, 5, NULL)
ON DUPLICATE KEY UPDATE
  name=VALUES(name),
  enabled=VALUES(enabled),
  interval_seconds=VALUES(interval_seconds);
