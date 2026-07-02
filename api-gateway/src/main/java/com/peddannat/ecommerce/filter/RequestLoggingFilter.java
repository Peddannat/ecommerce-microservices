package com.peddannat.ecommerce.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Logs every incoming request method and path.
 * Runs after JwtAuthFilter (order 0 vs -1).
 * Useful for debugging and production monitoring.
 */
@Component
public class RequestLoggingFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String method = exchange.getRequest().getMethod().name();
        String path = exchange.getRequest().getURI().getPath();

        log.info("Incoming Request → [{} {}]", method, path);

        return chain.filter(exchange).doFinally(signal ->
                log.info("Completed Request → [{} {}] Signal: {}", method, path, signal)
        );
    }

    @Override
    public int getOrder() {
        return 0;
    }
}