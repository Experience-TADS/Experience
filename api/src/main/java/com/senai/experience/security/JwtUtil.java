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

    private static final long EXPIRATION_TIME = 86400000; // 24h

    @Value("${jwt.secret}")
    public void setSecret(String secret) {
        key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
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
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
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
