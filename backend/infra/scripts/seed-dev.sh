#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."

echo "== MySQL: seed trigger_rules =="
docker compose exec -T mysql mysql -uapp -papp adinga < seeds/mysql/trigger_rules.sql

echo "== Kafka: copy seed files into container =="
docker compose cp seeds/kafka/seed_notifications.txt kafka:/tmp/seed_notifications.txt
docker compose cp seeds/kafka/seed_location_events.jsonl kafka:/tmp/seed_location_events.jsonl

echo "== Kafka: produce notifications =="
docker compose exec -T kafka sh -lc "rpk topic produce --brokers kafka:9092 notifications < /tmp/seed_notifications.txt"

echo "== Kafka: produce location-events =="
docker compose exec -T kafka sh -lc "rpk topic produce --brokers kafka:9092 location-events < /tmp/seed_location_events.jsonl"

cat <<'EON'

[OK] 샘플 데이터 주입 완료.

검증 가이드:
  1) Trigger 룰 파이어 로그:
     docker compose logs -f --since=1m trigger-engine-service

  2) Notification 소비/에러 처리 확인:
     docker compose logs -f --since=1m notification-service
     # DLT에 들어갔는지 확인:
     docker compose exec -T kafka rpk topic consume --brokers kafka:9092 notifications.dlt -n 1

  3) Swagger에서 각 서비스 문서 확인:
     http://localhost:8000/swagger-ui.html

EON
