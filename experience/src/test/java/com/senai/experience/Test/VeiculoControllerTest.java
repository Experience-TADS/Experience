package com.senai.experience.Test;


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
class VeiculoControllerTest extends PostgresTestContainer {

    @Autowired MockMvc mockMvc;
    private final ObjectMapper mapper = new ObjectMapper();

    private String tokenAdmin;
    private String tokenCliente;
    private Long idProduto;

    @BeforeEach
    void setup() throws Exception {
        tokenAdmin   = AuthHelper.obterToken(mockMvc, "admin.veiculo@test.com",   "senha123", "ADMIN");
        tokenCliente = AuthHelper.obterToken(mockMvc, "cliente.veiculo@test.com", "senha123", "CLIENTE");
        idProduto    = criarProduto();
    }

    // ── GET /api/veiculo ──────────────────────────────────────────────────────

    @Test
    void getVeiculosSemTokenDeveRetornar403() throws Exception {
        mockMvc.perform(get("/api/veiculo"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getVeiculosComTokenDeveRetornar200() throws Exception {
        mockMvc.perform(get("/api/veiculo")
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    // ── POST /api/veiculo ─────────────────────────────────────────────────────

    @Test
    void postVeiculoComAdminDeveRetornar201() throws Exception {
        mockMvc.perform(post("/api/veiculo")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"idProduto":%d,"chassi":12345,"statusVeiculo":"AGUARDANDO"}
                                """.formatted(idProduto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.chassi").value(12345));
    }

    @Test
    void postVeiculoSemTokenDeveRetornar403() throws Exception {
        mockMvc.perform(post("/api/veiculo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"idProduto":%d,"chassi":12345,"statusVeiculo":"AGUARDANDO"}
                                """.formatted(idProduto)))
                .andExpect(status().isForbidden());
    }

    // ── GET /api/veiculo/{id} ─────────────────────────────────────────────────

    @Test
    void getVeiculoPorIdExistenteDeveRetornar200() throws Exception {
        Long id = criarVeiculo();

        mockMvc.perform(get("/api/veiculo/" + id)
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
    }

    @Test
    void getVeiculoPorIdInexistenteDeveRetornar404() throws Exception {
        mockMvc.perform(get("/api/veiculo/9999")
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isNotFound());
    }

    // ── PUT /api/veiculo/{id} ─────────────────────────────────────────────────

    @Test
    void putVeiculoComAdminDeveRetornar200() throws Exception {
        Long id = criarVeiculo();

        mockMvc.perform(put("/api/veiculo/" + id)
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"idProduto":%d,"chassi":99999,"statusVeiculo":"AGUARDANDO"}
                                """.formatted(idProduto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.chassi").value(99999));
    }

    // ── DELETE /api/veiculo/{id} ──────────────────────────────────────────────

    @Test
    void deleteVeiculoComAdminDeveRetornar204() throws Exception {
        Long id = criarVeiculo();

        mockMvc.perform(delete("/api/veiculo/" + id)
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isNoContent());
    }

    // ── GET /api/veiculo/{id}/status ──────────────────────────────────────────

    @Test
    void getStatusHistoricoComTokenDeveRetornar200() throws Exception {
        Long id = criarVeiculo();

        mockMvc.perform(get("/api/veiculo/" + id + "/status")
                        .header("Authorization", "Bearer " + tokenCliente))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getStatusHistoricoSemTokenDeveRetornar403() throws Exception {
        mockMvc.perform(get("/api/veiculo/1/status"))
                .andExpect(status().isForbidden());
    }

    // ── POST /api/veiculo/{id}/status ─────────────────────────────────────────

    @Test
    void postStatusComAdminDeveAvancarStatus() throws Exception {
        Long id = criarVeiculo();

        mockMvc.perform(post("/api/veiculo/" + id + "/status")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"MONTAGEM_ESTRUTURAL\""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("MONTAGEM_ESTRUTURAL"));
    }

    @Test
    void postStatusComClienteDeveRetornar403() throws Exception {
        Long id = criarVeiculo();

        mockMvc.perform(post("/api/veiculo/" + id + "/status")
                        .header("Authorization", "Bearer " + tokenCliente)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"MONTAGEM_ESTRUTURAL\""))
                .andExpect(status().isForbidden());
    }

    @Test
    void postStatusTransicaoInvalidaDeveRetornar500() throws Exception {
        Long id = criarVeiculo();

        // Tenta ir de AGUARDANDO direto para PINTURA (inválido)
        mockMvc.perform(post("/api/veiculo/" + id + "/status")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"PINTURA\""))
                .andExpect(status().is5xxServerError());
    }

    // ── GET /api/veiculo/chassi/{chassi} ─────────────────────────────────────

    @Test
    void getVeiculoPorChassiExistenteDeveRetornar200() throws Exception {
        criarVeiculo(); // cria com chassi 12345

        mockMvc.perform(get("/api/veiculo/chassi/12345")
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.chassi").value(12345));
    }

    @Test
    void getVeiculoPorChassiInexistenteDeveRetornar404() throws Exception {
        mockMvc.perform(get("/api/veiculo/chassi/99999")
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isNotFound());
    }

    @Test
    void getVeiculoPorChassiSemTokenDeveRetornar403() throws Exception {
        mockMvc.perform(get("/api/veiculo/chassi/12345"))
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

    private Long criarVeiculo() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/veiculo")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"idProduto":%d,"chassi":12345,"statusVeiculo":"AGUARDANDO"}
                                """.formatted(idProduto)))
                .andReturn();
        return mapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }
}
