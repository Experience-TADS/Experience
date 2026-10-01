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
 * Valida o contrato DTO: request com campo "senha" (não "senhaHash"),
 * response sem senhaHash, e POST retorna 201.
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
        tokenAdmin = AuthHelper.obterToken(mockMvc, "admin.pf@test.com", "Senha@123", "ADMIN");
    }

    private String corpo(String nome, String email, String cpf) {
        return """
                {
                  "nome": "%s",
                  "email": "%s",
                  "senha": "Senha@123",
                  "dataNascimento": "1990-05-15",
                  "cpf": "%s",
                  "role": "CLIENTE"
                }
                """.formatted(nome, email, cpf);
    }

    // ── POST /api/pessoaFisica (público) ──────────────────────────────────────

    @Test
    void postPessoaFisicaValidaDeveRetornar201() throws Exception {
        mockMvc.perform(post("/api/pessoaFisica")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo("João Silva", "joao.silva@test.com", "39053344705")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.cpf").value("39053344705"))
                .andExpect(jsonPath("$.senhaHash").doesNotExist());
    }

    @Test
    void postPessoaFisicaComCpfInvalidoDeveRetornar400() throws Exception {
        mockMvc.perform(post("/api/pessoaFisica")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo("CPF Inválido", "cpfinvalido@test.com", "11111111111")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void postPessoaFisicaSemCampoObrigatorioDeveRetornar400() throws Exception {
        // Sem o campo "nome"
        mockMvc.perform(post("/api/pessoaFisica")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "semcampo@test.com",
                                  "senha": "Senha@123",
                                  "dataNascimento": "1990-05-15",
                                  "cpf": "39053344705",
                                  "role": "CLIENTE"
                                }
                                """))
                .andExpect(status().isBadRequest());
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
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void responseNaoDeveConterSenhaHash() throws Exception {
        mockMvc.perform(get("/api/pessoaFisica")
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].senhaHash").doesNotExist());
    }

    // ── GET /api/pessoaFisica/{id} ────────────────────────────────────────────

    @Test
    void getPessoaFisicaPorIdExistenteDeveRetornar200() throws Exception {
        Long id = criarPessoaFisica("joao.get@test.com", "39053344705");

        mockMvc.perform(get("/api/pessoaFisica/" + id)
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.senhaHash").doesNotExist());
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
        Long id = criarPessoaFisica("joao.put@test.com", "39053344705");

        mockMvc.perform(put("/api/pessoaFisica/" + id)
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "João Atualizado",
                                  "email": "joao.put@test.com",
                                  "senha": "NovaSenha@456",
                                  "dataNascimento": "1990-05-15",
                                  "cpf": "39053344705",
                                  "role": "CLIENTE"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("João Atualizado"))
                .andExpect(jsonPath("$.senhaHash").doesNotExist());
    }

    @Test
    void putPessoaFisicaInexistenteDeveRetornar404() throws Exception {
        mockMvc.perform(put("/api/pessoaFisica/9999")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo("Ninguém", "ninguem@test.com", "39053344705")))
                .andExpect(status().isNotFound());
    }

    // ── DELETE /api/pessoaFisica/{id} ─────────────────────────────────────────

    @Test
    void deletePessoaFisicaComTokenDeveRetornar204() throws Exception {
        Long id = criarPessoaFisica("joao.delete@test.com", "39053344705");

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
                        .content(corpo("João Silva", email, cpf)))
                .andExpect(status().isCreated())
                .andReturn();
        JsonNode json = mapper.readTree(result.getResponse().getContentAsString());
        return json.get("id").asLong();
    }
}
