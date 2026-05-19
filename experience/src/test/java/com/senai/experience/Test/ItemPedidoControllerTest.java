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
 * Testes de integração para ItemPedidoController — /api/itens-pedido
 *
 * ItemPedido referencia um Produto existente. O endpoint não exige pedido
 * vinculado para criação (o mapper não seta pedido, apenas produto).
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ItemPedidoControllerTest extends PostgresTestContainer {

    @Autowired MockMvc mockMvc;
    private final ObjectMapper mapper = new ObjectMapper();

    private String tokenAdmin;
    private Long idProduto;

    @BeforeEach
    void setup() throws Exception {
        tokenAdmin = AuthHelper.obterToken(mockMvc, "admin.item@test.com", "senha123", "ADMIN");
        idProduto  = criarProduto();
    }

    // ── GET /api/itens-pedido ─────────────────────────────────────────────────

    @Test
    void getItensSemTokenDeveRetornar403() throws Exception {
        mockMvc.perform(get("/api/itens-pedido"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getItensComTokenDeveRetornar200() throws Exception {
        mockMvc.perform(get("/api/itens-pedido")
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    // ── POST /api/itens-pedido ────────────────────────────────────────────────

    @Test
    void postItemPedidoComTokenDeveRetornar201() throws Exception {
        mockMvc.perform(post("/api/itens-pedido")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"idProduto": %d, "quantidade": 2}
                                """.formatted(idProduto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.quantidade").value(2))
                .andExpect(jsonPath("$.idProduto").value(idProduto));
    }

    @Test
    void postItemPedidoSemTokenDeveRetornar403() throws Exception {
        mockMvc.perform(post("/api/itens-pedido")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"idProduto": 1, "quantidade": 1}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void postItemPedidoComProdutoInexistenteDeveRetornarErro() throws Exception {
        mockMvc.perform(post("/api/itens-pedido")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"idProduto": 9999, "quantidade": 1}
                                """))
                .andExpect(status().is5xxServerError());
    }

    // ── GET /api/itens-pedido/{id} ────────────────────────────────────────────

    @Test
    void getItemPorIdExistenteDeveRetornar200() throws Exception {
        Long id = criarItem();

        mockMvc.perform(get("/api/itens-pedido/" + id)
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idItemPedido").value(id));
    }

    @Test
    void getItemPorIdInexistenteDeveRetornar404() throws Exception {
        mockMvc.perform(get("/api/itens-pedido/9999")
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isNotFound());
    }

    // ── PUT /api/itens-pedido/{id} ────────────────────────────────────────────

    @Test
    void putItemComTokenDeveRetornar200() throws Exception {
        Long id = criarItem();

        mockMvc.perform(put("/api/itens-pedido/" + id)
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"idProduto": %d, "quantidade": 5}
                                """.formatted(idProduto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantidade").value(5));
    }

    @Test
    void putItemInexistenteDeveRetornar404() throws Exception {
        mockMvc.perform(put("/api/itens-pedido/9999")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"idProduto": %d, "quantidade": 1}
                                """.formatted(idProduto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void putItemSemTokenDeveRetornar403() throws Exception {
        mockMvc.perform(put("/api/itens-pedido/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"idProduto": 1, "quantidade": 1}
                                """))
                .andExpect(status().isForbidden());
    }

    // ── DELETE /api/itens-pedido/{id} ─────────────────────────────────────────

    @Test
    void deleteItemComTokenDeveRetornar204() throws Exception {
        Long id = criarItem();

        mockMvc.perform(delete("/api/itens-pedido/" + id)
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteItemSemTokenDeveRetornar403() throws Exception {
        mockMvc.perform(delete("/api/itens-pedido/1"))
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
        return mapper.readTree(result.getResponse().getContentAsString()).get("idProduto").asLong();
    }

    private Long criarItem() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/itens-pedido")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"idProduto": %d, "quantidade": 2}
                                """.formatted(idProduto)))
                .andReturn();
        return mapper.readTree(result.getResponse().getContentAsString()).get("idItemPedido").asLong();
    }
}
