package org.fxtravel.services.gateway.conf;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoutesConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // 发现服务路由（Eureka Dashboard）
                .route("discover-service", r -> r.path("/eureka/**")
                        .uri("lb://discover-service"))
                .build();
    }
}
