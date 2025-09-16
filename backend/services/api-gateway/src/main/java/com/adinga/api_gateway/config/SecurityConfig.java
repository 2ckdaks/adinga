package com.adinga.api_gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    // 일반 라우트(게이트웨이 경유 요청)는 전부 허용
//    @Bean
//    @Order(1)
//    public SecurityWebFilterChain appSecurity(ServerHttpSecurity http) {
//        return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
//                .authorizeExchange(ex -> ex
//                        // Swagger
//                        .pathMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/api/*/v3/api-docs/**").permitAll()
//                        // 헬스/인포만 공개
//                        .pathMatchers("/actuator/health", "/actuator/health/**", "/actuator/info").permitAll()
//                        // 나머지는 여기선 판단하지 않음(Actuator 전용 필터가 체크)
//                        .anyExchange().permitAll()
//                )
//                .build();
//    }
    @Bean
    @Order(1)
    SecurityWebFilterChain security(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(ex -> ex
                        .pathMatchers(
                                "/actuator/health", "/actuator/health/**", "/actuator/info",
                                "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**"
                        ).permitAll()
                        .anyExchange().authenticated()
                )
                .build();
    }
}

/**
 * Actuator 전용 베어러 토큰 가드(WebFilter) — Actuator 체인에도 확실히 적용됨.
 * - /actuator/health, /actuator/info 은 통과
 * - 그 외 /actuator/** 는 Authorization: Bearer {token} 없으면 401
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE) // 보안 필터 체인의 맨 앞에서 확인
class ActuatorBearerGuard implements WebFilter {

    private final String token;

    ActuatorBearerGuard(@Value("${security.dev-token:}") String token) {
        this.token = token;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest req = exchange.getRequest();
        String path = req.getURI().getPath();

        if (!path.startsWith("/actuator/")) {
            return chain.filter(exchange); // actuator가 아니면 패스
        }
        // 공개 허용 경로
        if (path.equals("/actuator/health") || path.startsWith("/actuator/health/")
                || path.equals("/actuator/info")) {
            return chain.filter(exchange);
        }

        String auth = req.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (token != null && !token.isBlank() && ("Bearer " + token).equals(auth)) {
            return chain.filter(exchange);
        }

        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
}