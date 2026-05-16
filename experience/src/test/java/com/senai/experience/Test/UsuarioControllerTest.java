package com.senai.experience.Test;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


import com.senai.experience.config.PostgresTestContainer;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UsuarioControllerTest extends PostgresTestContainer {

    @Autowired MockMvc mockMvc;

    @BeforeEach
    void criarUsuarioAdmin() throws Exception {
    mockMvc.perform(post("/api/usuario")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "nome": "Admin Teste",
              "email": "admin@test.com",
              "senha": "senha123",
              "dataNascimento": "1990-01-01",
              "role": "ADMIN"
            }
        """));
    }

    @Test
    void postLoginComCredenciaisValidasDeveRetornarToken() throws Exception {
        mockMvc.perform(post("/api/usuario/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"email": "admin@test.com", "senha": "senha123"}
            """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").exists());
    }


    @Test
    void postLoginComSenhaErradaDeveRetornar401() throws Exception {
        mockMvc.perform(post("/api/usuario/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"email": "admin@test.com", "senha": "senhaErrada"}
            """))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void getUsuariosSemTokenDeveRetornar403() throws Exception {
        mockMvc.perform(get("/api/usuario"))
            .andExpect(status().isForbidden());
    }

}

