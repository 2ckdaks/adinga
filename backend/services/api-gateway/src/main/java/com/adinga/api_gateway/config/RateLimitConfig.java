package com.adinga.api_gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;
import java.net.InetSocketAddress;

@Configuration
public class RateLimitConfig {
    @Bean("ipKeyResolver")
    public KeyResolver ipKeyResolver() {
        return exchange -> {
            InetSocketAddress addr = exchange.getRequest().getRemoteAddress();
            String ip = (addr != null && addr.getAddress() != null)
                    ? addr.getAddress().getHostAddress()
                    : "unknown";
            return Mono.just(ip);
        };
    }
}
