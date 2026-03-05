package com.tableorder.customer.auth;

import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(1)
public class JwtAuthFilter implements Filter {

    private final JwtProvider jwtProvider;

    public JwtAuthFilter(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String path = req.getRequestURI();

        // 인증 불필요 경로
        if (path.startsWith("/api/auth/") || path.startsWith("/h2-console")
                || "OPTIONS".equalsIgnoreCase(req.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        // /api/** 경로만 인증 적용
        if (!path.startsWith("/api/")) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = req.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendError(res, 401, "INVALID_TOKEN", "유효하지 않은 토큰입니다");
            return;
        }

        try {
            Claims claims = jwtProvider.parseToken(authHeader.substring(7));
            req.setAttribute("claims", claims);
            req.setAttribute("storeId", claims.get("storeId", Long.class));
            req.setAttribute("tableId", claims.get("tableId", Long.class));
            req.setAttribute("tableNumber", claims.get("tableNumber", Integer.class));
            req.setAttribute("sessionId", claims.get("sessionId", String.class));
            req.setAttribute("role", claims.get("role", String.class));
            chain.doFilter(request, response);
        } catch (Exception e) {
            sendError(res, 401, "INVALID_TOKEN", "유효하지 않은 토큰입니다");
        }
    }

    private void sendError(HttpServletResponse res, int status, String code, String message) throws IOException {
        res.setStatus(status);
        res.setContentType("application/json;charset=UTF-8");
        res.getWriter().write(
                "{\"status\":%d,\"code\":\"%s\",\"message\":\"%s\"}".formatted(status, code, message));
    }
}
