package com.adinga.location_event_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 불필요한 기본 보안 UI/팝업 끔
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)

                // 공개 라우트
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                // Actuator (공개)
                                "/actuator/health", "/actuator/info",
                                // Swagger & OpenAPI
                                "/swagger-ui.html", "/swagger-ui/**",
                                "/v3/api-docs/**", "/v3/api-docs/swagger-config",
                                "/webjars/**"
                        ).permitAll()
                        .anyRequest().permitAll()
                );

        return http.build();
    }
}
