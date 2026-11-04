package com.senai.experience.unit;

import com.senai.experience.entities.Usuario;
import com.senai.experience.repositories.UsuarioRepository;
import com.senai.experience.services.UsuarioService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario(
                "João Silva",
                "joao@example.com",
                "$2a$10$hashedPassword",
                LocalDate.of(1990, 1, 1)
        );
        usuario.setAtivo(true);
    }

    // ── findByEmail ───────────────────────────────────────────────────────────

    @Test
    void findByEmailDeveRetornarUsuarioQuandoExistir() {
        when(usuarioRepository.findByEmail("joao@example.com"))
                .thenReturn(Optional.of(usuario));

        Usuario resultado = usuarioService.findByEmail("joao@example.com");

        assertThat(resultado).isNotNull();
        assertThat(resultado.getEmail()).isEqualTo("joao@example.com");
    }

    @Test
    void findByEmailDeveRetornarNullQuandoEmailNaoExistir() {
        when(usuarioRepository.findByEmail("naoexiste@example.com"))
                .thenReturn(Optional.empty());

        Usuario resultado = usuarioService.findByEmail("naoexiste@example.com");

        assertThat(resultado).isNull();
    }

    // ── login ─────────────────────────────────────────────────────────────────

    @Test
    void loginDeveRetornarUsuarioComCredenciaisValidas() {
        when(usuarioRepository.findByEmail("joao@example.com"))
                .thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senha123", usuario.getSenhaHash()))
                .thenReturn(true);

        Usuario resultado = usuarioService.login("joao@example.com", "senha123");

        assertThat(resultado).isNotNull();
        assertThat(resultado.getEmail()).isEqualTo("joao@example.com");
    }

    @Test
    void loginDeveRetornarNullQuandoEmailNaoExistir() {
        when(usuarioRepository.findByEmail("naoexiste@example.com"))
                .thenReturn(Optional.empty());

        Usuario resultado = usuarioService.login("naoexiste@example.com", "qualquersenha");

        assertThat(resultado).isNull();
    }

    @Test
    void loginDeveRetornarNullQuandoSenhaErrada() {
        when(usuarioRepository.findByEmail("joao@example.com"))
                .thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senhaErrada", usuario.getSenhaHash()))
                .thenReturn(false);

        Usuario resultado = usuarioService.login("joao@example.com", "senhaErrada");

        assertThat(resultado).isNull();
    }

    @Test
    void loginDeveRetornarNullQuandoUsuarioInativo() {
        usuario.setAtivo(false);
        when(usuarioRepository.findByEmail("joao@example.com"))
                .thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senha123", usuario.getSenhaHash()))
                .thenReturn(true);

        Usuario resultado = usuarioService.login("joao@example.com", "senha123");

        assertThat(resultado).isNull();
    }
}
