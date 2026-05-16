package com.senai.experience.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
class PedidoControllerTest extends PostgresTestContainer {

    @Autowired MockMvc mockMvc;
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private String tokenAdmin;
    private String tokenVendedor;
    private String tokenCliente;
    private Long idCliente;
    private Long idVendedor;

    @BeforeEach
    void setup() throws Exception {
        tokenAdmin   = AuthHelper.obterToken(mockMvc, "admin.pedido@test.com",   "senha123", "ADMIN");
        tokenVendedor = AuthHelper.obterToken(mockMvc, "vendedor.pedido@test.com", "senha123", "VENDEDOR");
        tokenCliente = AuthHelper.obterToken(mockMvc, "cliente.pedido@test.com", "senha123", "CLIENTE");

        idCliente  = buscarIdPorEmail("cliente.pedido@test.com");
        idVendedor = buscarIdPorEmail("vendedor.pedido@test.com");
    }

    // ── GET /api/pedido ───────────────────────────────────────────────────────

    @Test
    void getPedidosSemTokenDeveRetornar403() throws Exception {
        mockMvc.perform(get("/api/pedido"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getPedidosComVendedorDeveRetornar200() throws Exception {
        mockMvc.perform(get("/api/pedido")
                        .header("Authorization", "Bearer " + tokenVendedor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getPedidosComAdminDeveRetornar200() throws Exception {
        mockMvc.perform(get("/api/pedido")
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk());
    }

    @Test
    void getPedidosComClienteDeveRetornar403() throws Exception {
        // Cliente não tem acesso a /api/pedido (apenas /api/pedido/meus-pedidos)
        mockMvc.perform(get("/api/pedido")
                        .header("Authorization", "Bearer " + tokenCliente))
                .andExpect(status().isForbidden());
    }

    // ── POST /api/pedido ──────────────────────────────────────────────────────

    @Test
    void postPedidoComVendedorDeveRetornar201() throws Exception {
        mockMvc.perform(post("/api/pedido")
                        .header("Authorization", "Bearer " + tokenVendedor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "idCliente": %d,
                                  "idVendedor": %d,
                                  "dataPedido": "2024-01-15T10:00:00",
                                  "valorTotal": 85000.00
                                }
                                """.formatted(idCliente, idVendedor)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.valorTotal").value(85000.00));
    }

    @Test
    void postPedidoComClienteInexistenteDeveRetornar500() throws Exception {
        mockMvc.perform(post("/api/pedido")
                        .header("Authorization", "Bearer " + tokenVendedor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "idCliente": 9999,
                                  "idVendedor": %d,
                                  "dataPedido": "2024-01-15T10:00:00",
                                  "valorTotal": 85000.00
                                }
                                """.formatted(idVendedor)))
                .andExpect(status().is5xxServerError());
    }

    // ── GET /api/pedido/{id} ──────────────────────────────────────────────────

    @Test
    void getPedidoPorIdExistenteDeveRetornar200() throws Exception {
        Long id = criarPedido();

        mockMvc.perform(get("/api/pedido/" + id)
                        .header("Authorization", "Bearer " + tokenVendedor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
    }

    @Test
    void getPedidoPorIdInexistenteDeveRetornar404() throws Exception {
        mockMvc.perform(get("/api/pedido/9999")
                        .header("Authorization", "Bearer " + tokenVendedor))
                .andExpect(status().isNotFound());
    }

    // ── PUT /api/pedido/{id} ──────────────────────────────────────────────────

    @Test
    void putPedidoComVendedorDeveRetornar200() throws Exception {
        Long id = criarPedido();

        mockMvc.perform(put("/api/pedido/" + id)
                        .header("Authorization", "Bearer " + tokenVendedor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "idCliente": %d,
                                  "idVendedor": %d,
                                  "dataPedido": "2024-02-01T10:00:00",
                                  "valorTotal": 90000.00
                                }
                                """.formatted(idCliente, idVendedor)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valorTotal").value(90000.00));
    }

    @Test
    void putPedidoInexistenteDeveRetornar404() throws Exception {
        mockMvc.perform(put("/api/pedido/9999")
                        .header("Authorization", "Bearer " + tokenVendedor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "idCliente": %d,
                                  "idVendedor": %d,
                                  "dataPedido": "2024-02-01T10:00:00",
                                  "valorTotal": 90000.00
                                }
                                """.formatted(idCliente, idVendedor)))
                .andExpect(status().isNotFound());
    }

    // ── DELETE /api/pedido/{id} ───────────────────────────────────────────────

    @Test
    void deletePedidoComAdminDeveRetornar204() throws Exception {
        Long id = criarPedido();

        mockMvc.perform(delete("/api/pedido/" + id)
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isNoContent());
    }

    // ── GET /api/pedido/meus-pedidos ──────────────────────────────────────────

    @Test
    void getMeusPedidosComClienteDeveRetornar200() throws Exception {
        mockMvc.perform(get("/api/pedido/meus-pedidos")
                        .header("Authorization", "Bearer " + tokenCliente))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getMeusPedidosComVendedorDeveRetornar200() throws Exception {
        mockMvc.perform(get("/api/pedido/meus-pedidos")
                        .header("Authorization", "Bearer " + tokenVendedor))
                .andExpect(status().isOk());
    }

    @Test
    void getMeusPedidosSemTokenDeveRetornar403() throws Exception {
        mockMvc.perform(get("/api/pedido/meus-pedidos"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getMeusPedidosClienteRetornaApenasOsProprios() throws Exception {
        // Cria um pedido vinculado ao cliente
        criarPedido();

        MvcResult result = mockMvc.perform(get("/api/pedido/meus-pedidos")
                        .header("Authorization", "Bearer " + tokenCliente))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode pedidos = mapper.readTree(result.getResponse().getContentAsString());
        // Todos os pedidos retornados devem ter o cliente correto
        for (JsonNode pedido : pedidos) {
            org.assertj.core.api.Assertions.assertThat(
                    pedido.get("idCliente").get("id").asLong()
            ).isEqualTo(idCliente);
        }
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private Long criarPedido() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/pedido")
                        .header("Authorization", "Bearer " + tokenVendedor)
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
        JsonNode usuarios = mapper.readTree(result.getResponse().getContentAsString());
        for (JsonNode u : usuarios) {
            if (email.equals(u.get("email").asText())) {
                return u.get("id").asLong();
            }
        }
        throw new RuntimeException("Usuário não encontrado: " + email);
    }
}
