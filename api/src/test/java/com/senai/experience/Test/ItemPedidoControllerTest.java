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
    private Long idPedido;

    @BeforeEach
    void setup() throws Exception {
        tokenAdmin   = AuthHelper.obterToken(mockMvc, "admin.item@test.com",    "Senha@123", "ADMIN");
        // Cliente e vendedor são necessários para criar um pedido ao qual os itens se vinculam.
        AuthHelper.obterToken(mockMvc, "cliente.item@test.com",  "Senha@123", "CLIENTE");
        AuthHelper.obterToken(mockMvc, "vendedor.item@test.com", "Senha@123", "VENDEDOR");
        idProduto = criarProduto();
        idPedido  = criarPedido();
    }

    private Long criarPedido() throws Exception {
        Long idCliente  = buscarIdPorEmail("cliente.item@test.com");
        Long idVendedor = buscarIdPorEmail("vendedor.item@test.com");
        MvcResult result = mockMvc.perform(post("/api/pedido")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "idCliente": %d,
                                  "idVendedor": %d,
                                  "dataPedido": "2024-01-15T10:00:00",
                                  "valorTotal": 85000.00
                                }
                                """.formatted(idCliente, idVendedor)))
                .andReturn();
        return mapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }

    private Long buscarIdPorEmail(String email) throws Exception {
        MvcResult result = mockMvc.perform(get("/api/usuario")
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andReturn();
        JsonNode usuarios = mapper.readTree(result.getResponse().getContentAsString()).get("content");
        for (JsonNode u : usuarios) {
            if (email.equals(u.get("email").asText())) {
                return u.get("id").asLong();
            }
        }
        throw new RuntimeException("Usuário não encontrado: " + email);
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
                .andExpect(jsonPath("$.content").isArray());
    }

    // ── POST /api/itens-pedido ────────────────────────────────────────────────

    @Test
    void postItemPedidoComTokenDeveRetornar201() throws Exception {
        mockMvc.perform(post("/api/itens-pedido")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"idPedido": %d, "idProduto": %d, "quantidade": 2}
                                """.formatted(idPedido, idProduto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.quantidade").value(2))
                .andExpect(jsonPath("$.produto.id").value(idProduto));
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
        // Produto inexistente lança RuntimeException, traduzida para 404 pelo handler global.
        mockMvc.perform(post("/api/itens-pedido")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"idPedido": %d, "idProduto": 9999, "quantidade": 1}
                                """.formatted(idPedido)))
                .andExpect(status().isNotFound());
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
                                {"modelo":"Civic","cor":"Preto","versao":"EXL","ano":2024,"preco":149900.00}
                                """))
                .andReturn();
        return mapper.readTree(result.getResponse().getContentAsString()).get("idProduto").asLong();
    }

    private Long criarItem() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/itens-pedido")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"idPedido": %d, "idProduto": %d, "quantidade": 2}
                                """.formatted(idPedido, idProduto)))
                .andReturn();
        return mapper.readTree(result.getResponse().getContentAsString()).get("idItemPedido").asLong();
    }
}
