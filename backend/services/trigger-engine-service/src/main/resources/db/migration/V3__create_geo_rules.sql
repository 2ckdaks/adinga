CREATE TABLE IF NOT EXISTS geo_rules (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    todo_id      BIGINT       NOT NULL,
    device_id    VARCHAR(100) NOT NULL,
    lat          DOUBLE       NOT NULL,
    lng          DOUBLE       NOT NULL,
    radius_m     INT          NOT NULL,
    event_when   VARCHAR(10)  NOT NULL, -- ENTER or EXIT
    enabled      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME     NULL ON UPDATE CURRENT_TIMESTAMP
);

CREATE INDEX idx_geo_rules_todo ON geo_rules(todo_id);
