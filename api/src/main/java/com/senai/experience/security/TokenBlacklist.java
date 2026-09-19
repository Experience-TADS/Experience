package com.senai.experience.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Blacklist de tokens JWT em memória — S-012.
 *
 * Critérios atendidos:
 * - Token adicionado à blacklist após logout
 * - Requisições com token na lista negra retornam 401
 * - Blacklist limpa automaticamente após expiração do token (a cada 30 min)
 */
@Component
public class TokenBlacklist {

    // token → data de expiração original
    private final Map<String, Date> blacklist = new ConcurrentHashMap<>();

    /** Invalida um token, registrando sua expiração para limpeza posterior. */
    public void invalidate(String token) {
        Date expiration = extractExpiration(token);
        blacklist.put(token, expiration != null
                ? expiration
                : new Date(System.currentTimeMillis() + 86_400_000L)); // fallback: 24h
    }

    /** Retorna true se o token está na lista negra. */
    public boolean isBlacklisted(String token) {
        return blacklist.containsKey(token);
    }

    /** Remove entradas já expiradas — executa a cada 30 minutos. */
    @Scheduled(fixedDelay = 1_800_000)
    public void removeExpiredTokens() {
        Date now = new Date();
        blacklist.entrySet().removeIf(entry -> entry.getValue().before(now));
    }

    private Date extractExpiration(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(JwtUtil.getKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getExpiration();
        } catch (Exception e) {
            return null;
        }
    }
}
