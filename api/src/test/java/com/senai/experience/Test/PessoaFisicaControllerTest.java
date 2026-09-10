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
 *
 * Observação: a coluna cpf é VARCHAR(11), então o CPF deve ser enviado
 * sem máscara (11 dígitos). Erros de persistência/validação são traduzidos
 * pelo GlobalHandlerException para 404 (RuntimeException genérica).
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
                                  "cpf": "39053344705",
                                  "role": "CLIENTE"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cpf").value("39053344705"));
    }

    @Test
    void postPessoaFisicaComCpfInvalidoDeveRetornarErro() throws Exception {
        // CPF sem dígito verificador válido: a validação @CPF falha na persistência
        // e o GlobalHandlerException converte a exceção para 404.
        mockMvc.perform(post("/api/pessoaFisica")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "CPF Inválido",
                                  "email": "cpfinvalido@test.com",
                                  "senhaHash": "senha123",
                                  "dataNascimento": "1990-05-15",
                                  "cpf": "11111111111",
                                  "role": "CLIENTE"
                                }
                                """))
                .andExpect(status().isNotFound());
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
                          "cpf": "39053344705",
                          "role": "CLIENTE"
                        }
                        """));

        // Segundo com mesmo email deve falhar (email duplicado → 404 pelo handler)
        mockMvc.perform(post("/api/pessoaFisica")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Maria Souza 2",
                                  "email": "maria.duplicada@test.com",
                                  "senhaHash": "senha123",
                                  "dataNascimento": "1985-03-20",
                                  "cpf": "27548483086",
                                  "role": "CLIENTE"
                                }
                                """))
                .andExpect(status().isNotFound());
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

    // ── GET /api/pessoaFisica/{id} ────────────────────────────────────────────

    @Test
    void getPessoaFisicaPorIdExistenteDeveRetornar200() throws Exception {
        Long id = criarPessoaFisica("joao.get@test.com", "39053344705");

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
        Long id = criarPessoaFisica("joao.put@test.com", "39053344705");

        mockMvc.perform(put("/api/pessoaFisica/" + id)
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "João Atualizado",
                                  "email": "joao.put@test.com",
                                  "senhaHash": "novaSenha123",
                                  "dataNascimento": "1990-05-15",
                                  "cpf": "39053344705",
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
                                  "cpf": "39053344705",
                                  "role": "CLIENTE"
                                }
                                """))
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
