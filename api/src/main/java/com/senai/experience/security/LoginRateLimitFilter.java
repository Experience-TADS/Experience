package com.senai.experience.security;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class LoginRateLimitFilter extends OncePerRequestFilter {

    private static final String LOGIN_PATH = "/api/usuario/login";

    private final int maxTentativas;
    private final int janelaMinutos;

    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    public LoginRateLimitFilter(
            @Value("${ratelimit.login.max-tentativas:5}") int maxTentativas,
            @Value("${ratelimit.login.janela-minutos:10}") int janelaMinutos) {
        this.maxTentativas = maxTentativas;
        this.janelaMinutos = janelaMinutos;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Só limita POST /api/usuario/login; todo o resto passa direto.
        if (!"POST".equalsIgnoreCase(request.getMethod())
                || !LOGIN_PATH.equals(request.getServletPath())) {
            filterChain.doFilter(request, response);
            return;
        }

        String ip = resolverIp(request);
        Bucket bucket = buckets.computeIfAbsent(ip, k -> novoBucket());

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
            return;
        }

        response.setStatus(429);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(
                "{\"error\":\"Muitas tentativas de login. Tente novamente mais tarde.\"}");
    }

    private Bucket novoBucket() {
        Bandwidth limite = Bandwidth.builder()
                .capacity(maxTentativas)
                .refillGreedy(maxTentativas, Duration.ofMinutes(janelaMinutos))
                .build();
        return Bucket.builder()
                .addLimit(limite)
                .build();
    }


    private String resolverIp(HttpServletRequest request) {
        if (request == null) {
            return "desconhecido";
        }
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
