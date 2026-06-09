package com.senai.experience.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class TelefoneControllerTest extends PostgresTestContainer {

    @Autowired MockMvc mockMvc;
    private final ObjectMapper mapper = new ObjectMapper();

    private String tokenAdmin;
    private Long idUsuario;

    @BeforeEach
    void setup() throws Exception {
        tokenAdmin = AuthHelper.obterToken(mockMvc, "admin.telefone@test.com", "senha123", "ADMIN");
        idUsuario  = buscarIdPorEmail("admin.telefone@test.com");
    }

    // ── GET /api/telefones ────────────────────────────────────────────────────

    @Test
    void getTelefonesSemTokenDeveRetornar403() throws Exception {
        mockMvc.perform(get("/api/telefones"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getTelefonesComTokenDeveRetornar200() throws Exception {
        mockMvc.perform(get("/api/telefones")
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    // ── POST /api/telefones ───────────────────────────────────────────────────

    @Test
    void postTelefoneComTokenDeveRetornar201() throws Exception {
        mockMvc.perform(post("/api/telefones")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"numero": "11999990000", "idUsuario": %d}
                                """.formatted(idUsuario)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numero").value("11999990000"));
    }

    @Test
    void postTelefoneSemTokenDeveRetornar403() throws Exception {
        mockMvc.perform(post("/api/telefones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"numero": "11999990000", "idUsuario": 1}
                                """))
                .andExpect(status().isForbidden());
    }

    // ── GET /api/telefones/{id} ───────────────────────────────────────────────

    @Test
    void getTelefonePorIdExistenteDeveRetornar200() throws Exception {
        Long id = criarTelefone();

        mockMvc.perform(get("/api/telefones/" + id)
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
    }

    @Test
    void getTelefonePorIdInexistenteDeveRetornar404() throws Exception {
        mockMvc.perform(get("/api/telefones/9999")
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isNotFound());
    }

    // ── PUT /api/telefones/{id} ───────────────────────────────────────────────

    @Test
    void putTelefoneComTokenDeveRetornar200() throws Exception {
        Long id = criarTelefone();

        mockMvc.perform(put("/api/telefones/" + id)
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"numero": "11888880000", "idUsuario": %d}
                                """.formatted(idUsuario)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numero").value("11888880000"));
    }

    @Test
    void putTelefoneInexistenteDeveRetornar404() throws Exception {
        mockMvc.perform(put("/api/telefones/9999")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"numero": "11888880000", "idUsuario": %d}
                                """.formatted(idUsuario)))
                .andExpect(status().isNotFound());
    }

    // ── DELETE /api/telefones/{id} ────────────────────────────────────────────

    @Test
    void deleteTelefoneComTokenDeveRetornar204() throws Exception {
        Long id = criarTelefone();

        mockMvc.perform(delete("/api/telefones/" + id)
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteTelefoneSemTokenDeveRetornar403() throws Exception {
        mockMvc.perform(delete("/api/telefones/1"))
                .andExpect(status().isForbidden());
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private Long criarTelefone() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/telefones")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"numero": "11999990000", "idUsuario": %d}
                                """.formatted(idUsuario)))
                .andReturn();
        return mapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }

    private Long buscarIdPorEmail(String email) throws Exception {
        MvcResult result = mockMvc.perform(get("/api/usuario")
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andReturn();
        JsonNode usuarios = mapper.readTree(result.getResponse().getContentAsString());
        for (JsonNode u : usuarios) {
            if (email.equals(u.get("email").asText())) {
                return u.get("id").asLong();
            }
        }
        throw new RuntimeException("Usuário não encontrado: " + email);
    }
}
