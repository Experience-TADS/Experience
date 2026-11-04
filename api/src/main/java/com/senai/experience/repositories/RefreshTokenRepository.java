package com.senai.experience.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.senai.experience.entities.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    // Ponto de extensão para revogação em cascata da família (backlog — fora de escopo do S-Card 1).
    List<RefreshToken> findByUsuarioIdAndRevogadoFalse(Long usuarioId);
}
