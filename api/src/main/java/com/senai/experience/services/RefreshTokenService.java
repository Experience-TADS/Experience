package com.senai.experience.services;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.senai.experience.entities.RefreshToken;
import com.senai.experience.repositories.RefreshTokenRepository;

/**
 * Emissão, rotação e revogação de refresh tokens opacos (S-Card 1).
 *
 * <p>O token entregue ao cliente é texto puro aleatório (32 bytes, Base64 URL-safe);
 * no banco guardamos apenas o hash SHA-256 hex. A rotação revoga o token apresentado
 * e emite um novo par na mesma transação, garantindo que o antigo só é invalidado se
 * um substituto for persistido com sucesso.</p>
 */
@Service
public class RefreshTokenService {

    public record ResultadoRotacao(Long usuarioId, String novoRefreshToken) {}

    private static final SecureRandom RNG = new SecureRandom();

    private final RefreshTokenRepository repository;
    private final long ttlDias;

    public RefreshTokenService(RefreshTokenRepository repository,
                               @Value("${jwt.refresh-token.ttl-dias:7}") long ttlDias) {
        this.repository = repository;
        this.ttlDias = ttlDias;
    }

    /**
     * Gera um refresh token opaco novo, persiste o hash e devolve o TEXTO PURO ao chamador.
     */
    public String emitir(Long usuarioId) {
        String tokenPuro = gerarTokenOpaco();
        Instant agora = Instant.now();
        RefreshToken rt = new RefreshToken();
        rt.setTokenHash(hash(tokenPuro));
        rt.setUsuarioId(usuarioId);
        rt.setDataCriacao(agora);
        rt.setDataExpiracao(agora.plus(ttlDias, ChronoUnit.DAYS));
        rt.setRevogado(false);
        repository.save(rt);
        return tokenPuro;
    }

    /**
     * Valida o token apresentado; se válido, revoga o atual (rotação) e emite um novo par.
     *
     * @throws RefreshTokenInvalidoException se o token for inexistente, revogado ou expirado.
     */
    @Transactional
    public ResultadoRotacao rotacionar(String tokenPuro) {
        RefreshToken atual = repository.findByTokenHash(hash(tokenPuro))
                .orElseThrow(() -> new RefreshTokenInvalidoException("Refresh token inválido."));
        if (atual.isRevogado()) {
            throw new RefreshTokenInvalidoException("Refresh token revogado.");
        }
        if (atual.getDataExpiracao().isBefore(Instant.now())) {
            throw new RefreshTokenInvalidoException("Refresh token expirado.");
        }
        atual.setRevogado(true);
        repository.save(atual);
        String novo = emitir(atual.getUsuarioId());
        return new ResultadoRotacao(atual.getUsuarioId(), novo);
    }

    /**
     * Revoga o refresh token apresentado. Idempotente: não lança se ausente ou já revogado.
     */
    @Transactional
    public void revogar(String tokenPuro) {
        if (tokenPuro == null || tokenPuro.isBlank()) {
            return;
        }
        repository.findByTokenHash(hash(tokenPuro)).ifPresent(rt -> {
            if (!rt.isRevogado()) {
                rt.setRevogado(true);
                repository.save(rt);
            }
        });
    }

    private String gerarTokenOpaco() {
        byte[] b = new byte[32];
        RNG.nextBytes(b);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(b);
    }

    private String hash(String tokenPuro) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] dig = md.digest(tokenPuro.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(64);
            for (byte x : dig) {
                sb.append(String.format("%02x", x));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 é garantido na JVM padrão; falha aqui é erro de programação.
            throw new IllegalStateException("Algoritmo SHA-256 indisponível", e);
        }
    }
}
