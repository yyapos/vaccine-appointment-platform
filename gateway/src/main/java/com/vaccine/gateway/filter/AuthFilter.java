package com.vaccine.gateway.filter;

import com.vaccine.gateway.util.GatewayJwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * 网关认证过滤器
 * 统一验证token有效性
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthFilter implements GlobalFilter, Ordered {

    private final GatewayJwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        // 登录接口不需要验证token
        if (path.startsWith("/api/auth/")) {
            return chain.filter(exchange);
        }

        // 后台管理系统路径 /api/admin/** 需要验证token
        // 用户小程序路径 /api/app/** 需要验证token
        boolean needAuth = path.startsWith("/api/admin/") || path.startsWith("/api/app/");

        if (!needAuth) {
            return chain.filter(exchange);
        }

        // 从请求头获取token
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("未提供认证token - 路径: {}", path);
            return unauthorized(exchange.getResponse(), "未提供认证token");
        }

        // 去掉 "Bearer " 前缀
        String token = authHeader.substring(7);

        try {
            // 验证token是否有效
            jwtUtil.getUserIdFromToken(token);
            log.info("token验证通过 - 路径: {}", path);
            return chain.filter(exchange);
        } catch (Exception e) {
            log.warn("token无效或已过期 - 路径: {}, 错误: {}", path, e.getMessage());
            return unauthorized(exchange.getResponse(), "token无效或已过期");
        }
    }

    /**
     * 返回401未授权响应
     */
    private Mono<Void> unauthorized(ServerHttpResponse response, String message) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");

        String body = String.format("{\"code\":401,\"message\":\"%s\"}", message);
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);

        return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
    }

    @Override
    public int getOrder() {
        // 在日志过滤器之前执行
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}