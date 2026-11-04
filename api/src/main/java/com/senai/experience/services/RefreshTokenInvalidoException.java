package com.senai.experience.services;

/**
 * Lançada quando um refresh token apresentado é inexistente, revogado ou expirado.
 *
 * <p>Deve ser capturada dentro do {@code AuthController} e traduzida para 401 — nunca
 * deixar propagar para o {@code GlobalHandlerException} (que converteria em 404).</p>
 */
public class RefreshTokenInvalidoException extends RuntimeException {
    public RefreshTokenInvalidoException(String message) {
        super(message);
    }
}
