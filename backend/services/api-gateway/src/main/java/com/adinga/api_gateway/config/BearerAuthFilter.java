package com.adinga.api_gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Component
public class BearerAuthFilter implements GlobalFilter, Ordered {

    private static final AntPathMatcher MATCHER = new AntPathMatcher();

    private final boolean enabled;
    private final String token;
    private final List<String> whitelist;

    public BearerAuthFilter(
            @Value("${security.enabled:false}") boolean enabled,
            @Value("${security.dev-token:}") String token,
            @Value("${security.whitelist:}") String whitelistCsv
    ) {
        this.enabled = enabled;
        this.token = token;

        // Swagger / Actuator / 각 서비스 v3 api-docs 기본 허용 패턴
        var defaults = List.of(
                "/swagger-ui.html",
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/api/*/v3/api-docs",
                "/api/*/v3/api-docs/**",
                "/api/**/v3/api-docs",
                "/api/**/v3/api-docs/**",
                "/actuator/**",
                "/api/*/actuator/**"
        );
        var fromProp = Arrays.stream(whitelistCsv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
        this.whitelist = Stream.concat(defaults.stream(), fromProp.stream()).toList();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!enabled) return chain.filter(exchange);

        String path = exchange.getRequest().getURI().getPath();

        // 화이트리스트면 통과
        if (whitelist.stream().anyMatch(p -> MATCHER.match(p, path))) {
            return chain.filter(exchange);
        }

        // Bearer dev 토큰 검사
        String auth = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(token) && ("Bearer " + token).equals(auth)) {
            return chain.filter(exchange);
        }

        var res = exchange.getResponse();
        res.setStatusCode(HttpStatus.UNAUTHORIZED);
        res.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        byte[] body = "{\"error\":\"unauthorized\"}".getBytes(StandardCharsets.UTF_8);
        return res.writeWith(Mono.just(res.bufferFactory().wrap(body)));
    }

    @Override public int getOrder() { return -1; } // rate limiter보다 앞
}