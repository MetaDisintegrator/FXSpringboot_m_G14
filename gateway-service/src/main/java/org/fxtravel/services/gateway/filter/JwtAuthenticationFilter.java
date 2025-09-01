package org.fxtravel.services.gateway.filter;

import org.fxtravel.services.gateway.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // 登录接口、注册接口放行
        if (path.startsWith("/api/auth/login") || path.startsWith("/api/auth/register") || path.startsWith("/api/auth/logout")) {
            return chain.filter(exchange);
        }

        String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (token == null || !token.startsWith("Bearer ")) {
            System.out.println("Missing or invalid Authorization header");
            return unauthorized(exchange);
        }

        token = token.substring(7);
        try {
            // 只从 Token 中获取 userId
            String userId = jwtUtil.getUserIdFromToken(token);

            // 注入用户信息到请求头
            ServerHttpRequest request = exchange.getRequest().mutate()
                    .header("X-User-Id", userId)
                    .build();

            return chain.filter(exchange.mutate().request(request).build());
        } catch (Exception e) {
            System.out.println("Invalid token: " + e.getMessage());
            return unauthorized(exchange);
        }
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return -1; // 过滤器优先级
    }
}
