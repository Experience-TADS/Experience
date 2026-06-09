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
 * O endpoint POST /api/pessoaJuridica é público (permitAll no SecurityConfig).
 * Os demais endpoints exigem autenticação.
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
        tokenAdmin = AuthHelper.obterToken(mockMvc, "admin.pj@test.com", "senha123", "ADMIN");
    }

    // ── POST /api/pessoaJuridica (público) ────────────────────────────────────

    @Test
    void postPessoaJuridicaValidaDeveRetornar200() throws Exception {
        mockMvc.perform(post("/api/pessoaJuridica")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Empresa Teste",
                                  "email": "empresa@test.com",
                                  "senhaHash": "senha123",
                                  "dataNascimento": "2000-01-01",
                                  "cnpj": "11.222.333/0001-81",
                                  "razaoSocial": "Empresa Teste LTDA",
                                  "role": "VENDEDOR"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cnpj").value("11.222.333/0001-81"))
                .andExpect(jsonPath("$.razaoSocial").value("Empresa Teste LTDA"));
    }

    @Test
    void postPessoaJuridicaComCnpjInvalidoDeveRetornar400() throws Exception {
        mockMvc.perform(post("/api/pessoaJuridica")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Empresa Inválida",
                                  "email": "invalida@test.com",
                                  "senhaHash": "senha123",
                                  "dataNascimento": "2000-01-01",
                                  "cnpj": "11.111.111/1111-11",
                                  "razaoSocial": "Empresa Inválida LTDA",
                                  "role": "VENDEDOR"
                                }
                                """))
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
                                  "senhaHash": "senha123",
                                  "dataNascimento": "2000-01-01",
                                  "cnpj": "11.222.333/0001-81",
                                  "razaoSocial": "",
                                  "role": "VENDEDOR"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void postPessoaJuridicaComCnpjDuplicadoDeveRetornarErro() throws Exception {
        // Primeiro cadastro
        mockMvc.perform(post("/api/pessoaJuridica")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "nome": "Empresa A",
                          "email": "empresa.a@test.com",
                          "senhaHash": "senha123",
                          "dataNascimento": "2000-01-01",
                          "cnpj": "11.222.333/0001-81",
                          "razaoSocial": "Empresa A LTDA",
                          "role": "VENDEDOR"
                        }
                        """));

        // Segundo com mesmo CNPJ deve falhar
        mockMvc.perform(post("/api/pessoaJuridica")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Empresa B",
                                  "email": "empresa.b@test.com",
                                  "senhaHash": "senha123",
                                  "dataNascimento": "2000-01-01",
                                  "cnpj": "11.222.333/0001-81",
                                  "razaoSocial": "Empresa B LTDA",
                                  "role": "VENDEDOR"
                                }
                                """))
                .andExpect(status().is5xxServerError());
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
                .andExpect(jsonPath("$").isArray());
    }

    // ── GET /api/pessoaJuridica/{id} ──────────────────────────────────────────

    @Test
    void getPessoaJuridicaPorIdExistenteDeveRetornar200() throws Exception {
        Long id = criarPessoaJuridica("empresa.get@test.com", "11.222.333/0001-81");

        mockMvc.perform(get("/api/pessoaJuridica/" + id)
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
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
        Long id = criarPessoaJuridica("empresa.put@test.com", "11.222.333/0001-81");

        mockMvc.perform(put("/api/pessoaJuridica/" + id)
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Empresa Atualizada",
                                  "email": "empresa.put@test.com",
                                  "senhaHash": "novaSenha123",
                                  "dataNascimento": "2000-01-01",
                                  "cnpj": "11.222.333/0001-81",
                                  "razaoSocial": "Empresa Atualizada LTDA",
                                  "role": "VENDEDOR"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.razaoSocial").value("Empresa Atualizada LTDA"));
    }

    @Test
    void putPessoaJuridicaInexistenteDeveRetornar404() throws Exception {
        mockMvc.perform(put("/api/pessoaJuridica/9999")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Ninguém",
                                  "email": "ninguem@test.com",
                                  "senhaHash": "senha123",
                                  "dataNascimento": "2000-01-01",
                                  "cnpj": "11.222.333/0001-81",
                                  "razaoSocial": "Ninguém LTDA",
                                  "role": "VENDEDOR"
                                }
                                """))
                .andExpect(status().isNotFound());
    }

    // ── DELETE /api/pessoaJuridica/{id} ──────────────────────────────────────

    @Test
    void deletePessoaJuridicaComTokenDeveRetornar204() throws Exception {
        Long id = criarPessoaJuridica("empresa.delete@test.com", "11.222.333/0001-81");

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
                        .content("""
                                {
                                  "nome": "Empresa Teste",
                                  "email": "%s",
                                  "senhaHash": "senha123",
                                  "dataNascimento": "2000-01-01",
                                  "cnpj": "%s",
                                  "razaoSocial": "Empresa Teste LTDA",
                                  "role": "VENDEDOR"
                                }
                                """.formatted(email, cnpj)))
                .andReturn();
        JsonNode json = mapper.readTree(result.getResponse().getContentAsString());
        return json.get("id").asLong();
    }
}
