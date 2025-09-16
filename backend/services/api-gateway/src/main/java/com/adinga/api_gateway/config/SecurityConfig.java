package com.adinga.api_gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    @Order(1)
    public SecurityWebFilterChain security(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .authorizeExchange(ex -> ex
                        // 게이트웨이 자체
                        .pathMatchers(
                                "/actuator/health", "/actuator/health/**", "/actuator/info",
                                "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**"
                        ).permitAll()

                        // 프록시 Swagger & OpenAPI & actuator 일부
                        .pathMatchers(
                                "/api/*/swagger-ui.html",
                                "/api/*/swagger-ui/**",
                                "/api/*/v3/api-docs/**",
                                "/api/*/webjars/**",
                                "/api/*/actuator/health",
                                "/api/*/actuator/health/**",
                                "/api/*/actuator/info"
                        ).permitAll()

                        .anyExchange().authenticated()
                )
                .build();
    }
}

/**
 * /actuator/* 전용 토큰 가드 (게이트웨이 자신의 actuator 보호)
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
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
            return chain.filter(exchange); // actuator가 아니면 통과
        }
        if (path.equals("/actuator/health") || path.startsWith("/actuator/health/")
                || path.equals("/actuator/info")) {
            return chain.filter(exchange); // health/info는 공개
        }

        String auth = req.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (token != null && !token.isBlank() && ("Bearer " + token).equals(auth)) {
            return chain.filter(exchange); // 토큰 일치 시 통과
        }

        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
}