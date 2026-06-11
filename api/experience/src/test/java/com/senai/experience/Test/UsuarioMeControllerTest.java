package com.senai.experience.Test;

import com.senai.experience.config.PostgresTestContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes complementares para UsuarioController:
 * GET /api/usuario/{id}, PUT /api/usuario/{id}, DELETE /api/usuario/{id}, GET /api/usuario/me
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UsuarioMeControllerTest extends PostgresTestContainer {

    @Autowired MockMvc mockMvc;

    private String tokenAdmin;
    private Long idAdmin;

    @BeforeEach
    void setup() throws Exception {
        tokenAdmin = AuthHelper.obterToken(mockMvc, "admin.me@test.com", "senha123", "ADMIN");
        idAdmin    = buscarIdAdmin();
    }

    // ── GET /api/usuario ──────────────────────────────────────────────────────

    @Test
    void getUsuariosComTokenDeveRetornar200() throws Exception {
        mockMvc.perform(get("/api/usuario")
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    // ── GET /api/usuario/{id} ─────────────────────────────────────────────────

    @Test
    void getUsuarioPorIdExistenteDeveRetornar200() throws Exception {
        mockMvc.perform(get("/api/usuario/" + idAdmin)
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("admin.me@test.com"));
    }

    @Test
    void getUsuarioPorIdInexistenteDeveRetornar404() throws Exception {
        mockMvc.perform(get("/api/usuario/9999")
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isNotFound());
    }

    // ── PUT /api/usuario/{id} ─────────────────────────────────────────────────

    @Test
    void putUsuarioComTokenDeveRetornar200() throws Exception {
        mockMvc.perform(put("/api/usuario/" + idAdmin)
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Admin Atualizado",
                                  "email": "admin.me@test.com",
                                  "senha": "novaSenha123",
                                  "dataNascimento": "1990-01-01",
                                  "role": "ADMIN"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Admin Atualizado"));
    }

    @Test
    void putUsuarioInexistenteDeveRetornar404() throws Exception {
        mockMvc.perform(put("/api/usuario/9999")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Ninguém",
                                  "email": "ninguem@test.com",
                                  "senha": "senha123",
                                  "dataNascimento": "1990-01-01",
                                  "role": "ADMIN"
                                }
                                """))
                .andExpect(status().isNotFound());
    }

    // ── DELETE /api/usuario/{id} ──────────────────────────────────────────────

    @Test
    void deleteUsuarioComTokenDeveRetornar204() throws Exception {
        // Cria um usuário extra para deletar
        String tokenExtra = AuthHelper.obterToken(mockMvc, "extra.delete@test.com", "senha123", "CLIENTE");
        Long idExtra = buscarIdPorEmail("extra.delete@test.com");

        mockMvc.perform(delete("/api/usuario/" + idExtra)
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUsuarioSemTokenDeveRetornar403() throws Exception {
        mockMvc.perform(delete("/api/usuario/1"))
                .andExpect(status().isForbidden());
    }

    // ── GET /api/usuario/me ───────────────────────────────────────────────────

    @Test
    void getMeComTokenValidoDeveRetornarDadosDoUsuario() throws Exception {
        mockMvc.perform(get("/api/usuario/me")
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("admin.me@test.com"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    void getMeSemTokenDeveRetornar400() throws Exception {
        mockMvc.perform(get("/api/usuario/me"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getMeComTokenInvalidoDeveRetornar401() throws Exception {
        mockMvc.perform(get("/api/usuario/me")
                        .header("Authorization", "Bearer token.invalido.aqui"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getMeNaoExpoeSenhaHash() throws Exception {
        mockMvc.perform(get("/api/usuario/me")
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.senhaHash").doesNotExist())
                .andExpect(jsonPath("$.senha").doesNotExist());
    }

    // ── PATCH /api/usuario/{id}/ativar ────────────────────────────────────────

    @Test
    void patchAtivarComAdminDeveRetornar200() throws Exception {
        mockMvc.perform(patch("/api/usuario/" + idAdmin + "/ativar")
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk());
    }

    @Test
    void patchAtivarUsuarioInexistenteDeveRetornar404() throws Exception {
        mockMvc.perform(patch("/api/usuario/9999/ativar")
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isNotFound());
    }

    @Test
    void patchAtivarSemTokenDeveRetornar403() throws Exception {
        mockMvc.perform(patch("/api/usuario/1/ativar"))
                .andExpect(status().isForbidden());
    }

    // ── PATCH /api/usuario/{id}/desativar ─────────────────────────────────────

    @Test
    void patchDesativarComAdminDeveRetornar200() throws Exception {
        mockMvc.perform(patch("/api/usuario/" + idAdmin + "/desativar")
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk());
    }

    @Test
    void patchDesativarUsuarioInexistenteDeveRetornar404() throws Exception {
        mockMvc.perform(patch("/api/usuario/9999/desativar")
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isNotFound());
    }

    @Test
    void patchDesativarSemTokenDeveRetornar403() throws Exception {
        mockMvc.perform(patch("/api/usuario/1/desativar"))
                .andExpect(status().isForbidden());
    }

    // ── POST /api/usuario — validações ────────────────────────────────────────

    @Test
    void postUsuarioComEmailDuplicadoDeveRetornarErro() throws Exception {
        // Primeiro cadastro
        mockMvc.perform(post("/api/usuario")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "nome": "Duplicado",
                          "email": "duplicado@test.com",
                          "senha": "senha123",
                          "dataNascimento": "1990-01-01",
                          "role": "CLIENTE"
                        }
                        """));

        // Segundo cadastro com mesmo email deve falhar
        mockMvc.perform(post("/api/usuario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Duplicado 2",
                                  "email": "duplicado@test.com",
                                  "senha": "senha123",
                                  "dataNascimento": "1990-01-01",
                                  "role": "CLIENTE"
                                }
                                """))
                .andExpect(status().is5xxServerError());
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private Long buscarIdAdmin() throws Exception {
        return buscarIdPorEmail("admin.me@test.com");
    }

    private Long buscarIdPorEmail(String email) throws Exception {
        var result = mockMvc.perform(get("/api/usuario")
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andReturn();
        var usuarios = new com.fasterxml.jackson.databind.ObjectMapper()
                .readTree(result.getResponse().getContentAsString());
        for (var u : usuarios) {
            if (email.equals(u.get("email").asText())) {
                return u.get("id").asLong();
            }
        }
        throw new RuntimeException("Usuário não encontrado: " + email);
    }
}
