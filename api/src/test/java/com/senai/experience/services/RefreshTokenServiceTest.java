package com.senai.experience.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.senai.experience.entities.RefreshToken;
import com.senai.experience.repositories.RefreshTokenRepository;
import com.senai.experience.services.RefreshTokenService.ResultadoRotacao;

/**
 * Testes unitários de {@link RefreshTokenService} (S-Card 1): emissão, rotação,
 * reuso/expiração (401) e revogação idempotente. Usa repositório mockado.
 */
@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository repository;

    private RefreshTokenService novoService() {
        return new RefreshTokenService(repository, 7L);
    }

    @Test
    void emitirGeraTokenDiferenteCadaVezEPersisteHashNaoTextoPuro() {
        RefreshTokenService service = novoService();
        when(repository.save(any(RefreshToken.class))).thenAnswer(inv -> inv.getArgument(0));

        String t1 = service.emitir(1L);
        String t2 = service.emitir(1L);

        assertThat(t1).isNotBlank();
        assertThat(t2).isNotBlank();
        assertThat(t1).isNotEqualTo(t2);

        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(repository, org.mockito.Mockito.times(2)).save(captor.capture());
        RefreshToken salvo = captor.getAllValues().get(0);
        // O hash SHA-256 hex tem 64 chars e nunca é o texto puro.
        assertThat(salvo.getTokenHash()).hasSize(64);
        assertThat(salvo.getTokenHash()).isNotEqualTo(t1);
        assertThat(salvo.isRevogado()).isFalse();
        assertThat(salvo.getUsuarioId()).isEqualTo(1L);
        assertThat(salvo.getDataExpiracao()).isAfter(Instant.now());
    }

    @Test
    void rotacionarRevogaOAntigoEEmiteNovo() {
        RefreshTokenService service = novoService();
        // emitir para descobrir o token puro e o hash correspondente
        when(repository.save(any(RefreshToken.class))).thenAnswer(inv -> inv.getArgument(0));
        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
        String tokenPuro = service.emitir(42L);
        verify(repository).save(captor.capture());
        String hash = captor.getValue().getTokenHash();

        RefreshToken existente = new RefreshToken();
        existente.setTokenHash(hash);
        existente.setUsuarioId(42L);
        existente.setRevogado(false);
        existente.setDataCriacao(Instant.now());
        existente.setDataExpiracao(Instant.now().plus(7, ChronoUnit.DAYS));
        when(repository.findByTokenHash(hash)).thenReturn(Optional.of(existente));

        ResultadoRotacao r = service.rotacionar(tokenPuro);

        assertThat(r.usuarioId()).isEqualTo(42L);
        assertThat(r.novoRefreshToken()).isNotBlank();
        assertThat(r.novoRefreshToken()).isNotEqualTo(tokenPuro);
        assertThat(existente.isRevogado()).isTrue();
    }

    @Test
    void rotacionarTokenRevogadoLancaInvalido() {
        RefreshTokenService service = novoService();
        RefreshToken revogado = new RefreshToken();
        revogado.setUsuarioId(1L);
        revogado.setRevogado(true);
        revogado.setDataExpiracao(Instant.now().plus(7, ChronoUnit.DAYS));
        when(repository.findByTokenHash(anyString())).thenReturn(Optional.of(revogado));

        assertThatThrownBy(() -> service.rotacionar("qualquer"))
                .isInstanceOf(RefreshTokenInvalidoException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void rotacionarTokenExpiradoLancaInvalido() {
        RefreshTokenService service = novoService();
        RefreshToken expirado = new RefreshToken();
        expirado.setUsuarioId(1L);
        expirado.setRevogado(false);
        expirado.setDataExpiracao(Instant.now().minus(1, ChronoUnit.DAYS));
        when(repository.findByTokenHash(anyString())).thenReturn(Optional.of(expirado));

        assertThatThrownBy(() -> service.rotacionar("qualquer"))
                .isInstanceOf(RefreshTokenInvalidoException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void rotacionarTokenInexistenteLancaInvalido() {
        RefreshTokenService service = novoService();
        when(repository.findByTokenHash(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.rotacionar("qualquer"))
                .isInstanceOf(RefreshTokenInvalidoException.class);
    }

    @Test
    void revogarMarcaRevogadoQuandoPresente() {
        RefreshTokenService service = novoService();
        RefreshToken ativo = new RefreshToken();
        ativo.setRevogado(false);
        when(repository.findByTokenHash(anyString())).thenReturn(Optional.of(ativo));

        service.revogar("algum-token");

        assertThat(ativo.isRevogado()).isTrue();
        verify(repository).save(ativo);
    }

    @Test
    void revogarIdempotenteQuandoAusenteOuJaRevogado() {
        RefreshTokenService service = novoService();
        when(repository.findByTokenHash(anyString())).thenReturn(Optional.empty());

        // ausente: não lança, não salva
        service.revogar("inexistente");
        verify(repository, never()).save(any());

        // já revogado: não salva de novo
        RefreshToken jaRevogado = new RefreshToken();
        jaRevogado.setRevogado(true);
        when(repository.findByTokenHash(anyString())).thenReturn(Optional.of(jaRevogado));
        service.revogar("ja-revogado");
        verify(repository, never()).save(any());
    }

    @Test
    void revogarTokenNuloOuVazioNaoFazLookup() {
        RefreshTokenService service = novoService();

        service.revogar(null);
        service.revogar("  ");

        verify(repository, never()).findByTokenHash(anyString());
    }
}
