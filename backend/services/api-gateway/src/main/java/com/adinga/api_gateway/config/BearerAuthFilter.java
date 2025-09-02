package com.adinga.api_gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;
import org.springframework.http.server.PathContainer;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Component
public class BearerAuthFilter implements GlobalFilter, Ordered {

    private final boolean enabled;
    private final String token;
    private final List<PathPattern> whitelist;
    private final PathPatternParser parser = PathPatternParser.defaultInstance;

    public BearerAuthFilter(
            @Value("${security.enabled:false}") boolean enabled,
            @Value("${security.dev-token:}") String token,
            @Value("${security.whitelist:/actuator/**,/api/*/actuator/**}") String whitelistCsv
    ) {
        this.enabled = enabled;
        this.token = token;
        this.whitelist = Arrays.stream(whitelistCsv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(parser::parse)
                .toList();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!enabled) return chain.filter(exchange);

        String path = exchange.getRequest().getURI().getPath();
        PathContainer container = PathContainer.parsePath(path);
        for (PathPattern p : whitelist) {
            if (p.matches(container)) return chain.filter(exchange);
        }

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

    /** 실행 순서(작을수록 먼저) */
    @Override public int getOrder() { return -1; } // rate limiter보다 앞에서 막기
}
