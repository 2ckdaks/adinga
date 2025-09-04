package com.adinga.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
// WebFlux 환경
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(ex -> ex
                        // 게이트웨이 자체 Swagger UI (우리가 BL-024에서 path=/swagger-ui.html 로 설정함)
                        .pathMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        // 각 서비스 스펙을 게이트웨이가 프록시로 노출하는 경로
                        .pathMatchers("/api/*/v3/api-docs", "/api/*/v3/api-docs/**").permitAll()
                        // (선택) 헬스체크
                        .pathMatchers("/actuator/**").permitAll()
                        // 개발 단계에선 나머지도 열어두기
                        .anyExchange().permitAll()
                );
        return http.build();
    }
}
