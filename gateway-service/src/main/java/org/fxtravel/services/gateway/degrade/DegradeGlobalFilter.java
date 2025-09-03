package org.fxtravel.services.gateway.degrade;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class DegradeGlobalFilter implements GlobalFilter, Ordered {

    // 约定：/api/{service}/...
    private static final Pattern P = Pattern.compile("^/api/([^/]+)/.*");

    @Value("${GATEWAY_MODE:enabled}")     private String gatewayMode;
    @Value("${USER_MODE:enabled}")        private String userMode;
    @Value("${TRAIN_MODE:enabled}")       private String trainMode;
    @Value("${HOTEL_MODE:enabled}")       private String hotelMode;
    @Value("${DISABLE_RECOMMEND:false}")  private boolean disableRecommend;

    enum Mode { enabled, readonly, disabled;
        static Mode of(String v){ try { return Mode.valueOf(v.toLowerCase()); } catch(Exception e){ return enabled; } }
    }

    private Mode modeFor(String service){
        if (service == null) return Mode.of(gatewayMode);
        switch (service) {
            case "user":  return Mode.of(userMode);
            case "train": return Mode.of(trainMode);
            case "hotel": return Mode.of(hotelMode);
            default:      return Mode.of(gatewayMode);
        }
    }

    @Override public int getOrder() { return -100; } // 尽早拦截

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1) 全局关闭
        if (Mode.of(gatewayMode) == Mode.disabled) {
            return write(exchange, HttpStatus.SERVICE_UNAVAILABLE,
                    "{\"code\":503,\"msg\":\"Service temporarily unavailable\",\"reason\":\"gateway disabled\"}");
        }

        // 2) 解析 service
        String path = exchange.getRequest().getURI().getPath();
        Matcher m = P.matcher(path);
        String service = m.matches() ? m.group(1) : null;
        Mode mode = modeFor(service);

        // 3) 服务关闭
        if (mode == Mode.disabled) {
            return write(exchange, HttpStatus.SERVICE_UNAVAILABLE,
                    "{\"code\":503,\"msg\":\"Service temporarily unavailable\",\"reason\":\""+service+" disabled\"}");
        }

        // 4) 只读：拦截写操作
        if (mode == Mode.readonly) {
            HttpMethod method = exchange.getRequest().getMethod();
            if (method != null && !(method == HttpMethod.GET || method == HttpMethod.HEAD || method == HttpMethod.OPTIONS)) {
                return write(exchange, HttpStatus.LOCKED,
                        "{\"code\":423,\"msg\":\"Read-only mode\",\"reason\":\""+service+" readonly\"}");
            }
        }

        // 5) 关闭非核心功能（示例：推荐）
        if (disableRecommend && path.startsWith("/api/recommend")) {
            return write(exchange, HttpStatus.OK,
                    "{\"code\":0,\"data\":[],\"msg\":\"Feature disabled\",\"reason\":\"recommend disabled\"}");
        }

        return chain.filter(exchange);
    }

    private Mono<Void> write(ServerWebExchange exchange, HttpStatus status, String json) {
        var resp = exchange.getResponse();
        resp.setStatusCode(status);
        resp.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        var buf = resp.bufferFactory().wrap(json.getBytes(StandardCharsets.UTF_8));
        return resp.writeWith(Mono.just(buf));
    }
}
