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
 * Testes de integração para PessoaJuridicaController — /api/pessoaJuridica
 *
 * Valida o contrato DTO: request com campo "senha" (não "senhaHash"),
 * response sem senhaHash, e POST retorna 201.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class PessoaJuridicaControllerTest extends PostgresTestContainer {

    @Autowired MockMvc mockMvc;
    private final ObjectMapper mapper = new ObjectMapper();

    private String tokenAdmin;

    @BeforeEach
    void setup() throws Exception {
        tokenAdmin = AuthHelper.obterToken(mockMvc, "admin.pj@test.com", "Senha@123", "ADMIN");
    }

    private String corpo(String nome, String email, String cnpj, String razaoSocial) {
        return """
                {
                  "nome": "%s",
                  "email": "%s",
                  "senha": "Senha@123",
                  "dataNascimento": "2000-01-01",
                  "cnpj": "%s",
                  "razaoSocial": "%s",
                  "role": "VENDEDOR"
                }
                """.formatted(nome, email, cnpj, razaoSocial);
    }

    // ── POST /api/pessoaJuridica (público) ────────────────────────────────────

    @Test
    void postPessoaJuridicaValidaDeveRetornar201() throws Exception {
        mockMvc.perform(post("/api/pessoaJuridica")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo("Empresa Teste", "empresa@test.com", "11222333000181", "Empresa Teste LTDA")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.cnpj").value("11222333000181"))
                .andExpect(jsonPath("$.razaoSocial").value("Empresa Teste LTDA"))
                .andExpect(jsonPath("$.senhaHash").doesNotExist());
    }

    @Test
    void postPessoaJuridicaComCnpjInvalidoDeveRetornar400() throws Exception {
        mockMvc.perform(post("/api/pessoaJuridica")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo("Empresa Inválida", "invalida@test.com", "11111111111111", "Empresa Inválida LTDA")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void postPessoaJuridicaSemRazaoSocialDeveRetornar400() throws Exception {
        mockMvc.perform(post("/api/pessoaJuridica")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Empresa Sem Razão",
                                  "email": "semrazao@test.com",
                                  "senha": "Senha@123",
                                  "dataNascimento": "2000-01-01",
                                  "cnpj": "11222333000181",
                                  "role": "VENDEDOR"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    // ── GET /api/pessoaJuridica ───────────────────────────────────────────────

    @Test
    void getPessoasJuridicasSemTokenDeveRetornar403() throws Exception {
        mockMvc.perform(get("/api/pessoaJuridica"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getPessoasJuridicasComTokenDeveRetornar200() throws Exception {
        mockMvc.perform(get("/api/pessoaJuridica")
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void responseNaoDeveConterSenhaHash() throws Exception {
        mockMvc.perform(get("/api/pessoaJuridica")
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].senhaHash").doesNotExist());
    }

    // ── GET /api/pessoaJuridica/{id} ──────────────────────────────────────────

    @Test
    void getPessoaJuridicaPorIdExistenteDeveRetornar200() throws Exception {
        Long id = criarPessoaJuridica("empresa.get@test.com", "11222333000181");

        mockMvc.perform(get("/api/pessoaJuridica/" + id)
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.senhaHash").doesNotExist());
    }

    @Test
    void getPessoaJuridicaPorIdInexistenteDeveRetornar404() throws Exception {
        mockMvc.perform(get("/api/pessoaJuridica/9999")
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isNotFound());
    }

    // ── PUT /api/pessoaJuridica/{id} ──────────────────────────────────────────

    @Test
    void putPessoaJuridicaComTokenDeveRetornar200() throws Exception {
        Long id = criarPessoaJuridica("empresa.put@test.com", "11222333000181");

        mockMvc.perform(put("/api/pessoaJuridica/" + id)
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo("Empresa Atualizada", "empresa.put@test.com", "11222333000181", "Empresa Atualizada LTDA")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.razaoSocial").value("Empresa Atualizada LTDA"))
                .andExpect(jsonPath("$.senhaHash").doesNotExist());
    }

    @Test
    void putPessoaJuridicaInexistenteDeveRetornar404() throws Exception {
        mockMvc.perform(put("/api/pessoaJuridica/9999")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo("Ninguém", "ninguem@test.com", "11222333000181", "Ninguém LTDA")))
                .andExpect(status().isNotFound());
    }

    // ── DELETE /api/pessoaJuridica/{id} ──────────────────────────────────────

    @Test
    void deletePessoaJuridicaComTokenDeveRetornar204() throws Exception {
        Long id = criarPessoaJuridica("empresa.delete@test.com", "11222333000181");

        mockMvc.perform(delete("/api/pessoaJuridica/" + id)
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isNoContent());
    }

    @Test
    void deletePessoaJuridicaSemTokenDeveRetornar403() throws Exception {
        mockMvc.perform(delete("/api/pessoaJuridica/1"))
                .andExpect(status().isForbidden());
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private Long criarPessoaJuridica(String email, String cnpj) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/pessoaJuridica")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo("Empresa Teste", email, cnpj, "Empresa Teste LTDA")))
                .andExpect(status().isCreated())
                .andReturn();
        JsonNode json = mapper.readTree(result.getResponse().getContentAsString());
        return json.get("id").asLong();
    }
}
