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

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class EnderecoControllerTest extends PostgresTestContainer {

    @Autowired MockMvc mockMvc;
    private final ObjectMapper mapper = new ObjectMapper();

    private String tokenAdmin;
    private Long idUsuario;

    @BeforeEach
    void setup() throws Exception {
        tokenAdmin = AuthHelper.obterToken(mockMvc, "admin.endereco@test.com", "senha123", "ADMIN");
        idUsuario  = buscarIdPorEmail("admin.endereco@test.com");
    }

    // ── GET /api/endereco ─────────────────────────────────────────────────────

    @Test
    void getEnderecosSemTokenDeveRetornar403() throws Exception {
        mockMvc.perform(get("/api/endereco"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getEnderecosComTokenDeveRetornar200() throws Exception {
        mockMvc.perform(get("/api/endereco")
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    // ── POST /api/endereco ────────────────────────────────────────────────────

    @Test
    void postEnderecoComTokenDeveRetornar201() throws Exception {
        mockMvc.perform(post("/api/endereco")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "cep": "01310-100",
                                  "logradouro": "Av. Paulista",
                                  "numero": 1000,
                                  "bairro": "Bela Vista",
                                  "cidade": "São Paulo",
                                  "estado": "SP",
                                  "idUsuario": %d
                                }
                                """.formatted(idUsuario)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.logradouro").value("Av. Paulista"));
    }

    @Test
    void postEnderecoSemTokenDeveRetornar403() throws Exception {
        mockMvc.perform(post("/api/endereco")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"cep":"01310-100","logradouro":"Av. Paulista","numero":1000,
                                 "bairro":"Bela Vista","cidade":"São Paulo","estado":"SP","idUsuario":1}
                                """))
                .andExpect(status().isForbidden());
    }

    // ── GET /api/endereco/{id} ────────────────────────────────────────────────

    @Test
    void getEnderecoPorIdExistenteDeveRetornar200() throws Exception {
        Long id = criarEndereco();

        mockMvc.perform(get("/api/endereco/" + id)
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
    }

    @Test
    void getEnderecoPorIdInexistenteDeveRetornar404() throws Exception {
        mockMvc.perform(get("/api/endereco/9999")
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isNotFound());
    }

    // ── PUT /api/endereco/{id} ────────────────────────────────────────────────

    @Test
    void putEnderecoComTokenDeveRetornar200() throws Exception {
        Long id = criarEndereco();

        mockMvc.perform(put("/api/endereco/" + id)
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "cep": "04538-133",
                                  "logradouro": "Av. Brigadeiro Faria Lima",
                                  "numero": 2000,
                                  "bairro": "Itaim Bibi",
                                  "cidade": "São Paulo",
                                  "estado": "SP",
                                  "idUsuario": %d
                                }
                                """.formatted(idUsuario)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.logradouro").value("Av. Brigadeiro Faria Lima"));
    }

    @Test
    void putEnderecoInexistenteDeveRetornar404() throws Exception {
        mockMvc.perform(put("/api/endereco/9999")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"cep":"01310-100","logradouro":"Rua X","numero":1,
                                 "bairro":"Centro","cidade":"SP","estado":"SP","idUsuario":%d}
                                """.formatted(idUsuario)))
                .andExpect(status().isNotFound());
    }

    // ── DELETE /api/endereco/{id} ─────────────────────────────────────────────

    @Test
    void deleteEnderecoComTokenDeveRetornar204() throws Exception {
        Long id = criarEndereco();

        mockMvc.perform(delete("/api/endereco/" + id)
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteEnderecoSemTokenDeveRetornar403() throws Exception {
        mockMvc.perform(delete("/api/endereco/1"))
                .andExpect(status().isForbidden());
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private Long criarEndereco() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/endereco")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "cep": "01310-100",
                                  "logradouro": "Av. Paulista",
                                  "numero": 1000,
                                  "bairro": "Bela Vista",
                                  "cidade": "São Paulo",
                                  "estado": "SP",
                                  "idUsuario": %d
                                }
                                """.formatted(idUsuario)))
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
