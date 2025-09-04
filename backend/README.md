Adinga Dev Stack – Quick Guide

로컬 개발용 전체 스택(MySQL/Redis/Kafka + gateway & 4 services)을 Docker Compose로 띄우고, 헬스체크/디버그/카프카(rpk) 스니펫으로 빠르게 확인할 수 있는 가이드입니다.

1) 부팅/정지
# infra 디렉토리에서
cd backend/infra

# 전체 부팅
docker compose up -d

# 코드 변경 자동 재빌드/재시작(선택)
docker compose watch

# 정지
docker compose down

# 볼륨까지 정리
docker compose down -v

2) 포트 & 엔드포인트
   컴포넌트	로컬 포트	핵심 엔드포인트
   api-gateway	8000	Swagger UI: http://localhost:8000/swagger-ui.html
   todo-service	8201	http://localhost:8201/actuator/health
   location-event-service	8301	http://localhost:8301/actuator/health
   trigger-engine-service	8401	http://localhost:8401/actuator/health
   notification-service	8501	http://localhost:8501/actuator/health
   MySQL	3307 (→ 컨테이너 3306)
   Redis	6379
   Kafka (Redpanda)	9092(internal), 19092(host)

Gateway에 개발용 토큰이 켜진 상태라면(기본값) 게이트웨이로 API 호출 시 Authorization: Bearer dev-adinga 헤더를 넣어 주세요.

게이트웨이 경유 헬스체크(토큰 필요)
# locations
curl -H "Authorization: Bearer dev-adinga" http://localhost:8000/api/locations/actuator/health

# notifications
curl -H "Authorization: Bearer dev-adinga" http://localhost:8000/api/notifications/actuator/health

# triggers
curl -H "Authorization: Bearer dev-adinga" http://localhost:8000/api/triggers/actuator/health

# todos
curl -H "Authorization: Bearer dev-adinga" http://localhost:8000/api/todos/actuator/health

3) Swagger (OpenAPI)

게이트웨이에 집합 UI가 노출됩니다.

URL: http://localhost:8000/swagger-ui.html

드롭다운에서 각 서비스 선택 시, 게이트웨이가 /api/<서비스>/v3/api-docs를 프록시합니다.

문제가 있을 때:

401 → 게이트웨이의 BearerAuth 프리필터 동작. Authorization 헤더(위 토큰) 넣었는지 확인.

404 → 서비스 쪽에 springdoc 미적용/버전 불일치 가능. 각 서비스 직접 /v3/api-docs 열어 상태 확인.

4) 로그/디버그
# 최근 2분 로그 팔로잉
docker compose logs -f --since=2m api-gateway
docker compose logs -f --since=2m todo-service
docker compose logs -f --since=2m location-event-service
docker compose logs -f --since=2m trigger-engine-service
docker compose logs -f --since=2m notification-service


애플리케이션 로그 레벨(임시) 조정 예:

# 게이트웨이에서 특정 패키지 DEBUG (컨테이너 안)
docker compose exec api-gateway sh -lc 'echo -e "\nlogging.level.com.adinga=DEBUG" >> /app/classes/application.properties && kill -HUP 1'

5) Kafka(Redpanda) – rpk 스니펫

컨테이너 내부 브로커 주소는 kafka:9092 입니다. (호스트용 19092는 rpk가 호스트에서 직접 접속할 때만 사용)

토픽 확인/생성
# 목록
docker compose exec kafka rpk topic list --brokers kafka:9092

# 필요하면 생성 (예시)
docker compose exec kafka rpk topic create notifications --brokers kafka:9092 -p 3 -r 1
docker compose exec kafka rpk topic create notifications.dlt --brokers kafka:9092 -p 3 -r 1
docker compose exec kafka rpk topic create location-events --brokers kafka:9092 -p 3 -r 1
docker compose exec kafka rpk topic create location-events.dlt --brokers kafka:9092 -p 3 -r 1

프로듀스(예시 데이터)

notification-service는 NotificationEvent(샘플) JSON을 소비합니다.

# notifications 토픽으로 JSON 발행 (게이트웨이/트리거 없이 직접)
echo '{"id":999,"name":"manual-test","timestamp":"2025-01-01T00:00:00Z"}' \
| docker compose exec -T kafka rpk topic produce notifications --brokers kafka:9092

컨슘(최근 메시지 n건)
# notifications
docker compose exec kafka rpk topic consume notifications --brokers kafka:9092 --offset newest -n 10 -f "%k | %v"

# DLT 확인
docker compose exec kafka rpk topic consume notifications.dlt --brokers kafka:9092 --offset newest -n 10 -f "%k | %v"

클러스터 정보
docker compose exec kafka rpk cluster info --brokers kafka:9092

6) 자주 나오는 이슈

Swagger 401: Gateway 개발 토큰 필터 동작. 헤더에 Authorization: Bearer dev-adinga.

Swagger 404: 각 서비스에 springdoc 미설치/버전 불일치. 서비스 직접 /v3/api-docs 호출로 진단.

rpk produce가 멈춤: 기본이 interactive 모드. 위처럼 echo ... | rpk topic produce 형태로 파이프 사용.

게이트웨이 라우팅 확인:

curl -s http://localhost:8000/actuator/gateway/routes | jq '.[].route_id'
docker compose logs -f --since=2m api-gateway

부록) 서비스별 요약

api-gateway (8000)

라우팅: /api/todos/**, /api/locations/**, /api/triggers/**, /api/notifications/**

Swagger 집합: /swagger-ui.html

개발 토큰(ON): Authorization: Bearer dev-adinga

todo-service (8201)

Web + JPA + Redis + springdoc

헬스: /actuator/health

location-event-service (8301)

Kafka 생산/소비(주제: location-events)

헬스: /actuator/health

trigger-engine-service (8401)

스케줄러가 룰 주기로 notifications 토픽으로 이벤트 발행

헬스: /actuator/health

notification-service (8501)

notifications 컨슘, 실패 시 notifications.dlt로 라우팅

헬스: /actuator/health