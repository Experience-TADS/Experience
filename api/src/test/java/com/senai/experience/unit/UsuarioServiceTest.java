package com.senai.experience.unit;

import com.senai.experience.entities.Usuario;
import com.senai.experience.entities.role.UserRole;
import com.senai.experience.repositories.UsuarioRepository;
import com.senai.experience.services.UsuarioService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para {@link UsuarioService}.
 *
 * <p>Cobre {@code findByEmail} e {@code login} após a refatoração do repositório
 * para {@code Optional<Usuario>}, garantindo ausência de NPE em todos os fluxos.</p>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UsuarioService — testes unitários")
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuarioAtivo;

    @BeforeEach
    void setUp() {
        usuarioAtivo = new Usuario(
                "Ana Souza",
                "ana@email.com",
                "hash_da_senha",
                LocalDate.of(1990, 3, 15),
                UserRole.CLIENTE
        );
        usuarioAtivo.setId(1L);
        usuarioAtivo.setAtivo(true);   // garante estado inicial explícito
    }

    // ── findByEmail ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("findByEmail retorna o usuário quando e-mail existe")
    void findByEmail_emailExistente_retornaUsuario() {
        when(usuarioRepository.findByEmail("ana@email.com"))
                .thenReturn(Optional.of(usuarioAtivo));

        Usuario resultado = usuarioService.findByEmail("ana@email.com");

        assertThat(resultado).isNotNull();
        assertThat(resultado.getEmail()).isEqualTo("ana@email.com");
        verify(usuarioRepository).findByEmail("ana@email.com");
    }

    @Test
    @DisplayName("findByEmail retorna null quando e-mail não existe — sem NPE")
    void findByEmail_emailInexistente_retornaNull() {
        when(usuarioRepository.findByEmail("inexistente@email.com"))
                .thenReturn(Optional.empty());

        Usuario resultado = usuarioService.findByEmail("inexistente@email.com");

        assertThat(resultado).isNull();
        verify(usuarioRepository).findByEmail("inexistente@email.com");
    }

    // ── login ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("login com credenciais válidas e conta ativa retorna o usuário")
    void login_credenciaisValidasAtivo_retornaUsuario() {
        when(usuarioRepository.findByEmail("ana@email.com"))
                .thenReturn(Optional.of(usuarioAtivo));
        when(passwordEncoder.matches("senha123", "hash_da_senha"))
                .thenReturn(true);

        Usuario resultado = usuarioService.login("ana@email.com", "senha123");

        assertThat(resultado).isNotNull();
        assertThat(resultado.getEmail()).isEqualTo("ana@email.com");
    }

    @Test
    @DisplayName("login com senha incorreta retorna null")
    void login_senhaIncorreta_retornaNull() {
        when(usuarioRepository.findByEmail("ana@email.com"))
                .thenReturn(Optional.of(usuarioAtivo));
        when(passwordEncoder.matches("senhaErrada", "hash_da_senha"))
                .thenReturn(false);

        Usuario resultado = usuarioService.login("ana@email.com", "senhaErrada");

        assertThat(resultado).isNull();
    }

    @Test
    @DisplayName("login com e-mail inexistente retorna null — sem NPE")
    void login_emailInexistente_retornaNull() {
        when(usuarioRepository.findByEmail("naoexiste@email.com"))
                .thenReturn(Optional.empty());

        Usuario resultado = usuarioService.login("naoexiste@email.com", "qualquerSenha");

        assertThat(resultado).isNull();
        // passwordEncoder.matches NÃO deve ser chamado quando o usuário não existe
        verify(passwordEncoder, never()).matches(any(), any());
    }

    @Test
    @DisplayName("login com conta desativada retorna null")
    void login_contaDesativada_retornaNull() {
        // Cria uma instância separada para não contaminar os outros testes
        Usuario usuarioDesativado = new Usuario(
                "Ana Souza",
                "ana.inativa@email.com",
                "hash_da_senha",
                LocalDate.of(1990, 3, 15),
                UserRole.CLIENTE
        );
        usuarioDesativado.setId(2L);
        usuarioDesativado.setAtivo(false);

        when(usuarioRepository.findByEmail("ana.inativa@email.com"))
                .thenReturn(Optional.of(usuarioDesativado));
        // senha correta, mas conta está desativada
        when(passwordEncoder.matches("senha123", "hash_da_senha"))
                .thenReturn(true);

        Usuario resultado = usuarioService.login("ana.inativa@email.com", "senha123");

        assertThat(resultado).isNull();
    }
}
