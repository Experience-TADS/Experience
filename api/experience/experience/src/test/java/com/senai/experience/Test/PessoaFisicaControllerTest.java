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

/**
 * Testes de integração para PessoaFisicaController — /api/pessoaFisica
 *
 * O endpoint POST /api/pessoaFisica é público (permitAll no SecurityConfig).
 * Os demais endpoints exigem autenticação.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class PessoaFisicaControllerTest extends PostgresTestContainer {

    @Autowired MockMvc mockMvc;
    private final ObjectMapper mapper = new ObjectMapper();

    private String tokenAdmin;

    @BeforeEach
    void setup() throws Exception {
        tokenAdmin = AuthHelper.obterToken(mockMvc, "admin.pf@test.com", "senha123", "ADMIN");
    }

    // ── POST /api/pessoaFisica (público) ──────────────────────────────────────

    @Test
    void postPessoaFisicaValidaDeveRetornar200() throws Exception {
        mockMvc.perform(post("/api/pessoaFisica")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "João Silva",
                                  "email": "joao.silva@test.com",
                                  "senhaHash": "senha123",
                                  "dataNascimento": "1990-05-15",
                                  "cpf": "529.982.247-25",
                                  "role": "CLIENTE"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cpf").value("529.982.247-25"));
    }

    @Test
    void postPessoaFisicaComCpfInvalidoDeveRetornar400() throws Exception {
        mockMvc.perform(post("/api/pessoaFisica")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "CPF Inválido",
                                  "email": "cpfinvalido@test.com",
                                  "senhaHash": "senha123",
                                  "dataNascimento": "1990-05-15",
                                  "cpf": "111.111.111-11",
                                  "role": "CLIENTE"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void postPessoaFisicaComEmailDuplicadoDeveRetornarErro() throws Exception {
        // Primeiro cadastro
        mockMvc.perform(post("/api/pessoaFisica")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "nome": "Maria Souza",
                          "email": "maria.duplicada@test.com",
                          "senhaHash": "senha123",
                          "dataNascimento": "1985-03-20",
                          "cpf": "529.982.247-25",
                          "role": "CLIENTE"
                        }
                        """));

        // Segundo com mesmo email deve falhar
        mockMvc.perform(post("/api/pessoaFisica")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Maria Souza 2",
                                  "email": "maria.duplicada@test.com",
                                  "senhaHash": "senha123",
                                  "dataNascimento": "1985-03-20",
                                  "cpf": "275.484.830-86",
                                  "role": "CLIENTE"
                                }
                                """))
                .andExpect(status().is5xxServerError());
    }

    // ── GET /api/pessoaFisica ─────────────────────────────────────────────────

    @Test
    void getPessoasFisicasSemTokenDeveRetornar403() throws Exception {
        mockMvc.perform(get("/api/pessoaFisica"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getPessoasFisicasComTokenDeveRetornar200() throws Exception {
        mockMvc.perform(get("/api/pessoaFisica")
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    // ── GET /api/pessoaFisica/{id} ────────────────────────────────────────────

    @Test
    void getPessoaFisicaPorIdExistenteDeveRetornar200() throws Exception {
        Long id = criarPessoaFisica("joao.get@test.com", "529.982.247-25");

        mockMvc.perform(get("/api/pessoaFisica/" + id)
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
    }

    @Test
    void getPessoaFisicaPorIdInexistenteDeveRetornar404() throws Exception {
        mockMvc.perform(get("/api/pessoaFisica/9999")
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isNotFound());
    }

    // ── PUT /api/pessoaFisica/{id} ────────────────────────────────────────────

    @Test
    void putPessoaFisicaComTokenDeveRetornar200() throws Exception {
        Long id = criarPessoaFisica("joao.put@test.com", "529.982.247-25");

        mockMvc.perform(put("/api/pessoaFisica/" + id)
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "João Atualizado",
                                  "email": "joao.put@test.com",
                                  "senhaHash": "novaSenha123",
                                  "dataNascimento": "1990-05-15",
                                  "cpf": "529.982.247-25",
                                  "role": "CLIENTE"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("João Atualizado"));
    }

    @Test
    void putPessoaFisicaInexistenteDeveRetornar404() throws Exception {
        mockMvc.perform(put("/api/pessoaFisica/9999")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Ninguém",
                                  "email": "ninguem@test.com",
                                  "senhaHash": "senha123",
                                  "dataNascimento": "1990-01-01",
                                  "cpf": "529.982.247-25",
                                  "role": "CLIENTE"
                                }
                                """))
                .andExpect(status().isNotFound());
    }

    // ── DELETE /api/pessoaFisica/{id} ─────────────────────────────────────────

    @Test
    void deletePessoaFisicaComTokenDeveRetornar204() throws Exception {
        Long id = criarPessoaFisica("joao.delete@test.com", "529.982.247-25");

        mockMvc.perform(delete("/api/pessoaFisica/" + id)
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isNoContent());
    }

    @Test
    void deletePessoaFisicaSemTokenDeveRetornar403() throws Exception {
        mockMvc.perform(delete("/api/pessoaFisica/1"))
                .andExpect(status().isForbidden());
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private Long criarPessoaFisica(String email, String cpf) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/pessoaFisica")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "João Silva",
                                  "email": "%s",
                                  "senhaHash": "senha123",
                                  "dataNascimento": "1990-05-15",
                                  "cpf": "%s",
                                  "role": "CLIENTE"
                                }
                                """.formatted(email, cpf)))
                .andReturn();
        JsonNode json = mapper.readTree(result.getResponse().getContentAsString());
        return json.get("id").asLong();
    }
}
