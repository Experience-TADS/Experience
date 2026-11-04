package com.senai.experience.entities;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Refresh token opaco persistido (S-Card 1).
 *
 * <p>Guarda apenas o hash SHA-256 do token (nunca o texto puro). A fonte da verdade
 * para revogação/rotação é a flag {@code revogado}. Usa {@code usuarioId} solto (não
 * {@code @ManyToOne}) porque {@code Usuario} é {@code @Inheritance(JOINED)} e o join
 * da hierarquia seria desnecessário em cada lookup.</p>
 */
@Entity
@Table(name = "refresh_token")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 64)
    private String tokenHash;

    @Column(nullable = false)
    private Long usuarioId;

    @Column(nullable = false)
    private Instant dataExpiracao;

    @Column(nullable = false)
    private boolean revogado;

    @Column(nullable = false)
    private Instant dataCriacao;
}
