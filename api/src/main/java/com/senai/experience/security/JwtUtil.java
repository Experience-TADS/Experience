package com.senai.experience.security;

import java.security.Key;
import java.util.Date;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    private static Key key;

    // Access token de curta duração (S-Card 1). Default 15 min, configurável via
    // jwt.access-token.ttl-minutos (env JWT_ACCESS_TOKEN_TTL_MINUTOS).
    private static long accessTtlMillis = 15 * 60 * 1000L; // default 15 min

    @Value("${jwt.secret}")
    public void setSecret(String secret) {
        key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    @Value("${jwt.access-token.ttl-minutos:15}")
    public void setAccessTtlMinutos(long minutos) {
        accessTtlMillis = minutos * 60_000L;
    }

    // Expõe a chave de assinatura para validação de tokens em outras classes
    public static Key getKey() {
        return key;
    }

    // Gera token com email no subject, role e userId como claims
    public static String generateToken(String email, String role, Long userId) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .claim("userId", userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTtlMillis))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Retorna o email (subject) do token
    public static String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Retorna o role gravado no claim "role"
    public static String extractRole(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
    }

    // Retorna o userId gravado no claim "userId"
    public static Long extractUserId(String token) {
        Object userId = Jwts.parserBuilder()
                .setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody()
                .get("userId");
        if (userId instanceof Integer) return ((Integer) userId).longValue();
        if (userId instanceof Long) return (Long) userId;
        return null;
    }

    public static boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}
