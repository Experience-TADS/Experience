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
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ProdutoControllerTest extends PostgresTestContainer {

    @Autowired MockMvc mockMvc;
    private final ObjectMapper mapper = new ObjectMapper();

    private String tokenAdmin;
    private String tokenCliente;

    @BeforeEach
    void setup() throws Exception {
        tokenAdmin   = AuthHelper.obterToken(mockMvc, "admin.produto@test.com",   "senha123", "ADMIN");
        tokenCliente = AuthHelper.obterToken(mockMvc, "cliente.produto@test.com", "senha123", "CLIENTE");
    }

    // ── GET /api/produto ──────────────────────────────────────────────────────

    @Test
    void getProdutosSemTokenDeveRetornar403() throws Exception {
        mockMvc.perform(get("/api/produto"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getProdutosComTokenDeveRetornar200() throws Exception {
        mockMvc.perform(get("/api/produto")
                        .header("Authorization", "Bearer " + tokenCliente))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    // ── POST /api/produto ─────────────────────────────────────────────────────

    @Test
    void postProdutoComAdminDeveRetornar201() throws Exception {
        mockMvc.perform(post("/api/produto")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"modelo":"Civic","cor":"Preto","versao":"EXL","ano":2024}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.modelo").value("Civic"));
    }

    @Test
    void postProdutoComClienteDeveRetornar403() throws Exception {
        mockMvc.perform(post("/api/produto")
                        .header("Authorization", "Bearer " + tokenCliente)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"modelo":"Civic","cor":"Preto","versao":"EXL","ano":2024}
                                """))
                .andExpect(status().isForbidden());
    }

    // ── GET /api/produto/{id} ─────────────────────────────────────────────────

    @Test
    void getProdutoPorIdExistenteDeveRetornar200() throws Exception {
        Long id = criarProduto();

        mockMvc.perform(get("/api/produto/" + id)
                        .header("Authorization", "Bearer " + tokenCliente))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idProduto").value(id));
    }

    @Test
    void getProdutoPorIdInexistenteDeveRetornar404() throws Exception {
        mockMvc.perform(get("/api/produto/9999")
                        .header("Authorization", "Bearer " + tokenCliente))
                .andExpect(status().isNotFound());
    }

    // ── PUT /api/produto/{id} ─────────────────────────────────────────────────

    @Test
    void putProdutoComAdminDeveRetornar200() throws Exception {
        Long id = criarProduto();

        mockMvc.perform(put("/api/produto/" + id)
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"modelo":"Corolla","cor":"Branco","versao":"XEI","ano":2025}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modelo").value("Corolla"));
    }

    @Test
    void putProdutoComClienteDeveRetornar403() throws Exception {
        Long id = criarProduto();

        mockMvc.perform(put("/api/produto/" + id)
                        .header("Authorization", "Bearer " + tokenCliente)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"modelo":"Corolla","cor":"Branco","versao":"XEI","ano":2025}
                                """))
                .andExpect(status().isForbidden());
    }

    // ── DELETE /api/produto/{id} ──────────────────────────────────────────────

    @Test
    void deleteProdutoComAdminDeveRetornar204() throws Exception {
        Long id = criarProduto();

        mockMvc.perform(delete("/api/produto/" + id)
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteProdutoComClienteDeveRetornar403() throws Exception {
        Long id = criarProduto();

        mockMvc.perform(delete("/api/produto/" + id)
                        .header("Authorization", "Bearer " + tokenCliente))
                .andExpect(status().isForbidden());
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private Long criarProduto() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/produto")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"modelo":"Civic","cor":"Preto","versao":"EXL","ano":2024}
                                """))
                .andReturn();
        JsonNode json = mapper.readTree(result.getResponse().getContentAsString());
        return json.get("idProduto").asLong();
    }
}
